package regsystem.registration.util

/**
  * @author ondrej.dlabola(at)morosystems.cz
  */
object Environment {
  val baseURL = scala.util.Properties.envOrElse("baseURL", "http://localhost:9000/api/registration")
  val users = scala.util.Properties.envOrElse("numberOfUsers", "50")
  val maxResponseTime = scala.util.Properties.envOrElse("maxResponseTime", "60000") //in milliseconds
}
