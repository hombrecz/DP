package regsystem.registration.simulation

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import net.liftweb.json.Serialization
import regsystem.registration.data.TestDataUtils.{groups, _}
import regsystem.registration.data.{Group, User}

import scala.util.Random

/**
  * @author ondrej.dlabola(at)morosystems.cz
  */
class BasicSimulation extends Simulation {

  private[this] val config = ConfigFactory.load()

  private val repeatCount = config.getInt("scenario.repeat_count")

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

  private def serializeGroup(group: Group) = Serialization.write(group)

  private def serializeUser(user: User) = Serialization.write(user)

  val scn: ScenarioBuilder = scenario("Users registration")
    .feed(groupFeeder.circular)
    .exec(
      http("Create group")
        .post("/groups")
        .header("Content-Type", "application/json")
        .body(StringBody("${group}"))
    )
    .feed(userFeeder)
    .exec(
      http("Register user")
        .post("/registration")
        .header("Content-Type", "application/json")
        .body(StringBody("${user}"))
        .check(status is 200)
    )

  setUp(scn.inject(atOnceUsers(userCount)))
    .protocols(httpProtocol)
    .assertions(global.successfulRequests.percent.is(percentSuccess))
}
