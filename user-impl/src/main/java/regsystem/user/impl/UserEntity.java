package regsystem.user.impl;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.util.Optional;

import regsystem.user.api.User;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public class UserEntity extends PersistentEntity<UserCommand, UserEvent, UserState> {

    @Override
    public Behavior initialBehavior(Optional<UserState> snapshotState) {
        BehaviorBuilder builder = newBehaviorBuilder(snapshotState.orElse(
                new UserState(Optional.empty())));

        builder.setCommandHandler(UserCommand.CreateUser.class, (cmd, ctx) -> {
            if (state().user.isPresent()) {
                ctx.invalidCommand("User" + entityId() + " is already created");
                return ctx.done();
            } else {
                User user = cmd.user;
                UserEvent event = new UserEvent.UserCreated(user.userId, user.teamId, user.name);
                return ctx.thenPersist(event);
            }
        });

        builder.setEventHandler(UserEvent.UserCreated.class,
                evt -> new UserState(Optional.of(new User(evt.userId, evt.teamId, evt.name))));

        return builder.build();
    }
}
