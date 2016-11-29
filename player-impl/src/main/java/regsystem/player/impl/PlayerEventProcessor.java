package regsystem.player.impl;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

import akka.Done;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public class PlayerEventProcessor extends CassandraReadSideProcessor<PlayerEvent> {

    private final Logger log = LoggerFactory.getLogger(PlayerEventProcessor.class);

    private PreparedStatement writePlayer = null;
    private PreparedStatement writeOffset = null;

    private void setWritePlayer(PreparedStatement writePlayer) {
        this.writePlayer = writePlayer;
    }

    private void setWriteOffset(PreparedStatement writeOffset) {
        this.writeOffset = writeOffset;
    }

    @Override
    public AggregateEventTag<PlayerEvent> aggregateTag() {
        return PlayerEventTag.INSTANCE;
    }

    @Override
    public CompletionStage<Optional<UUID>> prepare(CassandraSession session) {
        return
                prepareCreateTables(session).thenCompose(a ->
                        prepareWritePlayer(session).thenCompose(b ->
                                prepareWriteOffset(session).thenCompose(c ->
                                        selectOffset(session))));
    }

    private CompletionStage<Done> prepareCreateTables(CassandraSession session) {
        return session.executeCreateTable(
                "CREATE TABLE IF NOT EXISTS player ("
                        + "playerId text, teamId text, name text,"
                        + "PRIMARY KEY (playerId))")
                .thenCompose(a -> session.executeCreateTable(
                        "CREATE TABLE IF NOT EXISTS player_offset ("
                                + "partition int, offset timeuuid, "
                                + "PRIMARY KEY (partition))"));
    }

    private CompletionStage<Done> prepareWritePlayer(CassandraSession session) {
        return session.prepare("INSERT INTO player (playerId, teamId, name) VALUES (?, ?, ?)").thenApply(ps -> {
            setWritePlayer(ps);
            return Done.getInstance();
        });
    }

    private CompletionStage<Done> prepareWriteOffset(CassandraSession session) {
        return session.prepare("INSERT INTO player_offset (partition, offset) VALUES (1, ?)").thenApply(ps -> {
            setWriteOffset(ps);
            return Done.getInstance();
        });
    }

    private CompletionStage<Optional<UUID>> selectOffset(CassandraSession session) {
        return session.selectOne("SELECT offset FROM player_offset")
                .thenApply(
                        optionalRow -> optionalRow.map(r -> r.getUUID("offset")));
    }

    @Override
    public EventHandlers defineEventHandlers(EventHandlersBuilder builder) {
        builder.setEventHandler(PlayerEvent.PlayerCreated.class, this::processPlayerCreated);
        return builder.build();
    }

    private CompletionStage<List<BoundStatement>> processPlayerCreated(PlayerEvent.PlayerCreated event, UUID offset) {
        BoundStatement bindCreatePlayer = writePlayer.bind();
        bindCreatePlayer.setString("playerId", event.playerId);
        bindCreatePlayer.setString("teamId", event.teamId);
        bindCreatePlayer.setString("name", event.name);
        BoundStatement bindWriteOffset = writeOffset.bind(offset);
        log.info("Persisted player {}", event.name);
        return completedStatements(Arrays.asList(bindCreatePlayer, bindWriteOffset));
    }
}
