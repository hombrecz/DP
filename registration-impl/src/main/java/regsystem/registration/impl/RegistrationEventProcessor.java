package regsystem.registration.impl;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;

import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import akka.Done;

import static com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide.completedStatement;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public class RegistrationEventProcessor extends ReadSideProcessor<RegistrationEvent> {

    private final Logger log = LoggerFactory.getLogger(RegistrationEventProcessor.class);

    private final CassandraSession session;
    private final CassandraReadSide readSide;

    private PreparedStatement writeGroup = null;
    private PreparedStatement decreaseCapacity = null;

    @Inject
    public RegistrationEventProcessor(CassandraSession session, CassandraReadSide readSide) {
        this.session = session;
        this.readSide = readSide;
    }

    private void setWriteGroup(PreparedStatement writeGroup) {
        this.writeGroup = writeGroup;
    }

    private void setDecreaseCapacity(PreparedStatement decreaseCapacity) {
        this.decreaseCapacity = decreaseCapacity;
    }

    @Override
    public PSequence<AggregateEventTag<RegistrationEvent>> aggregateTags() {
        return TreePVector.singleton(RegistrationEventTag.INSTANCE);
    }

    @Override
    public ReadSideHandler<RegistrationEvent> buildHandler() {
        return readSide.<RegistrationEvent>builder("group_offset")
                .setGlobalPrepare(this::prepareCreateTables)
                .setPrepare((ignored) -> prepareStatements())
                .setEventHandler(RegistrationEvent.GroupCreated.class, this::processGroupCreated)
                .setEventHandler(RegistrationEvent.UserRegistered.class, this::processUserRegistered)
                .build();
    }

    private CompletionStage<Done> prepareCreateTables() {
        return session.executeCreateTable(
                "CREATE TABLE IF NOT EXISTS group ("
                        + "groupId text, groupName text, capacity int,"
                        + "PRIMARY KEY (groupId))");
    }

    private CompletionStage<Done> prepareStatements() {
        return prepareWriteGroup().thenCompose(a -> prepareDecreaseCapacity());
    }

    private CompletionStage<Done> prepareWriteGroup() {
        return session.prepare("INSERT INTO group (groupId, groupName, capacity) VALUES (?, ?, ?)").thenApply(ps -> {
            setWriteGroup(ps);
            log.info("Registration write group prepared statement - OK");
            return Done.getInstance();
        });
    }

    private CompletionStage<Done> prepareDecreaseCapacity() {
        return session.prepare("UPDATE group set capacity = ? where groupId = ?").thenApply(ps -> {
            setDecreaseCapacity(ps);
            log.info("Registration decrease capacity prepared statement - OK");
            return Done.getInstance();
        });
    }

    private CompletionStage<List<BoundStatement>> processGroupCreated(RegistrationEvent.GroupCreated event) {
        BoundStatement bindWriteGroup = writeGroup.bind();
        bindWriteGroup.setString("groupId", event.groupId);
        bindWriteGroup.setString("groupName", event.groupName);
        bindWriteGroup.setInt("capacity", event.capacity);
        log.info("Persisted group {}", event.groupName);
        return completedStatement(bindWriteGroup);
    }

    private CompletionStage<List<BoundStatement>> processUserRegistered(RegistrationEvent.UserRegistered event) {
        final Integer decreasedCapacity = event.group.capacity - 1;

        BoundStatement bindDecreaseCapacity = decreaseCapacity.bind();
        bindDecreaseCapacity.setString("groupId", event.group.groupId);
        bindDecreaseCapacity.setInt("capacity", decreasedCapacity);
        log.info("Decreased capacity of group {} to {}", event.group.groupName, decreasedCapacity);
        return completedStatement(bindDecreaseCapacity);
    }
}
