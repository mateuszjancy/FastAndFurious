package com.jancy.mateusz.fnf.catalog.service

import java.time.LocalDateTime

import cats.data.OptionT
import com.jancy.mateusz.fnf.catalog.library.CatalogueStatus.CatalogueStatus
import com.jancy.mateusz.fnf.catalog.library.{Catalogue, CatalogueLibrary, MovieLibrary}
import com.jancy.mateusz.fnf.catalog.service.CatalogueService.NewCatalogue

import scala.concurrent.{ExecutionContext, Future}

object CatalogueService {
  case class NewCatalogue(
      time: LocalDateTime,
      movieId: Long,
      price: Double,
      status: CatalogueStatus
  )
}

class CatalogueService(catalogueLibrary: CatalogueLibrary, movieLibrary: MovieLibrary)(implicit ec: ExecutionContext) {

  def create(newCatalogue: NewCatalogue): Future[Option[Int]] = {
    val catalogue = Catalogue(
      id = catalogueLibrary.NewId,
      time = newCatalogue.time,
      movieId = newCatalogue.movieId,
      price = newCatalogue.price,
      status = newCatalogue.status
    )

    createOrUpdate(catalogue)
  }

  def update(newCatalogue: NewCatalogue, id: Long): Future[Option[Int]] = {
    val catalogue = Catalogue(
      id = id,
      time = newCatalogue.time,
      movieId = newCatalogue.movieId,
      price = newCatalogue.price,
      status = newCatalogue.status
    )

    createOrUpdate(catalogue)
  }

  private def createOrUpdate(catalogue: Catalogue): Future[Option[Int]] =
    OptionT(movieLibrary.get(catalogue.movieId)).flatMap { _ =>
      OptionT(catalogueLibrary.insertOrUpdate(catalogue).map(Option(_)))
    }.value

  def get(catalogueId: Long): Future[Option[Catalogue]] = catalogueLibrary.get(catalogueId)
  def list: Future[Seq[Catalogue]]                      = catalogueLibrary.list

}
