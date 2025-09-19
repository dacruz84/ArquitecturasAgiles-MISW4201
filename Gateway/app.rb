# app.rb
require "sinatra"
require "json"
require_relative "config/config"
require_relative "helpers/cors"
require_relative "services/jwt_service"
require_relative "services/proxy_service"
require_relative "services/keycloak_service"

set :bind, "0.0.0.0"
set :port, 3000

register CORS

VOTING_PROXY = ProxyService.new(AppConfig::VOTING_BASE)

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
  content_type :json
  begin
    JwtService.verify!(
      request.env["HTTP_AUTHORIZATION"],
      introspect_url: AppConfig::INTROSPECT_URL,
      client_id:     AppConfig::OIDC_CLIENT_ID,
      client_secret: AppConfig::OIDC_CLIENT_SECRET
    )
  rescue => e
    halt 401, { error: e.message }.to_json
  end
end

post "/voting" do
  status_, headers_, body_ = VOTING_PROXY.forward!(request, "/voting")
  content_type headers_["content-type"] || headers_["Content-Type"] || "application/json"
  status status_
  body body_
end
