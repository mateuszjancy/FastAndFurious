package com.jancy.mateusz.fnf.catalog.api

import com.jancy.mateusz.fnf.catalog.Protocol
import com.jancy.mateusz.fnf.catalog.library.{CatalogueLibrary, CatalogueStatus}

class PublicCatalogueRoute(catalogueLibrary: CatalogueLibrary) extends Protocol {

  private val PublicStatus = CatalogueStatus.Public

  val route =
    path("catalogue") {
      get {
        complete(catalogueLibrary.listByStatus(PublicStatus))
      }
    } ~ path("catalogue" / LongNumber) { movieId =>
      get {
        complete(catalogueLibrary.listCatalogueByMovieIdAndStatus(movieId, PublicStatus))
      }
    }
}
