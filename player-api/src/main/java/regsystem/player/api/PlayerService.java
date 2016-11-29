package regsystem.player.api;

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
public interface PlayerService extends Service {

    ServiceCall<Player, Done> createPlayer();

    ServiceCall<NotUsed, PSequence<Player>> getPlayers();

    @Override
    default Descriptor descriptor() {
        return named("playerService").withCalls(
                namedCall("/api/players/", this::createPlayer),
                restCall(Method.GET, "/api/players/all", this::getPlayers)
        ).withAutoAcl(true);
    }
}
