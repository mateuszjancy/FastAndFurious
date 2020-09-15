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

### Quick Public API road-trip
* GET `http://localhost:8080/movie`: All movies, in case of lack of OMDb data only move title and ID will be provided, title always taken from fnf database
* GET `http://localhost:8080/movie/{movieId}`: `movieId` is our ID internally service maps it to retrieve data from OMDb
* GET `http://localhost:8080/catalogue` returns all movie times with prices, catalogue entity needs to be in `Public` status
* GET `http://localhost:8080/catalogue/{movieId}` returns all times with prices for given movie, catalogue entity needs to be in `Public` status
* GET `http://localhost:8080/rating` returns all ratings
* GET `http://localhost:8080/rating/{movieId}` returns all ratings for given `movieId`
* PUT `http://localhost:8080/rating` create new rating (min 1 max 5), `BadRequest` when done for non existing movie, example body: 
```
{
    "userId": "Mateusz",
    "movieId": 2,
    "rating": 4
}
```

### Quick Private API road-trip
Use authenticateBasic for any user name and `p4ssw0rd` pass ;)
* GET `http://localhost:8080/admin/catalogue` returns all movie times with prices in all statuses
* GET `http://localhost:8080/admin/catalogue/{movieId}` returns all movie times with prices in all statuses for given movie
* PUT `http://localhost:8080/admin/catalogue` create catalogue, `BadRequest` when done for non existing movie, example body: 
```
{
  "time" : "2020-09-15T07:41:00",
  "movieId" : 2,
  "price" : 10.0,
  "status" : "Public"
}
```

* POST `http://localhost:8080/admin/catalogue/{catalogueId}` update catalogue, `BadRequest` when done for non existing movie, example body: 
```
{
  "time" : "2020-09-15T07:41:00",
  "movieId" : 2,
  "price" : 10.0,
  "status" : "Public"
}
```

### PR rules
* Code formatted by `.scalafmt.conf`
* Reasonable tests needs to be provided
* All tests are passing

### TODO
* Error logging in OMDb client
* Separate API model from DB model
* Add Swagger doc
* Dockerize it 
* Replace authenticateBasic
