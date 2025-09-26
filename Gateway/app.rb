# app.rb
require "sinatra"
require "json"
require "base64"
require_relative "config/config"
require 'logger'
require_relative "helpers/cors"
require_relative "services/jwt_service"
require_relative "services/proxy_service"
require_relative "services/keycloak_service"



require "prometheus/middleware/collector"
require "prometheus/middleware/exporter"

use Prometheus::Middleware::Collector

# Servidor HTTP separado para métricas en puerto 3010
Thread.new do
  require 'socket'
  server = TCPServer.new('0.0.0.0', 3010)
  puts "[METRICS] Servidor de métricas iniciado en puerto 3010"
  
  loop do
    begin
      client = server.accept
      puts "[METRICS] Conexión recibida"
      
      # Leer solo primera línea (GET /metrics HTTP/1.1)
      first_line = client.gets
      puts "[METRICS] Request: #{first_line&.strip}"
      
      # Leer resto de headers hasta línea vacía
      while (line = client.gets) && line.strip != ""
        # Solo consumir headers, no procesarlos
      end
      
      if first_line&.include?('GET /metrics')
        registry = Prometheus::Client.registry
        body = Prometheus::Client::Formats::Text.marshal(registry)
        
        response = "HTTP/1.1 200 OK\r\n"
        response << "Content-Type: text/plain; version=0.0.4; charset=utf-8\r\n"
        response << "Content-Length: #{body.bytesize}\r\n"
        response << "Connection: close\r\n"
        response << "\r\n"
        response << body
        
        client.write(response)
        puts "[METRICS] Respuesta enviada (#{body.bytesize} bytes)"
      else
        client.write("HTTP/1.1 404 Not Found\r\nConnection: close\r\n\r\n")
        puts "[METRICS] Respuesta 404 enviada"
      end
      
    rescue => e
      puts "[METRICS] Error: #{e.message}"
    ensure
      client&.close
      puts "[METRICS] Conexión cerrada"
    end
  end
end


set :bind, "0.0.0.0"
set :port, 3009
set :logging, true

register CORS

VOTING_PROXY = ProxyService.new(AppConfig::VOTING_BASE)
BOOT_LOGGER = Logger.new($stdout)

configure do
  enable :logging
  BOOT_LOGGER.info("[BOOT] Gateway iniciando con KC_BASE=#{AppConfig::KC_BASE} realm=#{AppConfig::REALM} introspect=#{AppConfig::INTROSPECT_URL}")
end

get "/health" do
  content_type :json
  { status: "UP" }.to_json
end

post "/login" do
  content_type :json
  begin
    payload = JSON.parse(request.body.read) rescue {}
    user = payload["username"] || params["username"]
    pass = payload["password"] || params["password"]
    halt 400, { error: "username and password required" }.to_json if user.to_s.empty? || pass.to_s.empty?

    status_, body_ = KeycloakService.login_with_password(username: user, password: pass)
    status status_
    body body_
  rescue => e
    halt 500, { error: e.message }.to_json
  end
end

before "/voting" do
  # Permitir el preflight CORS (OPTIONS) sin exigir Authorization.
  if request.request_method == 'OPTIONS'
    logger.info("[PRE-FLIGHT] /voting OPTIONS desde #{request.env['HTTP_ORIGIN']} headers=#{request.env['HTTP_ACCESS_CONTROL_REQUEST_HEADERS']}")
    # El módulo CORS ya añadió los headers; respondemos vacio 204.
    halt 204
  end

  content_type :json
  logger.info("[AUTH] Verificando token para #{request.request_method} /voting from origin=#{request.env['HTTP_ORIGIN']} auth=#{request.env['HTTP_AUTHORIZATION']&.slice(0,30)}...")
  begin
    data = JwtService.verify!(
      request.env["HTTP_AUTHORIZATION"],
      introspect_url: AppConfig::INTROSPECT_URL,
      client_id:     AppConfig::OIDC_CLIENT_ID,
      client_secret: AppConfig::OIDC_CLIENT_SECRET
    )
    logger.info("[AUTH] Token válido sub=#{data['sub']} active=#{data['active']} scope=#{data['scope']}")

    # Decodificar el JWT sólo para extraer roles (sin volver a validar firma porque introspection ya dijo active=true).
    token = request.env["HTTP_AUTHORIZATION"].split(" ").last
    payload_segment = token.split(".")[1]
    # Añadir padding si faltan '='
    padding = (4 - payload_segment.length % 4) % 4
    payload_segment += '=' * padding
    jwt_payload = JSON.parse(Base64.urlsafe_decode64(payload_segment)) rescue {}
    roles = jwt_payload.dig('realm_access','roles') || []
    logger.info("[AUTHZ] Roles token=#{roles.join(',')}")
    unless roles.include?('LOGISTICA')
      logger.warn("[AUTHZ] Falta rol requerido LOGISTICA -> 403")
      halt 403, { error: 'forbidden', detail: 'Missing role LOGISTICA' }.to_json
    end
  rescue => e
    logger.warn("[AUTH] Falló verificación: #{e.message}")
    halt 401, { error: e.message }.to_json
  end
end

post "/voting" do
  logger.info("[PROXY] Reenviando petición al servicio Voting...")
  status_, headers_, body_ = VOTING_PROXY.forward!(request, "/voting")
  logger.info("[PROXY] Respuesta upstream status=#{status_} content-type=#{headers_["content-type"] || headers_["Content-Type"]}")
  content_type headers_["content-type"] || headers_["Content-Type"] || "application/json"
  status status_
  body body_
end

