package io.ggtour.db.migrations

import io.ggtour.account.model.AccountTable
import io.ggtour.common.storage.GGStorage
import org.flywaydb.core.api.migration.{BaseJavaMigration, Context}
import slick.lifted.TableQuery
import io.ggtour.common.storage.GGStorage.GGTourAPI._

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class V1__Create_account_table extends BaseJavaMigration {
  override def migrate(context: Context): Unit = {
    val schema = TableQuery[AccountTable].schema
    println("V1__Create_account_table:")
    schema.createIfNotExists.statements.foreach(println)
    GGStorage.db.run(DBIO.seq(
      schema.createIfNotExists
    )).onComplete {
      case Success(_) =>
        println("Account table created")
      case Failure(error) =>
        println(s"Failed to create account table: ${error.getMessage}")
        throw error
    }
  }
}
