package com.jancy.mateusz.fnf

import java.time.LocalDateTime
import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import com.jancy.mateusz.fnf.catalog.api.{CatalogueRoute, PublicCatalogueRoute, PublicMovieRoute, PublicRatingsRoute}
import com.jancy.mateusz.fnf.catalog.client.OMDbClient
import com.jancy.mateusz.fnf.catalog.library.{CatalogueLibrary, MovieLibrary, ReviewLibrary}
import com.jancy.mateusz.fnf.catalog.service.{CatalogueService, MoveDetailsService, MovieService, RatingService}
import com.typesafe.config.ConfigFactory
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext

object Boot extends App with Directives {
  // Core
  implicit val system = ActorSystem("fnf-system")
  implicit val dbEc   = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
  val cfg             = ConfigFactory.load()

  // Library
  val db: Database     = Database.forConfig("fnf-db")
  val catalogueLibrary = new CatalogueLibrary(db, () => LocalDateTime.now())
  val movieLibrary     = new MovieLibrary(db)
  val reviewLibrary    = new ReviewLibrary(db)

  // Client
  val omdbClient = new OMDbClient(cfg.getString("apikey"))

  // Service
  val moveDetailsService = new MoveDetailsService(omdbClient, movieLibrary)
  val ratingService      = new RatingService(reviewLibrary, movieLibrary)
  val movieService       = new MovieService(movieLibrary, db)
  val catalogueService   = new CatalogueService(catalogueLibrary, movieLibrary)

  // API
  val catalogueRoute       = new CatalogueRoute(catalogueService).route
  val publicCatalogueRoute = new PublicCatalogueRoute(catalogueLibrary).route
  val publicMovieRoute     = new PublicMovieRoute(moveDetailsService).route
  val publicRatingsRoute   = new PublicRatingsRoute(ratingService).route

  // App
  if (cfg.getBoolean("bootstrap")) movieService.bootstrap()

  val bindingFuture = Http()
    .newServerAt("localhost", 8080)
    .bind(catalogueRoute ~ publicCatalogueRoute ~ publicMovieRoute ~ publicRatingsRoute)
}
