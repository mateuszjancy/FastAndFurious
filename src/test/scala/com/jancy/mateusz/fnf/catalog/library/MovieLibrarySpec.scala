package com.jancy.mateusz.fnf.catalog.library

import com.jancy.mateusz.fnf.catalog.ItTest

import scala.concurrent.Future

class MovieLibrarySpec extends ItTest {

  "MovieLibrary" should "perform insertOrUpdate" in {
    val tested = new MovieLibrary(db)
    val movies: Future[Seq[Movie]] = for {
      _ <- tested.insert(Movie(1, "FnF.1", "A"))
      _ <- tested.insert(Movie(2, "FnF.2", "B"))
      list <- tested.list
    } yield list

    movies.map(_ shouldBe Seq(Movie(1, "FnF.1", "A"), Movie(2, "FnF.2", "B")))
  }
}
