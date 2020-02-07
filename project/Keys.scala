import com.typesafe.config.Config
import sbt._

object Keys {
  lazy val appConfig = settingKey[Config]("Application configuration for piping into plugins, etc.")
}
