package regsystem.user.api;

import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import org.pcollections.PSequence;

import akka.Done;
import akka.NotUsed;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.namedCall;
import static com.lightbend.lagom.javadsl.api.Service.restCall;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public interface UserService extends Service {

    ServiceCall<User, Done> createUser();

    ServiceCall<NotUsed, PSequence<User>> getUsers();

    @Override
    default Descriptor descriptor() {
        return named("userService").withCalls(
                namedCall("/api/userss/", this::createUser),
                restCall(Method.GET, "/api/users/all", this::getUsers)
        ).withAutoAcl(true);
    }
}
