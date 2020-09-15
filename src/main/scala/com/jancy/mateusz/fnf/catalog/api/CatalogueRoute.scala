package com.jancy.mateusz.fnf.catalog.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.directives.Credentials
import com.jancy.mateusz.fnf.catalog.Protocol
import com.jancy.mateusz.fnf.catalog.service.CatalogueService
import com.jancy.mateusz.fnf.catalog.service.CatalogueService.NewCatalogue

import scala.concurrent.ExecutionContext

class CatalogueRoute(catalogueService: CatalogueService)(implicit ec: ExecutionContext) extends Protocol {
  def myUserPassAuthenticator(credentials: Credentials): Option[String] = credentials match {
    case p @ Credentials.Provided(id) if p.verify("p4ssw0rd") => Some(id)
    case _                                                    => None
  }

  val route =
    authenticateBasic(realm = "fnf site", myUserPassAuthenticator) { _ =>
      path("admin" / "catalogue" / LongNumber) { catalogueId =>
        get {
          complete(catalogueService.get(catalogueId))
        } ~
          post {
            entity(as[NewCatalogue]) { newCatalogue =>
              onSuccess(catalogueService.update(newCatalogue, catalogueId)) {
                case Some(_) => complete(StatusCodes.OK)
                case None    => complete(StatusCodes.BadRequest)
              }
            }
          }
      } ~
        path("admin" / "catalogue") {
          get {
            complete(catalogueService.list)
          } ~
            put {
              entity(as[NewCatalogue]) { newCatalogue =>
                onSuccess(catalogueService.create(newCatalogue)) {
                  case Some(_) => complete(StatusCodes.OK)
                  case None    => complete(StatusCodes.BadRequest)
                }
              }
            }
        }
    }
}
