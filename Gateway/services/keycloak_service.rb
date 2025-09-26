require "faraday"
require_relative "../config/config"

class KeycloakService
  TOKEN_URL = "#{AppConfig::ISSUER}/protocol/openid-connect/token"

  def self.login_with_password(username:, password:)
    resp = Faraday.post(TOKEN_URL) do |f|
      f.headers["Content-Type"] = "application/x-www-form-urlencoded"
      f.body = URI.encode_www_form(
        grant_type: "password",
        client_id: AppConfig::OIDC_CLIENT_ID,
        client_secret: AppConfig::OIDC_CLIENT_SECRET,
        username: username,
        password: password
      )
    end
    [resp.status, resp.body]
  end

  # refresh token
  def self.refresh(refresh_token:)
    resp = Faraday.post(TOKEN_URL) do |f|
      f.headers["Content-Type"] = "application/x-www-form-urlencoded"
      f.body = URI.encode_www_form(
        grant_type: "refresh_token",
        client_id: AppConfig::OIDC_CLIENT_ID,
        client_secret: AppConfig::OIDC_CLIENT_SECRET,
        refresh_token: refresh_token
      )
    end
    JSON.parse(resp.body)
  end
end
