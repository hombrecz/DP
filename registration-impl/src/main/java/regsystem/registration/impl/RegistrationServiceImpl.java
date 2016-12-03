package regsystem.registration.impl;

import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;

import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.inject.Inject;

import akka.Done;
import akka.NotUsed;
import regsystem.registration.api.Group;
import regsystem.user.api.User;
import regsystem.user.api.UserService;
import regsystem.registration.api.RegistrationService;
import regsystem.registration.api.RegistrationTicket;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public class RegistrationServiceImpl implements RegistrationService {

    private final PersistentEntityRegistry persistentEntityRegistry;
    private final UserService userService;
    private final CassandraSession db;
    private final Logger log = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    @Inject
    public RegistrationServiceImpl(PersistentEntityRegistry persistentEntityRegistry,
                                   UserService userService, CassandraSession db) {
        this.persistentEntityRegistry = persistentEntityRegistry;
        this.userService = userService;
        this.db = db;
        persistentEntityRegistry.register(GroupEntity.class);
    }

    @Override
    public ServiceCall<RegistrationTicket, Done> registerUser() {
        return request -> {
            PersistentEntityRef<RegistrationCommand> groupEntity = persistentEntityRegistry.refFor(GroupEntity.class, request.groupId);
            User user = new User(UUID.randomUUID().toString().replaceAll("-", ""), request.name, request.groupId);
            final CompletionStage<Done> registerToGroup = groupEntity.ask(new RegistrationCommand.RegisterUser(user));
            return registerToGroup.thenCompose(group -> userService.createUser().invoke(user));
        };
    }

    @Override
    public ServiceCall<Group, Done> createGroup() {
        return request -> {
            log.info("Group: {}.", request.groupName);
            PersistentEntityRef<RegistrationCommand> ref =
                    persistentEntityRegistry.refFor(GroupEntity.class, request.groupId);
            return ref.ask(new RegistrationCommand.CreateGroup(request));
        };
    }

    @Override
    public ServiceCall<NotUsed, PSequence<Group>> getGroups() {
        return (req) -> {
            CompletionStage<PSequence<Group>> result
                    = db.selectAll("SELECT * FROM group").thenApply(rows -> {
                List<Group> list = rows.stream().map(r -> new Group(r.getString("groupId"),
                        r.getString("groupName"), r.getInt("capacity"))).collect(Collectors.toList());
                return TreePVector.from(list);
            });
            return result;
        };
    }

}
