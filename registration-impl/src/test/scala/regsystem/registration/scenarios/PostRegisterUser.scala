package regsystem.registration.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.util.Random

/**
  * @author ondrej.dlabola(at)morosystems.cz
  */
object PostRegisterUser {

  private def randomString = Random.alphanumeric.take(10).mkString

  private def newUser =  s"""{
    "groupId": "alpha",
    "userName": "John"
  }"""

  val registerUser = exec(
    http("RegisterUser")
      .post("/registration")
      .header("Content-Type", "application/json")
      .body(StringBody(newUser))
      .asJSON
      .check(status is 200)
  )
}
