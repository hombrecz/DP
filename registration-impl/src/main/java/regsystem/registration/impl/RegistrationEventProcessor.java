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

import java.util.ArrayList;
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
    private PreparedStatement registerPlayerToGroup = null;
    private PreparedStatement unregisterPlayerFromGroup = null;

    @Inject
    public RegistrationEventProcessor(CassandraSession session, CassandraReadSide readSide) {
        this.session = session;
        this.readSide = readSide;
    }

    private void setWriteGroup(PreparedStatement writeGroup) {
        this.writeGroup = writeGroup;
    }

    private void setRegisterPlayerToGroup(PreparedStatement registerPlayerToGroup) {
        this.registerPlayerToGroup = registerPlayerToGroup;
    }

    private void setUnregisterPlayerFromGroup(PreparedStatement unregisterPlayerFromGroup) {
        this.unregisterPlayerFromGroup = unregisterPlayerFromGroup;
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
                .setEventHandler(RegistrationEvent.UserExceeded.class, this::processUserExceeded)
                .build();
    }

    private CompletionStage<Done> prepareCreateTables() {
        return session.executeCreateTable(
                "CREATE TABLE IF NOT EXISTS group ("
                        + "groupId text, groupName text, capacity int, users list<text>,"
                        + "PRIMARY KEY (groupId))");
    }

    private CompletionStage<Done> prepareStatements() {
        return prepareWriteGroup()
                .thenCompose(a -> prepareRegisterPlayerToGroup())
                .thenCompose(b -> prepareUnregisterPlayerFromGroup());
    }

    private CompletionStage<Done> prepareWriteGroup() {
        return session.prepare("INSERT INTO group (groupId, groupName, capacity, users) VALUES (?, ?, ?, ?)").thenApply(ps -> {
            setWriteGroup(ps);
            log.info("Registration write group prepared statement - OK");
            return Done.getInstance();
        });
    }

    private CompletionStage<Done> prepareRegisterPlayerToGroup() {
        return session.prepare("UPDATE group SET capacity = ?, users = users + ? WHERE groupId = ?").thenApply(ps -> {
            setRegisterPlayerToGroup(ps);
            log.info("Registration decrease capacity prepared statement - OK");
            return Done.getInstance();
        });
    }

    private CompletionStage<Done> prepareUnregisterPlayerFromGroup() {
        return session.prepare("UPDATE group SET capacity = ?, users = users - ? WHERE groupId = ?").thenApply(ps -> {
            setUnregisterPlayerFromGroup(ps);
            log.info("Registration decrease capacity prepared statement - OK");
            return Done.getInstance();
        });
    }

    private CompletionStage<List<BoundStatement>> processGroupCreated(RegistrationEvent.GroupCreated event) {
        BoundStatement bindWriteGroup = writeGroup.bind();
        bindWriteGroup.setString("groupId", event.groupId);
        bindWriteGroup.setString("groupName", event.groupName);
        bindWriteGroup.setInt("capacity", event.capacity);
        bindWriteGroup.setList("users", event.users);
        log.info("Persisted group {}", event.groupName);
        return completedStatement(bindWriteGroup);
    }

    private CompletionStage<List<BoundStatement>> processUserRegistered(RegistrationEvent.UserRegistered event) {
        Integer decreasedCapacity = event.group.capacity - 1;
        List<String> registeredUsers = new ArrayList<>();
        registeredUsers.add(event.user.userId);

        BoundStatement bindDecreaseCapacity = registerPlayerToGroup.bind();
        bindDecreaseCapacity.setString("groupId", event.group.groupId);
        bindDecreaseCapacity.setInt("capacity", decreasedCapacity);
        bindDecreaseCapacity.setList("users", registeredUsers);
        log.info("Decreased capacity of group {} to {}, added player {}", event.group.groupName, decreasedCapacity, event.user.name);
        return completedStatement(bindDecreaseCapacity);
    }

    private CompletionStage<List<BoundStatement>> processUserExceeded(RegistrationEvent.UserExceeded event) {
        Integer increasedCapacity = event.group.capacity + 1;
        List<String> registeredUsers = new ArrayList<>();
        registeredUsers.add(event.user.userId);

        BoundStatement bindDecreaseCapacity = unregisterPlayerFromGroup.bind();
        bindDecreaseCapacity.setString("groupId", event.group.groupId);
        bindDecreaseCapacity.setInt("capacity", increasedCapacity);
        bindDecreaseCapacity.setList("users", registeredUsers);
        log.info("Increased capacity of group {} to {}, removed player {}", event.group.groupName, increasedCapacity, event.user.name);
        return completedStatement(bindDecreaseCapacity);
    }
}
