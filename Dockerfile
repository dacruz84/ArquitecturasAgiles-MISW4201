FROM ruby:3.2-alpine
WORKDIR /app
# Instalar dependencias para compilar gemas nativas
RUN apk add --no-cache build-base linux-headers
COPY Gemfile .
RUN bundle install
COPY app.rb .
EXPOSE 3001
CMD ["ruby", "app.rb"]
