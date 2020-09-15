package com.jancy.mateusz.fnf.catalog.library

import java.sql.Timestamp
import java.time.LocalDateTime

import com.jancy.mateusz.fnf.catalog.library.CatalogueStatus.CatalogueStatus
import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag

case class Movie(
    id: Long,
    title: String,
    imdbId: String
)

object CatalogueStatus extends Enumeration {
  type CatalogueStatus = Value
  val Draft   = Value
  val Public  = Value
  val Deleted = Value
}

case class Catalogue(
    id: Long,
    time: LocalDateTime,
    movieId: Long,
    price: Double,
    status: CatalogueStatus
)

case class Rating(
    id: Long,
    userId: String,
    movieId: Long,
    rating: Short
)

trait DbTypes {
  implicit val catalogueStatusMapper = MappedColumnType.base[CatalogueStatus, String](_.toString, CatalogueStatus.withName)

  implicit val localDateToDate = MappedColumnType.base[LocalDateTime, Timestamp](
    l => Timestamp.valueOf(l),
    d => d.toLocalDateTime
  )
}

object Schema extends DbTypes {

  class MovieTable(tag: Tag) extends Table[Movie](tag, "MOVIE") {
    val id    = column[Long]("id", O.PrimaryKey, O.AutoInc)
    val title = column[String]("title")
    val imdbId = column[String]("imdbId")

    def * = (id, title, imdbId) <> (Movie.tupled, Movie.unapply)
  }

  val movies = TableQuery[MovieTable]

  class CatalogueTable(tag: Tag) extends Table[Catalogue](tag, "CATALOGUE") {
    val id      = column[Long]("id", O.PrimaryKey, O.AutoInc)
    val time    = column[LocalDateTime]("time")
    val movieId = column[Long]("movie_id")
    val price   = column[Double]("price")
    val status  = column[CatalogueStatus]("status")

    def movie = foreignKey("catalogue_movie_fk", movieId, movies)(_.id)

    def * = (id, time, movieId, price, status) <> (Catalogue.tupled, Catalogue.unapply)
  }

  val catalogue = TableQuery[CatalogueTable]

  class RatingTable(tag: Tag) extends Table[Rating](tag, "RATING") {
    val id       = column[Long]("id", O.PrimaryKey, O.AutoInc)
    val userNick = column[String]("user_nick")
    val movieId  = column[Long]("movie_id")
    val rating   = column[Short]("rating")

    def movie = foreignKey("rating_movie_fk", movieId, movies)(_.id)

    def * = (id, userNick, movieId, rating) <> (Rating.tupled, Rating.unapply)
  }

  val ratings = TableQuery[RatingTable]

  def ddl = Seq(movies, catalogue, ratings).map(_.schema)
}
