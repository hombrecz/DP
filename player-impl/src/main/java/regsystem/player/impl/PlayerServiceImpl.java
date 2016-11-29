package regsystem.player.impl;

import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;

import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.inject.Inject;

import akka.Done;
import akka.NotUsed;
import regsystem.player.api.Player;
import regsystem.player.api.PlayerService;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public class PlayerServiceImpl implements PlayerService {

    private final PersistentEntityRegistry persistentEntityRegistry;
    private final CassandraSession db;
    private final Logger log = LoggerFactory.getLogger(PlayerServiceImpl.class);

    @Inject
    public PlayerServiceImpl(PersistentEntityRegistry persistentEntityRegistry,
                             CassandraReadSide readSide, CassandraSession db) {
        this.persistentEntityRegistry = persistentEntityRegistry;
        this.db = db;
        persistentEntityRegistry.register(PlayerEntity.class);
        readSide.register(PlayerEventProcessor.class);
    }

    @Override
    public ServiceCall<Player, Done> createPlayer() {
        return (request) -> {
            log.info("Player: {}.", request.name);
            PersistentEntityRef<PlayerCommand> ref =
                    persistentEntityRegistry.refFor(PlayerEntity.class, request.playerId);
            return ref.ask(new PlayerCommand.CreatePlayer(request));
        };
    }

    @Override
    public ServiceCall<NotUsed, PSequence<Player>> getPlayers() {
        return (req) -> {
            CompletionStage<PSequence<Player>> result
                    = db.selectAll("SELECT * FROM player").thenApply(rows -> {
                List<Player> list = rows.stream().map(r -> new Player(r.getString("playerId"),
                        r.getString("teamId"), r.getString("name"))).collect(Collectors.toList());
                return TreePVector.from(list);
            });

            return result;
        };
    }
}
