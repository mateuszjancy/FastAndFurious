package com.jancy.mateusz.fnf.catalog.api

import akka.http.scaladsl.model.StatusCodes
import com.jancy.mateusz.fnf.catalog.Protocol
import com.jancy.mateusz.fnf.catalog.library.Rating
import com.jancy.mateusz.fnf.catalog.service.RatingService
import com.jancy.mateusz.fnf.catalog.service.RatingService.NewRating

import scala.concurrent.ExecutionContext

class PublicRatingsRoute(ratingService: RatingService) extends Protocol {
  val route =
    path("rating" / LongNumber) { movieId =>
      get {
        onSuccess(ratingService.ratingsByMovie(movieId))(complete(_))
      }
    } ~
      path("rating") {
        get {
          onSuccess(ratingService.ratings)(complete(_))
        } ~
          put {
            entity(as[NewRating]) { newRating =>
              validate(newRating.rating >= 1 && newRating.rating <= 5, "review can have rating only from 1-5") {
                onSuccess(ratingService.createRating(newRating)) {
                  case Some(_) => complete(StatusCodes.OK)
                  case None    => complete(StatusCodes.BadRequest)
                }
              }
            }
          }
      }
}
