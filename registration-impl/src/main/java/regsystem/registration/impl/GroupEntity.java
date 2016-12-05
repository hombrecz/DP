package regsystem.registration.impl;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.util.Optional;

import akka.Done;
import regsystem.registration.api.Group;
import regsystem.user.api.User;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public class GroupEntity extends PersistentEntity<RegistrationCommand, RegistrationEvent, GroupState> {

    @Override
    public Behavior initialBehavior(Optional<GroupState> snapshotState) {

        BehaviorBuilder builder = newBehaviorBuilder(snapshotState.orElse(
                new GroupState(Optional.empty())));

        builder.setCommandHandler(RegistrationCommand.CreateGroup.class, (cmd, ctx) -> {
                    if (state().group.isPresent()) {
                        ctx.invalidCommand("Group " + entityId() + " is already created");
                        return ctx.done();
                    } else {
                        Group group = cmd.group;
                        RegistrationEvent.GroupCreated event = new RegistrationEvent.GroupCreated(group.groupId, group.groupName, group.capacity);
                        return ctx.thenPersist(event, evt -> ctx.reply(Done.getInstance()));
                    }
                });

        builder.setEventHandler(RegistrationEvent.GroupCreated.class,
                evt -> new GroupState(Optional.of(new Group(evt.groupId, evt.groupName, evt.capacity))));

        builder.setCommandHandler(RegistrationCommand.RegisterUser.class,
                (cmd, ctx) -> {
                    if (state().group.isPresent()) {
                        if (state().group.get().capacity > 0) {
                            RegistrationEvent.UserRegistered event = new RegistrationEvent.UserRegistered(
                                    new User(cmd.user.userId, state().group.get().groupId, cmd.user.name), state().group.get());
                            return ctx.thenPersist(event, evt -> ctx.reply(Done.getInstance()));
                        } else {
                            ctx.invalidCommand("Capacity of group " + entityId() + " is full");
                            return ctx.done();
                        }
                    } else {
                        ctx.invalidCommand("Group " + entityId() + " doesn't exist");
                        return ctx.done();
                    }
                }
        );

        builder.setEventHandler(RegistrationEvent.UserRegistered.class,
                evt -> state().registerUser());

        return builder.build();
    }
}
