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
import regsystem.user.api.User;
import regsystem.user.api.UserService;
import regsystem.registration.api.RegistrationService;
import regsystem.registration.api.RegistrationTicket;
import regsystem.registration.api.Team;

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
        persistentEntityRegistry.register(TeamEntity.class);
    }

    @Override
    public ServiceCall<RegistrationTicket, Done> registerPlayer() {
        return request -> {
            PersistentEntityRef<RegistrationCommand> teamEntity = persistentEntityRegistry.refFor(TeamEntity.class, request.teamId);
            User user = new User(UUID.randomUUID().toString().replaceAll("-", ""), request.name, request.teamId);
            final CompletionStage<Done> registerToTeam = teamEntity.ask(new RegistrationCommand.RegisterPlayer(user));
            return registerToTeam.thenCompose(team -> userService.createUser().invoke(user));
        };
    }

    @Override
    public ServiceCall<Team, Done> createTeam() {
        return request -> {
            log.info("Team: {}.", request.teamName);
            PersistentEntityRef<RegistrationCommand> ref =
                    persistentEntityRegistry.refFor(TeamEntity.class, request.teamId);
            return ref.ask(new RegistrationCommand.CreateTeam(request));
        };
    }

    @Override
    public ServiceCall<NotUsed, PSequence<Team>> getTeams() {
        return (req) -> {
            CompletionStage<PSequence<Team>> result
                    = db.selectAll("SELECT * FROM teams").thenApply(rows -> {
                List<Team> list = rows.stream().map(r -> new Team(r.getString("teamId"),
                        r.getString("teamName"), r.getInt("capacity"))).collect(Collectors.toList());
                return TreePVector.from(list);
            });
            return result;
        };
    }

}
