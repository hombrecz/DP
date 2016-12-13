package regsystem.registration.impl;

import com.google.common.base.Preconditions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.serialization.CompressedJsonable;

import org.pcollections.PSequence;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import regsystem.registration.api.Group;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
@SuppressWarnings("serial")
@Immutable
@JsonDeserialize
@ToString
@EqualsAndHashCode
public class GroupState implements CompressedJsonable {

    public final Optional<Group> group;

    @JsonCreator
    public GroupState(Optional<Group> group) {
        this.group = Preconditions.checkNotNull(group, "group is null");
    }

    public GroupState registerUser() {
        Group group = this.group.get();
        return new GroupState(Optional.of(new Group(group.groupId, group.groupName, group.capacity - 1, Optional.ofNullable(group.users))));
    }

    public GroupState unregisterUser(String userId) {
        Group group = this.group.get();
        PSequence<String> users = group.users;
        users.minus(userId);
        return new GroupState(Optional.of(new Group(group.groupId, group.groupName, group.capacity + 1, Optional.ofNullable(users))));
    }
}
