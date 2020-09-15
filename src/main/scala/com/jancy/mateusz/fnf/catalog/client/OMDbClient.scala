package com.jancy.mateusz.fnf.catalog.client

import akka.actor.ActorSystem
import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.jancy.mateusz.fnf.catalog.Protocol

import scala.concurrent.{ExecutionContext, Future}

case class OMDbRating(Source: String, Value: String)
case class OMDbMovieDetails(
    Title: String,
    Director: String,
    Awards: String,
    Ratings: List[OMDbRating],
    Metascore: String,
    imdbRating: String,
    imdbVotes: String,
    Production: String
)

trait Client {
  def getMovieDetails(imdbId: String): Future[Option[OMDbMovieDetails]]
}
class OMDbClient(apikey: String)(implicit system: ActorSystem, ec: ExecutionContext) extends Client with Protocol {

  def getMovieDetails(imdbId: String): Future[Option[OMDbMovieDetails]] =
    Http()
      .singleRequest(HttpRequest(uri = s"http://www.omdbapi.com/?apikey=$apikey&i=$imdbId"))
      .flatMap {
        case response if response.status == StatusCodes.OK => Unmarshal(response).to[OMDbMovieDetails].map(Some(_))
        case _                                             => Future.successful(None)
      }
}
