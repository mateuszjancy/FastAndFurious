package com.jancy.mateusz.fnf.catalog.library

import slick.jdbc.MySQLProfile.api._
import scala.language.postfixOps

import scala.concurrent.Future

class ReviewLibrary(db: Database) {
  def insert(review: Rating): Future[Int]             = db.run(Schema.ratings += review)
  def list: Future[Seq[Rating]]                       = db.run(Schema.ratings.result)
  def listByMovie(movieId: Long): Future[Seq[Rating]] = db.run(Schema.ratings.filter(_.movieId === movieId).sortBy(_.rating desc).result)
}
