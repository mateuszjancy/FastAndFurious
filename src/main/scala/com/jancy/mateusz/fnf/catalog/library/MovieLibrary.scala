package com.jancy.mateusz.fnf.catalog.library

import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future
import scala.language.postfixOps

class MovieLibrary(db: Database) {
  def insert(movie: Movie): Future[Int]    = db.run(Schema.movies += movie)
  def list: Future[Seq[Movie]]             = db.run(Schema.movies.sortBy(_.title asc).result)
  def get(id: Long): Future[Option[Movie]] = db.run(Schema.movies.filter(_.id === id).result.headOption)
}
