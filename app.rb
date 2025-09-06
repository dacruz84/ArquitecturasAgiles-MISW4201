# app.rb
require "sinatra"
require "json"

require "prometheus/middleware/collector"
require "prometheus/middleware/exporter"

use Prometheus::Middleware::Collector
use Prometheus::Middleware::Exporter, path: "/metrics"

# 1) Cargar stub de Rails.root
require_relative "./rails_stub"

# 2) Asegurar que Ruby pueda encontrar /services
$LOAD_PATH << File.expand_path("services", __dir__)

# 3) clases
require "logistics/route_planner"
require "logistics/three_opt"

# Config básica
set :environment, ENV.fetch("RACK_ENV", "development")
set :bind, "0.0.0.0"
set :port, ENV.fetch("PORT", 3001)

# Healthcheck
get "/health" do
  content_type :json
  { status: "ok" }.to_json
end

# Endpoint /route 
# Acepta: ?items=P1,P3,P4  ó body JSON { "items": ["P1","P3","P4"] }
post "/route" do
  content_type :json

  # leer items del body JSON o querystring
  payload = request.body.read
  raw = payload.empty? ? params["items"] : JSON.parse(payload)["items"]

  items = normalize_items(raw)
  if items.empty?
    status 422
    return({ error: "items es requerido" }.to_json)
  end

  planner = Logistics::RoutePlanner.new
  result  = planner.compute(items: items)
  order_str = result[:visit_order].join(",")

  status 200
  { route: order_str }.to_json
rescue Logistics::UnknownNodeError, ArgumentError => e
  status 422
  { error: e.message }.to_json
end

# ==== helpers ====
helpers do
  def normalize_items(raw)
    return [] if raw.nil?
    return raw if raw.is_a?(Array)
    raw.to_s.split(",").map(&:strip).reject(&:empty?)
  end
end
