package regsystem.registration.impl;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import akka.Done;
import regsystem.registration.api.Group;
import regsystem.user.api.User;

import static regsystem.registration.impl.RegistrationCommand.*;
import static regsystem.registration.impl.RegistrationEvent.*;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public class GroupEntity extends PersistentEntity<RegistrationCommand, RegistrationEvent, GroupState> {

    private final Logger log = LoggerFactory.getLogger(GroupEntity.class);

    @Override
    public Behavior initialBehavior(Optional<GroupState> snapshotState) {

        BehaviorBuilder builder = newBehaviorBuilder(snapshotState.orElse(
                new GroupState(Optional.empty())));

        builder.setCommandHandler(CreateGroup.class, (cmd, ctx) -> {
            if (state().group.isPresent()) {
                ctx.invalidCommand("Group " + entityId() + " is already created");
                return ctx.done();
            } else {
                Group group = cmd.group;
                GroupCreated event = new GroupCreated(group.groupId, group.groupName, group.capacity, Optional.empty());
                return ctx.thenPersist(event, evt -> ctx.reply(Done.getInstance()));
            }
        });

        builder.setEventHandler(GroupCreated.class,
                evt -> new GroupState(Optional.of(new Group(evt.groupId, evt.groupName, evt.capacity, Optional.empty()))));

        builder.setCommandHandler(RegisterUser.class,
                (cmd, ctx) -> {
                    if (state().group.isPresent()) {
                        if (state().group.get().capacity > 0) {
                            UserRegistered event = new UserRegistered(
                                    new User(cmd.user.userId, cmd.user.name), state().group.get());
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

        builder.setEventHandler(UserRegistered.class,
                evt -> state().registerUser());

        builder.setCommandHandler(CheckCapacity.class,
                (cmd, ctx) -> {
                    if (state().group.isPresent()) {
                        if (state().group.get().capacity >= 0) {
                            log.info("Capacity of group {} is enough.", state().group.get().groupName);
                            UserAccepted event = new UserAccepted(
                                    new User(cmd.user.userId, cmd.user.name), state().group.get());
                            return ctx.thenPersist(event, evt -> ctx.reply(Done.getInstance()));
                        } else {
                            UserExceeded event = new UserExceeded(
                                    new User(cmd.user.userId, cmd.user.name), state().group.get());
                            String message = "Capacity of group " + state().group.get().groupName + " has been exceeded. Performing correction.";
                            return ctx.thenPersist(event, evt -> ctx.invalidCommand(message));
                        }
                    } else {
                        ctx.invalidCommand("Group " + entityId() + " doesn't exist");
                        return ctx.done();
                    }
                }
        );

        builder.setEventHandler(UserExceeded.class,
                evt -> state().unregisterUser(evt.user.userId));

        builder.setEventHandler(UserAccepted.class,
                evt -> state());

        return builder.build();
    }
}
