package com.jancy.mateusz.fnf.catalog.service

import com.jancy.mateusz.fnf.catalog.library.{MovieLibrary, Rating, ReviewLibrary}
import com.jancy.mateusz.fnf.catalog.service.RatingService.NewRating

import scala.concurrent.{ExecutionContext, Future}

object RatingService {
  case class NewRating(
      userId: String,
      movieId: Long,
      rating: Short
  )
}

class RatingService(reviewLibrary: ReviewLibrary, movieLibrary: MovieLibrary)(implicit ex: ExecutionContext) {
  def createRating(rating: NewRating): Future[Option[Int]] = {
    movieLibrary.get(rating.movieId).flatMap {
      case Some(_) => reviewLibrary.insert(Rating(0, rating.userId, rating.movieId, rating.rating)).map(Some(_))
      case None    => Future.successful(None)
    }
  }

  def ratings: Future[Seq[Rating]] = reviewLibrary.list

  def ratingsByMovie(movieId: Long): Future[Seq[Rating]] = reviewLibrary.listByMovie(movieId)
}
