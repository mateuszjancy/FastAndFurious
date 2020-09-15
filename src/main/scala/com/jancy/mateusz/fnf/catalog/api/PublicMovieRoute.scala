package com.jancy.mateusz.fnf.catalog.api

import akka.http.scaladsl.model.StatusCodes
import com.jancy.mateusz.fnf.catalog.Protocol
import com.jancy.mateusz.fnf.catalog.service.MoveDetailsService

class PublicMovieRoute(moveDetailsService: MoveDetailsService) extends Protocol {
  val route = path("movie" / LongNumber) { movieId =>
    get {
      onSuccess(moveDetailsService.detailsByMovie(movieId)) {
        case Some(details) => complete(details)
        case None          => complete(StatusCodes.NotFound)
      }
    }
  } ~ path("movie") {
    get {
      onSuccess(moveDetailsService.details)(complete(_))
    }
  }
}
