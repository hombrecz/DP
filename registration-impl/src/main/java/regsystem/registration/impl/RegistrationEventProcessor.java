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
                .setPrepare(ignored -> prepareStatements())
                .setEventHandler(RegistrationEvent.GroupCreated.class, this::processGroupCreated)
                .setEventHandler(RegistrationEvent.UserRegistered.class, this::processUserRegistered)
                .setEventHandler(RegistrationEvent.UserExceeded.class, this::processUserExceeded)
                .build();
    }

    private CompletionStage<Done> prepareCreateTables() {
        return session.executeCreateTable(
                "CREATE TABLE IF NOT EXISTS group ("
                        + "id text, name text, capacity int, users list<text>,"
                        + "PRIMARY KEY (id))");
    }

    private CompletionStage<Done> prepareStatements() {
        return prepareWriteGroup()
                .thenCompose(a -> prepareRegisterPlayerToGroup())
                .thenCompose(b -> prepareUnregisterPlayerFromGroup());
    }

    private CompletionStage<Done> prepareWriteGroup() {
        return session.prepare("INSERT INTO group (id, name, capacity, users) VALUES (?, ?, ?, ?)").thenApply(ps -> {
            setWriteGroup(ps);
            log.debug("Registration write group prepared statement - OK");
            return Done.getInstance();
        });
    }

    private CompletionStage<Done> prepareRegisterPlayerToGroup() {
        return session.prepare("UPDATE group SET capacity = ?, users = users + ? WHERE id = ?").thenApply(ps -> {
            setRegisterPlayerToGroup(ps);
            log.debug("Registration decrease capacity prepared statement - OK");
            return Done.getInstance();
        });
    }

    private CompletionStage<Done> prepareUnregisterPlayerFromGroup() {
        return session.prepare("UPDATE group SET capacity = ?, users = users - ? WHERE id = ?").thenApply(ps -> {
            setUnregisterPlayerFromGroup(ps);
            log.debug("Registration decrease capacity prepared statement - OK");
            return Done.getInstance();
        });
    }

    private CompletionStage<List<BoundStatement>> processGroupCreated(RegistrationEvent.GroupCreated event) {
        BoundStatement bindWriteGroup = writeGroup.bind();
        bindWriteGroup.setString("id", event.id);
        bindWriteGroup.setString("name", event.name);
        bindWriteGroup.setInt("capacity", event.capacity);
        bindWriteGroup.setList("users", event.users);
        log.debug("Persisted group {}", event.name);
        return completedStatement(bindWriteGroup);
    }

    private CompletionStage<List<BoundStatement>> processUserRegistered(RegistrationEvent.UserRegistered event) {
        Integer decreasedCapacity = event.group.capacity - 1;
        List<String> registeredUsers = new ArrayList<>();
        registeredUsers.add(event.user.id);

        BoundStatement bindDecreaseCapacity = registerPlayerToGroup.bind();
        bindDecreaseCapacity.setString("id", event.group.id);
        bindDecreaseCapacity.setInt("capacity", decreasedCapacity);
        bindDecreaseCapacity.setList("users", registeredUsers);
        log.debug("Decreased capacity of group {} to {}, added player {}", event.group.name, decreasedCapacity, event.user.name);
        return completedStatement(bindDecreaseCapacity);
    }

    private CompletionStage<List<BoundStatement>> processUserExceeded(RegistrationEvent.UserExceeded event) {
        Integer increasedCapacity = event.group.capacity + 1;
        List<String> registeredUsers = new ArrayList<>();
        registeredUsers.add(event.user.id);

        BoundStatement bindDecreaseCapacity = unregisterPlayerFromGroup.bind();
        bindDecreaseCapacity.setString("id", event.group.id);
        bindDecreaseCapacity.setInt("capacity", increasedCapacity);
        bindDecreaseCapacity.setList("users", registeredUsers);
        log.debug("Increased capacity of group {} to {}, removed player {}", event.group.name, increasedCapacity, event.user.name);
        return completedStatement(bindDecreaseCapacity);
    }
}
