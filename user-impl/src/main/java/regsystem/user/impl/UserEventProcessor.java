package regsystem.user.impl;

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
public class UserEventProcessor extends CassandraReadSideProcessor<UserEvent> {

    private final Logger log = LoggerFactory.getLogger(UserEventProcessor.class);

    private PreparedStatement writeUser = null;
    private PreparedStatement writeOffset = null;

    private void setWriteUser(PreparedStatement writeUser) {
        this.writeUser = writeUser;
    }

    private void setWriteOffset(PreparedStatement writeOffset) {
        this.writeOffset = writeOffset;
    }

    @Override
    public AggregateEventTag<UserEvent> aggregateTag() {
        return UserEventTag.INSTANCE;
    }

    @Override
    public CompletionStage<Optional<UUID>> prepare(CassandraSession session) {
        return
                prepareCreateTables(session).thenCompose(a ->
                        prepareWriteUser(session).thenCompose(b ->
                                prepareWriteOffset(session).thenCompose(c ->
                                        selectOffset(session))));
    }

    private CompletionStage<Done> prepareCreateTables(CassandraSession session) {
        return session.executeCreateTable(
                "CREATE TABLE IF NOT EXISTS user ("
                        + "userId text, groupId text, name text,"
                        + "PRIMARY KEY (userId))")
                .thenCompose(a -> session.executeCreateTable(
                        "CREATE TABLE IF NOT EXISTS user_offset ("
                                + "partition int, offset timeuuid, "
                                + "PRIMARY KEY (partition))"));
    }

    private CompletionStage<Done> prepareWriteUser(CassandraSession session) {
        return session.prepare("INSERT INTO user (userId, groupId, name) VALUES (?, ?, ?)").thenApply(ps -> {
            setWriteUser(ps);
            return Done.getInstance();
        });
    }

    private CompletionStage<Done> prepareWriteOffset(CassandraSession session) {
        return session.prepare("INSERT INTO user_offset (partition, offset) VALUES (1, ?)").thenApply(ps -> {
            setWriteOffset(ps);
            return Done.getInstance();
        });
    }

    private CompletionStage<Optional<UUID>> selectOffset(CassandraSession session) {
        return session.selectOne("SELECT offset FROM user_offset")
                .thenApply(
                        optionalRow -> optionalRow.map(r -> r.getUUID("offset")));
    }

    @Override
    public EventHandlers defineEventHandlers(EventHandlersBuilder builder) {
        builder.setEventHandler(UserEvent.UserCreated.class, this::processUserCreated);
        return builder.build();
    }

    private CompletionStage<List<BoundStatement>> processUserCreated(UserEvent.UserCreated event, UUID offset) {
        BoundStatement bindCreateUser = writeUser.bind();
        bindCreateUser.setString("userId", event.userId);
        bindCreateUser.setString("groupId", event.groupId);
        bindCreateUser.setString("name", event.name);
        BoundStatement bindWriteOffset = writeOffset.bind(offset);
        log.info("Persisted user {}", event.name);
        return completedStatements(Arrays.asList(bindCreateUser, bindWriteOffset));
    }
}
