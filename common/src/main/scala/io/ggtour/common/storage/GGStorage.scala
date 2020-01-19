package io.ggtour.common.storage

import com.github.tminglei.slickpg._
import slick.basic.Capability
import slick.jdbc.JdbcCapabilities

object GGStorage
    extends ExPostgresProfile
    with PgArraySupport
    with PgDate2Support
    with PgRangeSupport
    with PgDateSupportJoda
    with PgHStoreSupport
    with PgSprayJsonSupport
    with PgSearchSupport
    with PgNetSupport
    with PgLTreeSupport {
  def pgjson =
    "jsonb" // jsonb support is in postgres 9.4.0 onward; for 9.3.x use "io.ggtour.common.json"

  override protected def computeCapabilities: Set[Capability] =
    super.computeCapabilities + JdbcCapabilities.insertOrUpdate

  override val api = new API with ArrayImplicits with DateTimeImplicits
  with JsonImplicits with NetImplicits with LTreeImplicits with RangeImplicits
  with HStoreImplicits with SearchImplicits with SearchAssistants {
    implicit val strListTypeMapper =
      new SimpleArrayJdbcType[String]("text").to(_.toList)
  }

  val db: GGStorage.backend.Database =
    api.Database.forConfig("ggtour.service.io.ggtour.common.storage")
}
