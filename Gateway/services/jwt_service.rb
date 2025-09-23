require "faraday"
require "json"
require "uri"

class JwtService
  def self.verify!(auth_header, introspect_url:, client_id:, client_secret:)
    raise "missing Authorization" unless auth_header&.start_with?("Bearer ")
    token = auth_header.split(" ").last
    start = Time.now

    resp = Faraday.post(introspect_url) do |f|
      f.headers["Content-Type"] = "application/x-www-form-urlencoded"
      f.body = URI.encode_www_form(
        client_id: client_id,
        client_secret: client_secret,
        token: token,
        token_type_hint: 'access_token'
      )
    end
  duration = ((Time.now - start) * 1000).round(1)
  body_preview = resp.body&.slice(0,200).to_s.gsub(/\s+/,' ')
  puts "[INTROSPECT] status=#{resp.status} ms=#{duration} body=#{body_preview}"

    raise "introspection_failed (#{resp.status})" unless resp.status == 200
    data = JSON.parse(resp.body) rescue {}

    unless data["active"] == true
      # DEBUG: decodificar payload para entender por qué podría estar inactivo (exp, nbf, typ, aud)
      begin
        header_b64, payload_b64, _sig = token.split(".")
        [header_b64, payload_b64].each { |seg| seg << '=' * ((4 - seg.length % 4) % 4) }
        payload_json = JSON.parse(Base64.urlsafe_decode64(payload_b64)) rescue {}
        now = Time.now.to_i
        exp = payload_json['exp']
        nbf = payload_json['nbf']
        iat = payload_json['iat']
        typ = payload_json['typ']
  aud = payload_json['aud']
        azp = payload_json['azp']
  iss = payload_json['iss']
  roles = payload_json.dig('realm_access','roles')
  resource_access = payload_json['resource_access']&.keys
  puts "[INTROSPECT-DEBUG] inactive_token typ=#{typ} azp=#{azp} aud=#{aud} iss=#{iss} roles=#{roles} resource_access_keys=#{resource_access} exp=#{exp} (delta_exp=#{exp ? exp - now : 'n/a'}) nbf=#{nbf} (delta_nbf=#{nbf ? nbf - now : 'n/a'}) iat=#{iat} now=#{now}"
      rescue => e
        puts "[INTROSPECT-DEBUG] failed_to_decode_payload #{e.class}: #{e.message}"
      end
      raise "invalid_token (active=#{data['active']}; token_use=#{data['token_type']} scope=#{data['scope']})"
    end
    data
  end
end
