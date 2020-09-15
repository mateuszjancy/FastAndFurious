package com.jancy.mateusz.fnf.catalog.api

import java.time.LocalDateTime
import java.util.concurrent.Executors

import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.model.{HttpEntity, MediaTypes, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.jancy.mateusz.fnf.catalog.Protocol
import com.jancy.mateusz.fnf.catalog.library._
import com.jancy.mateusz.fnf.catalog.service.CatalogueService
import com.jancy.mateusz.fnf.catalog.service.CatalogueService.NewCatalogue
import io.circe.syntax._
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class CatalogueRouteSpec extends AnyFlatSpecLike with MockFactory with Matchers with ScalatestRouteTest with Protocol {

  val testTime      = LocalDateTime.of(2020, 9, 15, 7, 41)
  implicit val dbEc = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

  "Get /admin/catalogue/$movieId" should "return catalog for given movie" in {
    val movieId   = 1
    val catalogue = Catalogue(0, testTime, movieId, 5, CatalogueStatus.Public)

    val catalogueLibrary: CatalogueLibrary = mock[CatalogueLibrary]
    val movieLibrary: MovieLibrary         = mock[MovieLibrary]
    val catalogueService                   = new CatalogueService(catalogueLibrary, movieLibrary)
    (catalogueLibrary.get _).expects(movieId).returns(Future.successful(Option(catalogue)))

    val tested           = new CatalogueRoute(catalogueService)
    val validCredentials = BasicHttpCredentials("John", "p4ssw0rd")

    Get(s"/admin/catalogue/$movieId") ~> addCredentials(validCredentials) ~>
      tested.route ~> check {
      responseAs[Option[Catalogue]] shouldEqual Some(catalogue)
    }
  }

  "Put /admin/catalogue" should "create catalog" in {
    val movie        = Movie(1, "test", "A")
    val catalogue    = Catalogue(0, testTime, movie.id, 5, CatalogueStatus.Public)
    val newCatalogue = NewCatalogue(testTime, movie.id, 5, CatalogueStatus.Public)

    val catalogueLibrary: CatalogueLibrary = mock[CatalogueLibrary]
    val movieLibrary: MovieLibrary         = mock[MovieLibrary]
    val catalogueService                   = new CatalogueService(catalogueLibrary, movieLibrary)

    (movieLibrary.get _).expects(movie.id).returns(Future.successful(Option(movie)))
    (catalogueLibrary.insertOrUpdate _).expects(catalogue).returns(Future.successful(1))

    val tested           = new CatalogueRoute(catalogueService)
    val validCredentials = BasicHttpCredentials("John", "p4ssw0rd")

    Put("/admin/catalogue").withEntity(HttpEntity(MediaTypes.`application/json`, newCatalogue.asJson.noSpaces)) ~> addCredentials(validCredentials) ~>
      tested.route ~> check {
      response.status shouldBe StatusCodes.OK
    }
  }

  it should "reject catalog for non existing movie" in {
    val newCatalogue = NewCatalogue(testTime, 1, 5, CatalogueStatus.Public)

    val catalogueLibrary: CatalogueLibrary = mock[CatalogueLibrary]
    val movieLibrary: MovieLibrary         = mock[MovieLibrary]
    val catalogueService                   = new CatalogueService(catalogueLibrary, movieLibrary)

    (movieLibrary.get _).expects(newCatalogue.movieId).returns(Future.successful(None))

    val tested           = new CatalogueRoute(catalogueService)
    val validCredentials = BasicHttpCredentials("John", "p4ssw0rd")

    Put("/admin/catalogue").withEntity(HttpEntity(MediaTypes.`application/json`, newCatalogue.asJson.noSpaces)) ~> addCredentials(validCredentials) ~>
      tested.route ~> check {
      response.status shouldBe StatusCodes.BadRequest
    }
  }
}
