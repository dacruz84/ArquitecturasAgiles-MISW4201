# config/config.rb
module AppConfig
  KC_BASE  = ENV.fetch("KC_BASE", "http://localhost:8080")
  REALM    = ENV.fetch("KC_REALM", "arquitectura")
  ISSUER   = "#{KC_BASE}/realms/#{REALM}"
  JWKS_URL = "#{ISSUER}/protocol/openid-connect/certs"
  OIDC_CLIENT_ID = ENV.fetch("OIDC_CLIENT_ID", "arq-sec")
  OIDC_CLIENT_SECRET = ENV.fetch("OIDC_CLIENT_SECRET", "nzh4zOsFbd7HLwrAUNKL5BFJIUN6A2Cw")
  INTROSPECT_URL = "#{ISSUER}/protocol/openid-connect/token/introspect"

  VOTING_BASE = ENV.fetch("VOTING_BASE", "http://localhost:9000/voting")
end
