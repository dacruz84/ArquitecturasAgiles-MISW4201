require "faraday"
require "json"
require "uri"

class JwtService
  def self.verify!(auth_header, introspect_url:, client_id:, client_secret:)
    raise "missing Authorization" unless auth_header&.start_with?("Bearer ")
    token = auth_header.split(" ").last

    resp = Faraday.post(introspect_url) do |f|
      f.headers["Content-Type"] = "application/x-www-form-urlencoded"
      f.body = URI.encode_www_form(
        client_id: client_id,
        client_secret: client_secret,
        token: token
      )
    end

    raise "introspection_failed (#{resp.status})" unless resp.status == 200
    data = JSON.parse(resp.body) rescue {}

    raise "invalid_token" unless data["active"] == true
    data
  end
end
