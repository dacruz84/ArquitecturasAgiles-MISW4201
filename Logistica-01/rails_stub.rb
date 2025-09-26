# rails_stub.rb
require "pathname"

module Rails
  def self.root
    # raíz del proyecto (ajustado desde este archivo)
    Pathname.new(File.expand_path(__dir__))
  end
end
