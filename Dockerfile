FROM ruby:3.2-alpine
WORKDIR /app

# Dependencias mínimas para compilar gemas
RUN apk add --no-cache build-base linux-headers

# Copiar Gemfile primero para cache
COPY Gemfile .
RUN bundle install

# Copiar todo lo demás (incluye rails_stub.rb, services/, config/)
COPY . .

EXPOSE 3001
CMD ["ruby", "app.rb"]
