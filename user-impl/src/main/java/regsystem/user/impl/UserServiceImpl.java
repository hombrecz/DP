package regsystem.user.impl;

import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
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
import regsystem.user.api.User;
import regsystem.user.api.UserService;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public class UserServiceImpl implements UserService {

    private final PersistentEntityRegistry persistentEntityRegistry;
    private final CassandraSession db;
    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Inject
    public UserServiceImpl(PersistentEntityRegistry persistentEntityRegistry,
                           ReadSide readSide, CassandraSession db) {
        this.persistentEntityRegistry = persistentEntityRegistry;
        this.db = db;
        persistentEntityRegistry.register(UserEntity.class);
        readSide.register(UserEventProcessor.class);
    }

    @Override
    public ServiceCall<User, Done> createUser() {
        return (request) -> {
            log.info("User: {}.", request.name);
            return userEntityRef(request.userId).ask(new UserCommand.CreateUser(request))
                .thenApply(ack -> Done.getInstance());
        };
    }

    @Override
    public ServiceCall<NotUsed, PSequence<User>> getUsers() {
        return (req) -> {
            CompletionStage<PSequence<User>> result
                    = db.selectAll("SELECT * FROM user").thenApply(rows -> {
                List<User> list = rows.stream().map(r -> new User(r.getString("userId"),
                        r.getString("groupId"), r.getString("name"))).collect(Collectors.toList());
                return TreePVector.from(list);
            });

            return result;
        };
    }

    private PersistentEntityRef<UserCommand> userEntityRef(String userId) {
        PersistentEntityRef<UserCommand> ref = persistentEntityRegistry.refFor(UserEntity.class, userId);
        return ref;
    }
}
