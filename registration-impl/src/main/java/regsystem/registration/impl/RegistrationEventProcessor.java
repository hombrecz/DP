package regsystem.registration.impl;

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
public class RegistrationEventProcessor extends CassandraReadSideProcessor<RegistrationEvent> {

    private final Logger log = LoggerFactory.getLogger(RegistrationEventProcessor.class);

    private PreparedStatement writeTeam = null;
    private PreparedStatement decreaseCapacity = null;
    private PreparedStatement writeOffset = null;

    private void setWriteTeam(PreparedStatement writeTeam) {
        this.writeTeam = writeTeam;
    }

    private void setDecreaseCapacity(PreparedStatement decreaseCapacity) {
        this.decreaseCapacity = decreaseCapacity;
    }

    private void setWriteOffset(PreparedStatement writeOffset) {
        this.writeOffset = writeOffset;
    }

    @Override
    public AggregateEventTag<RegistrationEvent> aggregateTag() {
        return RegistrationEventTag.INSTANCE;
    }

    @Override
    public CompletionStage<Optional<UUID>> prepare(CassandraSession session) {
        return
                prepareCreateTables(session).thenCompose(a ->
                        prepareWriteTeam(session).thenCompose(b ->
                                prepareDecreaseCapacity(session).thenCompose(c ->
                                        prepareWriteOffset(session).thenCompose(d ->
                                                selectOffset(session)))));
    }

    private CompletionStage<Done> prepareCreateTables(CassandraSession session) {
        return session.executeCreateTable(
                "CREATE TABLE IF NOT EXISTS team ("
                        + "teamId text, teamName text, capacity int,"
                        + "PRIMARY KEY (teamId))")
                .thenCompose(a -> session.executeCreateTable(
                        "CREATE TABLE IF NOT EXISTS team_offset ("
                                + "partition int, offset timeuuid, "
                                + "PRIMARY KEY (partition))"));
    }

    private CompletionStage<Done> prepareWriteTeam(CassandraSession session) {
        return session.prepare("INSERT INTO team (teamId, teamName, capacity) VALUES (?, ?, ?)").thenApply(ps -> {
            setWriteTeam(ps);
            return Done.getInstance();
        });
    }

    private CompletionStage<Done> prepareDecreaseCapacity(CassandraSession session) {
        return session.prepare("UPDATE team set capacity = ? where teamId = ?").thenApply(ps -> {
            setDecreaseCapacity(ps);
            return Done.getInstance();
        });
    }

    private CompletionStage<Done> prepareWriteOffset(CassandraSession session) {
        return session.prepare("INSERT INTO team_offset (partition, offset) VALUES (1, ?)").thenApply(ps -> {
            setWriteOffset(ps);
            return Done.getInstance();
        });
    }

    private CompletionStage<Optional<UUID>> selectOffset(CassandraSession session) {
        return session.selectOne("SELECT offset FROM team_offset")
                .thenApply(
                        optionalRow -> optionalRow.map(r -> r.getUUID("offset")));
    }

    @Override
    public EventHandlers defineEventHandlers(EventHandlersBuilder builder) {
        builder.setEventHandler(RegistrationEvent.TeamCreated.class, this::processTeamCreated);
        builder.setEventHandler(RegistrationEvent.PlayerRegistered.class, this::processPlayerRegistered);
        return builder.build();
    }

    private CompletionStage<List<BoundStatement>> processTeamCreated(RegistrationEvent.TeamCreated event, UUID offset) {
        BoundStatement bindWriteTeam = writeTeam.bind();
        bindWriteTeam.setString("teamId", event.teamId);
        bindWriteTeam.setString("teamName", event.teamName);
        bindWriteTeam.setInt("capacity", event.capacity);
        BoundStatement bindWriteOffset = writeOffset.bind(offset);
        log.info("Persisted team {}", event.teamName);
        return completedStatements(Arrays.asList(bindWriteTeam, bindWriteOffset));
    }

    private CompletionStage<List<BoundStatement>> processPlayerRegistered(RegistrationEvent.PlayerRegistered event, UUID offset) {
        BoundStatement bindDecreaseCapacity = decreaseCapacity.bind();
        bindDecreaseCapacity.setString("teamId", event.team.teamId);
        bindDecreaseCapacity.setInt("capacity", event.team.capacity);
        BoundStatement bindWriteOffset = writeOffset.bind(offset);
        log.info("Decreased capacity of team {} to {}", event.team.teamName, event.team.capacity);
        return completedStatements(Arrays.asList(bindDecreaseCapacity, bindWriteOffset));
    }
}
