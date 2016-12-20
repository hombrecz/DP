package regsystem.registration.api;

import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.api.transport.Method;

import org.pcollections.PSequence;

import akka.Done;
import akka.NotUsed;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;
import static com.lightbend.lagom.javadsl.api.Service.topic;

public interface RegistrationService extends Service {

    ServiceCall<RegistrationTicket, Done> registerUser();

    ServiceCall<Group, Done> createGroup();

    ServiceCall<NotUsed, PSequence<Group>> getGroups();

    Topic registeredUsersTopic();

    @Override
    default Descriptor descriptor() {
        return named("registrationService").withCalls(
                restCall(Method.POST, "/api/registration", this::registerUser),
                restCall(Method.POST, "/api/groups", this::createGroup),
                restCall(Method.GET, "/api/groups/all", this::getGroups)
        )
                .publishing(
                        topic("users", this::registeredUsersTopic)
                )
                .withAutoAcl(true);
    }

}
