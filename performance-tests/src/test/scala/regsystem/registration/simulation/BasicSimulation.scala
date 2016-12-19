package regsystem.registration.simulation

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import net.liftweb.json.Serialization
import regsystem.registration.data.TestDataUtils.{groups, _}
import regsystem.registration.data.{JsonGroup, JsonUser}
import scala.concurrent.duration._

class BasicSimulation extends Simulation {

  private[this] val config = ConfigFactory.load()

  private val repeatCount = config.getInt("scenario.repeat_count")

  private val groupCount = config.getInt("scenario.group_count")

  private val userCount = config.getInt("scenario.user_count")

  private val percentSuccess = config.getInt("scenario.percent_success")

  private val baseURL = config.getString("service.host") + config.getString("service.api_link")

  private val httpProtocol = http.baseURL(baseURL)

  implicit val formats = net.liftweb.json.DefaultFormats

  private def groupFeeder = for (i <- groups.indices) yield {
    Map("group" -> serializeGroup(groups(i)))
  }

  private def userFeeder = for (i <- users.indices) yield {
    Map("user" -> serializeUser(users(i)))
  }

  private def serializeGroup(group: JsonGroup) = Serialization.write(group)

  private def serializeUser(user: JsonUser) = Serialization.write(user)

  val createGroup: ScenarioBuilder = scenario("Groups creation")
    .feed(groupFeeder.circular)
    .exec(
      http("Create group")
        .post("/groups")
        .header("Content-Type", "application/json")
        .body(StringBody("${group}"))
    )

  val registerUsers: ScenarioBuilder = scenario("Users registration")
    .feed(userFeeder)
    .exec(
      http("Register user")
        .post("/registration")
        .header("Content-Type", "application/json")
        .body(StringBody("${user}"))
        .check(status is 200)
    )

  setUp(
    createGroup.inject(atOnceUsers(groupCount)),
    registerUsers.inject(nothingFor(1 seconds), rampUsers(userCount) over (10 seconds))
  )
    .protocols(httpProtocol)
    .assertions(global.successfulRequests.percent.is(percentSuccess))
}
