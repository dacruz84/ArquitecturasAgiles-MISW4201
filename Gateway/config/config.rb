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



  service-ruby:
    build:
      context: ./Logistica-01
    container_name: service-ruby
    image: local/service-ruby:latest
    environment:
      PORT: "3001"
    ports:
      - "3001:3001"
    networks:
      - voting-net
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:3001/health" ]
      interval: 10s
      timeout: 3s
      retries: 5
      start_period: 10s

    cpus: "1"
    mem_limit: "512m"
    mem_reservation: "256m"
