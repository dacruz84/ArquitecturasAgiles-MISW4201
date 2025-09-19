FROM ruby:3.2-alpine
WORKDIR /app

RUN apk add --no-cache build-base linux-headers curl

COPY Gemfile .
RUN bundle install

COPY . .

EXPOSE 3000
CMD ["ruby", "app.rb"]
