package com.jancy.mateusz.fnf.catalog.service

import java.util.concurrent.Executors

import com.jancy.mateusz.fnf.catalog.library.{Movie, MovieLibrary, Rating, ReviewLibrary}
import com.jancy.mateusz.fnf.catalog.service.RatingService.NewRating
import com.jancy.mateusz.fnf.catalog.service.RatingServiceMocks.{movie, movieLibrary, rating, reviewLibrary}
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.{ExecutionContext, Future}

class RatingServiceSpec extends AsyncFlatSpec with Matchers {
  implicit val dbEc = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

  "createRating" should "create raring for existing movie" in {
    val tested = new RatingService(reviewLibrary, movieLibrary)
    val newRating = NewRating(userId = "Test", movieId = movie.id, rating = 2)

    tested.createRating(newRating).map(_ shouldBe Some(1))
  }
}

object RatingServiceMocks extends MockFactory {
  val movie  = Movie(1, "test", "A")
  val rating = Rating(id = 0, userId = "Test", movieId = movie.id, rating = 2)

  val movieLibrary: MovieLibrary   = mock[MovieLibrary]
  val reviewLibrary: ReviewLibrary = mock[ReviewLibrary]
  (movieLibrary.get _).expects(movie.id).returns(Future.successful(Option(movie)))
  (reviewLibrary.insert _).expects(rating).returns(Future.successful(1))
}
