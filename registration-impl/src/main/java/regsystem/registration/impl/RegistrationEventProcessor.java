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

    private PreparedStatement writeGroup = null;
    private PreparedStatement decreaseCapacity = null;
    private PreparedStatement writeOffset = null;

    private void setWriteGroup(PreparedStatement writeGroup) {
        this.writeGroup = writeGroup;
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
                        prepareWriteGroup(session).thenCompose(b ->
                                prepareDecreaseCapacity(session).thenCompose(c ->
                                        prepareWriteOffset(session).thenCompose(d ->
                                                selectOffset(session)))));
    }

    private CompletionStage<Done> prepareCreateTables(CassandraSession session) {
        return session.executeCreateTable(
                "CREATE TABLE IF NOT EXISTS group ("
                        + "groupId text, groupName text, capacity int,"
                        + "PRIMARY KEY (groupId))")
                .thenCompose(a -> session.executeCreateTable(
                        "CREATE TABLE IF NOT EXISTS group_offset ("
                                + "partition int, offset timeuuid, "
                                + "PRIMARY KEY (partition))"));
    }

    private CompletionStage<Done> prepareWriteGroup(CassandraSession session) {
        return session.prepare("INSERT INTO group (groupId, groupName, capacity) VALUES (?, ?, ?)").thenApply(ps -> {
            setWriteGroup(ps);
            return Done.getInstance();
        });
    }

    private CompletionStage<Done> prepareDecreaseCapacity(CassandraSession session) {
        return session.prepare("UPDATE group set capacity = ? where groupId = ?").thenApply(ps -> {
            setDecreaseCapacity(ps);
            return Done.getInstance();
        });
    }

    private CompletionStage<Done> prepareWriteOffset(CassandraSession session) {
        return session.prepare("INSERT INTO group_offset (partition, offset) VALUES (1, ?)").thenApply(ps -> {
            setWriteOffset(ps);
            return Done.getInstance();
        });
    }

    private CompletionStage<Optional<UUID>> selectOffset(CassandraSession session) {
        return session.selectOne("SELECT offset FROM group_offset")
                .thenApply(
                        optionalRow -> optionalRow.map(r -> r.getUUID("offset")));
    }

    @Override
    public EventHandlers defineEventHandlers(EventHandlersBuilder builder) {
        builder.setEventHandler(RegistrationEvent.GroupCreated.class, this::processGroupCreated);
        builder.setEventHandler(RegistrationEvent.UserRegistered.class, this::processUserRegistered);
        return builder.build();
    }

    private CompletionStage<List<BoundStatement>> processGroupCreated(RegistrationEvent.GroupCreated event, UUID offset) {
        BoundStatement bindWriteGroup = writeGroup.bind();
        bindWriteGroup.setString("groupId", event.groupId);
        bindWriteGroup.setString("groupName", event.groupName);
        bindWriteGroup.setInt("capacity", event.capacity);
        BoundStatement bindWriteOffset = writeOffset.bind(offset);
        log.info("Persisted group {}", event.groupName);
        return completedStatements(Arrays.asList(bindWriteGroup, bindWriteOffset));
    }

    private CompletionStage<List<BoundStatement>> processUserRegistered(RegistrationEvent.UserRegistered event, UUID offset) {
        BoundStatement bindDecreaseCapacity = decreaseCapacity.bind();
        bindDecreaseCapacity.setString("groupId", event.group.groupId);
        bindDecreaseCapacity.setInt("capacity", event.group.capacity - 1);
        BoundStatement bindWriteOffset = writeOffset.bind(offset);
        log.info("Decreased capacity of group {} to {}", event.group.groupName, event.group.capacity);
        return completedStatements(Arrays.asList(bindDecreaseCapacity, bindWriteOffset));
    }
}
