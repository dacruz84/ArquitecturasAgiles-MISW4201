require "faraday"

class ProxyService
  def initialize(base_url)
    @conn = Faraday.new(base_url) { |f| f.request :url_encoded }
    @base_url = base_url
  end

  def forward!(req, path_relativo)
    # reconstruye la URL de destino
    url = path_relativo.dup
    url += "?#{req.query_string}" unless req.query_string.empty?

    headers = { "Content-Type" => req.env["CONTENT_TYPE"] }
    # Propaga identidad
    headers["Authorization"] = req.env["HTTP_AUTHORIZATION"] if req.env["HTTP_AUTHORIZATION"]

    resp = @conn.run_request(
      req.request_method.downcase.to_sym,
      url,
      req.body.read,
      headers
    )

    [resp.status, resp.headers, resp.body]
  rescue Faraday::Error => e
    [502, { "Content-Type" => "application/json" }, { error: "upstream_error", detail: e.message }.to_json]
  end
end
