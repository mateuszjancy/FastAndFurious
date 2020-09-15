package com.jancy.mateusz.fnf.catalog

import java.time.LocalDateTime

import cats.implicits._
import com.jancy.mateusz.fnf.catalog.library.Schema
import com.wix.mysql.EmbeddedMysql
import com.wix.mysql.config.{DownloadConfig, MysqldConfig}
import com.wix.mysql.distribution.Version
import org.scalatest.flatspec.AsyncFlatSpecLike
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class ItTest extends AsyncFlatSpecLike with BeforeAndAfterAll with BeforeAndAfterEach with Matchers {
  private var mysqld: Option[EmbeddedMysql] = None

  private val schema              = "fnf"
  private val timeout             = 10 seconds
  private lazy val port           = mysqld.map(_.getConfig.getPort).get
  protected lazy val db: Database = Database.forURL(s"jdbc:mysql://localhost:$port/$schema", "us", "pa")

  override def beforeAll(): Unit = {
    val download = DownloadConfig
      .aDownloadConfig()
      .withCacheDir(System.getProperty("java.io.tmpdir"))
      .build

    val config = MysqldConfig
      .aMysqldConfig(Version.v5_7_19)
      .withFreePort()
      .withUser("us", "pa")
      .build()

    mysqld = EmbeddedMysql.anEmbeddedMysql(config, download).addSchema(schema).start().some
  }

  override def beforeEach(): Unit = Await.result(db.run(DBIO.seq(Schema.ddl.map(_.create): _*)), timeout)


  override def afterEach(): Unit = {
    val clear = db.run(DBIO.seq(Schema.ddl.reverse.map(_.drop): _*))
    clear.foreach(_ => mysqld.foreach(_.stop()))
    Await.result(clear, timeout)
  }

  val testTime = LocalDateTime.of(2020, 9, 15, 7, 41)
}
