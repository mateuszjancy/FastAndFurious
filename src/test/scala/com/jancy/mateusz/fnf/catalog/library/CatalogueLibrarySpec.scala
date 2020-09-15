package com.jancy.mateusz.fnf.catalog.library

import java.time.LocalDateTime

import com.jancy.mateusz.fnf.catalog.ItTest

class CatalogueLibrarySpec extends ItTest {
  "listCatalogueByMovieIdAndStatus" should "exclude old or not expected status catalogues" in {
    val tested       = new CatalogueLibrary(db, () => testTime)
    val movieLibrary = new MovieLibrary(db)

    val yesterday = Catalogue(0, testTime.minusDays(1), 1, 5, CatalogueStatus.Public)
    val today     = Catalogue(0, testTime, 1, 5, CatalogueStatus.Public)
    val tomorrow  = Catalogue(0, testTime.plusDays(1), 1, 5, CatalogueStatus.Public)
    for {
      _    <- movieLibrary.insert(Movie(1, "FnF", "A"))
      _    <- tested.insertOrUpdate(Catalogue(0, testTime.minusDays(2), 1, 5, CatalogueStatus.Public))
      _    <- tested.insertOrUpdate(yesterday)
      _    <- tested.insertOrUpdate(today)
      _    <- tested.insertOrUpdate(today.copy(status = CatalogueStatus.Draft))
      _    <- tested.insertOrUpdate(tomorrow)
      list <- tested.listCatalogueByMovieIdAndStatus(1, CatalogueStatus.Public)
    } yield list.map(_.copy(id = 0)) shouldBe Seq(yesterday, today, tomorrow)
  }
}
