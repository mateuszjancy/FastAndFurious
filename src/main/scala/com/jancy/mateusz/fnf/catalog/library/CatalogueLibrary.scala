package com.jancy.mateusz.fnf.catalog.library

import java.time.LocalDateTime

import com.jancy.mateusz.fnf.catalog.library.CatalogueStatus.CatalogueStatus
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future
import scala.language.postfixOps

class CatalogueLibrary(db: Database, currentTime: () => LocalDateTime) extends DbTypes {
  def insertOrUpdate(movie: Catalogue): Future[Int] = db.run(Schema.catalogue.insertOrUpdate(movie))

  def list: Future[Seq[Catalogue]] = db.run(Schema.catalogue.sortBy(_.time desc).result)

  def listByStatus(status: CatalogueStatus): Future[Seq[Catalogue]] = db.run(Schema.catalogue.filter(_.status === status).result)

  def listCatalogueByMovieIdAndStatus(movieId: Long, status: CatalogueStatus): Future[Seq[Catalogue]] = {
    val yesterday = currentTime().minusDays(1)
    db.run(Schema.catalogue.filter(_.movieId === movieId).filter(_.status === status).filter(_.time >= yesterday).result)
  }

  def get(id: Long): Future[Option[Catalogue]] = db.run(Schema.catalogue.filter(_.id === id).result.headOption)
}
