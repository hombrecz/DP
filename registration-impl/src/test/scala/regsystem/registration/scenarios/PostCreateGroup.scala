package regsystem.registration.scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.util.Random

/**
  * @author ondrej.dlabola(at)morosystems.cz
  */
object PostCreateGroup {
  private def randomString = Random.alphanumeric.take(10).mkString

  private def newGroup =  s"""{
    "groupId": "alpha",
    "groupName": "Alpha",
    "capacity": "10"
  }"""

  val createGroup = exec(
    http("CreateGroup")
      .post("/groups")
      .header("Content-Type", "application/json")
      .body(StringBody(newGroup))
      .asJSON
      .check(status is 200)
  )
}
