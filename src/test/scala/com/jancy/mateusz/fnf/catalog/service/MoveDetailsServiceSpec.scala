package com.jancy.mateusz.fnf.catalog.service

import java.util.concurrent.Executors

import cats.implicits._
import com.jancy.mateusz.fnf.catalog.client.{Client, OMDbMovieDetails, OMDbRating}
import com.jancy.mateusz.fnf.catalog.library.{Movie, MovieLibrary}
import com.jancy.mateusz.fnf.catalog.service.MoveDetailsService.{MovieDetails, MovieDetailsRating}
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.{ExecutionContext, Future}

class MoveDetailsServiceSpec extends AsyncFlatSpec with Matchers {
  implicit val dbEc = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

  "derailsByMovie" should "return details for existing movie" in {

    val tested = new MoveDetailsService(MoveDetailsServiceMocks.omdbClient, MoveDetailsServiceMocks.movieLibrary)

    tested
      .detailsByMovie(MoveDetailsServiceMocks.movie.id)
      .map(_ shouldBe Some(MovieDetails(
        id = MoveDetailsServiceMocks.movie.id,
        title = MoveDetailsServiceMocks.movie.title,
        director = "director".some,
        awards = "awards".some,
        ratings = List.empty[MovieDetailsRating],
        metaScore = "metaScore".some,
        imdbRating = "imdbRating".some,
        imdbVotes = "imdbVotes".some,
        production = "production".some
      )))
  }
}

//Work-around for: https://github.com/paulbutcher/ScalaMock/issues/156
object MoveDetailsServiceMocks extends MockFactory {
  val movie = Movie(1, "test", "A")
  val movieDetails = OMDbMovieDetails(
    Title = "title",
    Director = "director",
    Awards = "awards",
    Ratings = List.empty[OMDbRating],
    Metascore = "metaScore",
    imdbRating = "imdbRating",
    imdbVotes = "imdbVotes",
    Production = "production"
  )

  val omdbClient: Client         = mock[Client]
  val movieLibrary: MovieLibrary = mock[MovieLibrary]

  (movieLibrary.get _).expects(movie.id).returns(Future.successful(Option(movie)))
  (omdbClient.getMovieDetails _).expects(movie.imdbId).returns(Future.successful(Option(movieDetails)))
}
