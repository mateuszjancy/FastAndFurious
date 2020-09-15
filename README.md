# FastAndFurious

# Tech decisions
* Taking time constraint I build app on top of akka http
* `omdbapi` cals are cached by `LoadingCache` from guava for 10 minutes to reduce number of calls
* Tests cover only corner-cases as I ran out of time     
* Service have proper separation of layers (repo, service, api) some APIs are using repos directly in order to reduce boilerplate
* App exposes DB model via REST which will bring issues when future changes occurs (first symptoms are visible, check `NewCatalogue` or `NewRating`), did that in order to save time.
* App can bootstrap itself (DDL, movie inserts), more in HOWTO
* App contain skeleton and example integration test with embedded MySQL
* App is self contain (uses Akka.Http server).

### How to start APP
1) Provide MySQL instance with `fnf` schema
1) Provide following system ENVs (for local DEV check default values in `application.conf`):
* `MYSQL_URL`
* `MYSQL_USER`
* `MYSQL_PASSWORD`
* `APIKEY` OMDb API key
* `BOOTSTRAP` if set to true will create tables in `fnf` schema and load basic movies
1) Run Boot.scala

### TODO
* Error logging in OMDb client
* Separate API model from DB model
* Add Swagger doc
* Dockerize it 
