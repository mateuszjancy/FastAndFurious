package com.jancy.mateusz.fnf.catalog

import akka.http.scaladsl.server.Directives
import com.jancy.mateusz.fnf.catalog.client.{OMDbMovieDetails, OMDbRating}
import com.jancy.mateusz.fnf.catalog.library.{Catalogue, CatalogueStatus, Movie, Rating}
import com.jancy.mateusz.fnf.catalog.service.CatalogueService.NewCatalogue
import com.jancy.mateusz.fnf.catalog.service.MoveDetailsService.{MovieDetails, MovieDetailsRating}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.Decoder.decodeEnumeration
import io.circe.Encoder.encodeEnumeration
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

trait Protocol extends Directives with FailFastCirceSupport {

  implicit lazy val catalogueStatusDecoder = decodeEnumeration(CatalogueStatus)
  implicit lazy val catalogueStatusEncoder = encodeEnumeration(CatalogueStatus)

  implicit lazy val catalogueEncoder = deriveEncoder[Catalogue]
  implicit lazy val catalogueDecoder = deriveDecoder[Catalogue]

  implicit lazy val newCatalogueEncoder = deriveEncoder[NewCatalogue]
  implicit lazy val newCatalogueDecoder = deriveDecoder[NewCatalogue]

  implicit lazy val movieEncoder = deriveEncoder[Movie]
  implicit lazy val movieDecoder = deriveDecoder[Movie]

  implicit lazy val reviewEncoder = deriveEncoder[Rating]
  implicit lazy val reviewDecoder = deriveDecoder[Rating]

  implicit lazy val omdbratingEncoder = deriveEncoder[OMDbRating]
  implicit lazy val omdbRatingDecoder = deriveDecoder[OMDbRating]

  implicit lazy val omdbMovieDetailsEncoder = deriveEncoder[OMDbMovieDetails]
  implicit lazy val omdbMovieDetailsDecoder = deriveDecoder[OMDbMovieDetails]

  implicit lazy val movieDetailsRatingEncoder = deriveEncoder[MovieDetailsRating]
  implicit lazy val movieDetailsRatingDecoder = deriveDecoder[MovieDetailsRating]

  implicit lazy val movieDetailsEncoder = deriveEncoder[MovieDetails]
  implicit lazy val movieDetailsDecoder = deriveDecoder[MovieDetails]
}
