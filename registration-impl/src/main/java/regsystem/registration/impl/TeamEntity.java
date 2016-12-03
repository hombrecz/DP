package regsystem.registration.impl;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.util.Optional;

import regsystem.user.api.User;
import regsystem.registration.api.Team;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public class TeamEntity extends PersistentEntity<RegistrationCommand, RegistrationEvent, TeamState> {

    @Override
    public Behavior initialBehavior(Optional<TeamState> snapshotState) {

        BehaviorBuilder builder = newBehaviorBuilder(snapshotState.orElse(
                new TeamState(Optional.empty())));

        builder.setCommandHandler(RegistrationCommand.CreateTeam.class,
                (cmd, ctx) -> {
                    if (state().team.isPresent()) {
                        ctx.invalidCommand("Team " + entityId() + " is already created");
                        return ctx.done();
                    } else {
                        Team team = cmd.team;
                        RegistrationEvent.TeamCreated event = new RegistrationEvent.TeamCreated(team.teamId, team.teamName, team.capacity);
                        return ctx.thenPersist(event);
                    }
                });

        builder.setEventHandler(RegistrationEvent.TeamCreated.class,
                evt -> new TeamState(Optional.of(new Team(evt.teamId, evt.teamName, evt.capacity))));

        builder.setCommandHandler(RegistrationCommand.RegisterPlayer.class,
                (cmd, ctx) -> {
                    if (state().team.isPresent()) {
                        if (state().team.get().capacity > 0) {
                            RegistrationEvent.PlayerRegistered event = new RegistrationEvent.PlayerRegistered(
                                    new User(cmd.user.userId, state().team.get().teamId, cmd.user.name), state().team.get());
                            return ctx.thenPersist(event);
                        } else {
                            ctx.invalidCommand("Capacity of team " + entityId() + " is full");
                            return ctx.done();
                        }
                    } else {
                        ctx.invalidCommand("Team " + entityId() + " doesn't exist");
                        return ctx.done();
                    }
                }
        );

        builder.setEventHandler(RegistrationEvent.PlayerRegistered.class,
                evt -> state().registerPlayer());

        return builder.build();
    }
}
