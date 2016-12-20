package regsystem.registration.impl;

import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.broker.TopicProducer;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import com.lightbend.lagom.javadsl.pubsub.PubSubRef;
import com.lightbend.lagom.javadsl.pubsub.PubSubRegistry;
import com.lightbend.lagom.javadsl.pubsub.TopicId;

import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.inject.Inject;

import akka.Done;
import akka.NotUsed;
import akka.japi.Pair;
import regsystem.registration.api.Group;
import regsystem.registration.api.RegistrationService;
import regsystem.registration.api.RegistrationTicket;
import regsystem.user.api.User;

import static regsystem.registration.impl.RegistrationEvent.UserAccepted;

public class RegistrationServiceImpl implements RegistrationService {

    private final Logger log = LoggerFactory.getLogger(RegistrationServiceImpl.class);
    private final PersistentEntityRegistry persistentEntityRegistry;
    private final CassandraSession db;
    private final PubSubRef<RegistrationEvent.UserAccepted> publishedTopic;

    @Inject
    public RegistrationServiceImpl(PersistentEntityRegistry persistentEntityRegistry, ReadSide readSide,
                                   CassandraSession db,PubSubRegistry pubSub) {
        this.persistentEntityRegistry = persistentEntityRegistry;
        this.db = db;
        this.publishedTopic = pubSub.refFor(TopicId.of(UserAccepted.class, ""));
        persistentEntityRegistry.register(GroupEntity.class);
        readSide.register(RegistrationEventProcessor.class);
    }

    @Override
    public ServiceCall<RegistrationTicket, Done> registerUser() {
        return request -> {
            PersistentEntityRef<RegistrationCommand> groupEntity = groupEntityRef(request.groupId);
            User user = new User(UUID.randomUUID().toString(), request.userName);
            final CompletionStage<Done> registerToGroup = groupEntity.ask(new RegistrationCommand.RegisterUser(user));
            final CompletionStage<Done> checkCapacity = groupEntity.ask(new RegistrationCommand.CheckCapacity(user));
            return registerToGroup.thenCompose(afterRegistration -> checkCapacity);
        };
    }

    @Override
    public ServiceCall<Group, Done> createGroup() {
        return request -> {
            log.info("Creating group: {}.", request.name);
            return groupEntityRef(request.id).ask(new RegistrationCommand.CreateGroup(request))
                    .thenApply(ack -> Done.getInstance());
        };
    }

    @Override
    public ServiceCall<NotUsed, PSequence<Group>> getGroups() {
        return req -> db.selectAll("SELECT * FROM group").thenApply(rows -> {
            List<Group> list = rows.stream().map(r -> {
                Group group = new Group(r.getString("id"), r.getString("name"), r.getInt("capacity"),
                        getUsersFromList(r.getList("users", String.class)));
                log.info("Returning group: {}", group);
                return group;
            }).collect(Collectors.toList());
            return TreePVector.from(list);
        });
    }

    @Override
    public Topic<RegistrationEvent> registeredUsersTopic() {
        //TODO OD - in case of sharding switch to taggedStreamWithOffset
        return TopicProducer.singleStreamWithOffset(offset -> {
            return persistentEntityRegistry
                    .eventStream(RegistrationEventTag.INSTANCE, offset)
                    .map(p -> new Pair<>(p.first(), offset));
        });
    }

    private PersistentEntityRef<RegistrationCommand> groupEntityRef(String id) {
        return persistentEntityRegistry.refFor(GroupEntity.class, id);
    }

    private Optional<PSequence<String>> getUsersFromList(List<String> users) {
        return Optional.of(TreePVector.from(users));
    }

}
