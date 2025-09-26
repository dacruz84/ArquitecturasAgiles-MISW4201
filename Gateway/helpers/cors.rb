module CORS
  def self.registered(app)
    app.before do
      headers "Access-Control-Allow-Origin" => "*",
              "Access-Control-Allow-Methods" => "GET,POST,OPTIONS",
              "Access-Control-Allow-Headers" => "Authorization,Content-Type"
    end
    app.options "*" do 204 end
  end
end
