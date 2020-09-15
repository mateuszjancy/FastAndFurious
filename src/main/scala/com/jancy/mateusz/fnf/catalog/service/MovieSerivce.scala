package com.jancy.mateusz.fnf.catalog.service

import com.jancy.mateusz.fnf.catalog.library.{Movie, MovieLibrary, Schema}
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class MovieService(movieLibrary: MovieLibrary, db: Database)(implicit ec: ExecutionContext) {
  def bootstrap(): Unit = {
    val movies = Seq(
      Movie(0, "The Fast and the Furious", "tt0232500"),
      Movie(0, "2 Fast 2 Furious", "tt0322259"),
      Movie(0, "The Fast and the Furious: Tokyo Drift", "tt0463985"),
      Movie(0, "Fast & Furious", "tt1013752"),
      Movie(0, "Fast Five", "tt1596343"),
      Movie(0, "Fast & Furious 6", "tt1905041"),
      Movie(0, "Furious 7", "tt2820852"),
      Movie(0, "The Fate of the Furious", "tt4630562")
    )

    for {
      _ <- db.run(DBIO.seq(Schema.ddl.map(_.create): _*))
      _ <- Future.sequence(movies.map(movieLibrary.insert))
    } yield ()
  }

}
