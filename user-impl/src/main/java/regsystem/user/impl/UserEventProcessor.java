package regsystem.user.impl;

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
public class UserEventProcessor extends ReadSideProcessor<UserEvent> {

    private final Logger log = LoggerFactory.getLogger(UserEventProcessor.class);

    private final CassandraSession session;
    private final CassandraReadSide readSide;

    private PreparedStatement writeUser = null;

    @Inject
    public UserEventProcessor(CassandraSession session, CassandraReadSide readSide) {
        this.session = session;
        this.readSide = readSide;
    }

    private void setWriteUser(PreparedStatement writeUser) {
        this.writeUser = writeUser;
    }

    @Override
    public PSequence<AggregateEventTag<UserEvent>> aggregateTags() {
        return TreePVector.singleton(UserEventTag.INSTANCE);
    }

    @Override
    public ReadSideHandler<UserEvent> buildHandler() {
        return readSide.<UserEvent>builder("user_offset")
                .setGlobalPrepare(this::prepareCreateTables)
                .setPrepare((ignored) -> prepareWriteUser())
                .setEventHandler(UserEvent.UserCreated.class, this::processUserCreated)
                .build();
    }

    private CompletionStage<Done> prepareCreateTables() {
        // @formatter:off
        return session.executeCreateTable(
            "CREATE TABLE IF NOT EXISTS user ("
                            + "userId text, groupId text, name text,"
                            + "PRIMARY KEY (userId))");
        // @formatter:on
    }

    private CompletionStage<Done> prepareWriteUser() {
        return session.prepare("INSERT INTO user (userId, groupId, name) VALUES (?, ?, ?)").thenApply(ps -> {
            setWriteUser(ps);
            return Done.getInstance();
        });
    }

    private CompletionStage<List<BoundStatement>> processUserCreated(UserEvent.UserCreated event) {
        BoundStatement bindCreateUser = writeUser.bind();
        bindCreateUser.setString("userId", event.userId);
        bindCreateUser.setString("groupId", event.groupId);
        bindCreateUser.setString("name", event.name);
        log.info("Persisted user {}", event.name);
        return completedStatement(bindCreateUser);
    }

}
