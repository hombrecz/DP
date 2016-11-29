package regsystem.registration.api;

import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import org.pcollections.PSequence;

import akka.Done;
import akka.NotUsed;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public interface RegistrationService extends Service {

    ServiceCall<RegistrationTicket, Done> registerPlayer();
    ServiceCall<Team, Done> createTeam();

    ServiceCall<NotUsed, PSequence<Team>> getTeams();

    @Override
    default Descriptor descriptor() {
        return named("registrationService").withCalls(
                restCall(Method.POST, "/api/registration", this::registerPlayer),
                restCall(Method.POST, "/api/teams", this::createTeam),
                restCall(Method.GET, "/api/teams/all", this::getTeams)
        ).withAutoAcl(true);
    }

}
