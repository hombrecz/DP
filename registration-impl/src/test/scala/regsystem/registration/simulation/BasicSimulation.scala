package regsystem.registration.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import regsystem.registration.scenarios._

import scala.concurrent.duration._

/**
  * @author ondrej.dlabola(at)morosystems.cz
  */
class BasicSimulation extends Simulation{

  val scenarioName = "UserRegistration"
  val httpProtocol = http.baseURL("http://localhost:9000/api")

  val scn = scenario(scenarioName)
    .exec(
      PostCreateGroup.createGroup,
      PostRegisterUser.registerUser
    )

  setUp(scn.inject(rampUsers(10) over (10 seconds)))
    .protocols(httpProtocol)
}
