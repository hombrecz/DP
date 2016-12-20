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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.inject.Inject;

import akka.Done;
import akka.NotUsed;
import akka.stream.javadsl.Flow;
import regsystem.registration.api.RegistrationService;
import regsystem.registration.impl.RegistrationEvent;
import regsystem.user.api.User;
import regsystem.user.api.UserService;

public class UserServiceImpl implements UserService {

    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final PersistentEntityRegistry persistentEntityRegistry;
    private final CassandraSession db;

    @Inject
    public UserServiceImpl(PersistentEntityRegistry persistentEntityRegistry,
                           ReadSide readSide, CassandraSession db,
                           RegistrationService registrationService) {
        this.persistentEntityRegistry = persistentEntityRegistry;
        this.db = db;
        persistentEntityRegistry.register(UserEntity.class);
        readSide.register(UserEventProcessor.class);

        registrationService.registeredUsersTopic()
                .subscribe()
                .atLeastOnce(
                        Flow.create().mapAsync(1, this::handleCreateUser)
                );
    }

    private CompletionStage handleCreateUser(Object event) {
        if (event instanceof RegistrationEvent.UserAccepted) {
            return handleUserAcceptedEvent(
                    (RegistrationEvent.UserAccepted) event
            );
        } else {
            // Ignore.
            return CompletableFuture.completedFuture(Done.getInstance());
        }
    }

    private CompletionStage handleUserAcceptedEvent(RegistrationEvent.UserAccepted event) {
        return userEntityRef(event.user.id).ask(new UserCommand.CreateUser(event.user))
                .thenApply(ack -> Done.getInstance());
    }

    @Override
    public ServiceCall<User, Done> createUser() {
        return request -> {
            log.info("Creating user: {}.", request.name);
            return userEntityRef(request.id).ask(new UserCommand.CreateUser(request))
                    .thenApply(ack -> Done.getInstance());
        };
    }

    @Override
    public ServiceCall<NotUsed, PSequence<User>> getUsers() {
        return req -> db.selectAll("SELECT * FROM user").thenApply(rows -> {
            List<User> list = rows.stream().map(r -> new User(
                    r.getString("id"),
                    r.getString("name"))).collect(Collectors.toList()
            );
            return TreePVector.from(list);
        });
    }

    private PersistentEntityRef<UserCommand> userEntityRef(String id) {
        return persistentEntityRegistry.refFor(UserEntity.class, id);
    }
}
