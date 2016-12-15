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

@SuppressWarnings("serial")
@Immutable
@JsonDeserialize
@ToString
@EqualsAndHashCode
public class GroupState implements CompressedJsonable {

    public final Optional<Group> group;

    @JsonCreator
    GroupState(Optional<Group> group) {
        this.group = Preconditions.checkNotNull(group, "group is null");
    }

    GroupState registerUser() {
        Group group = this.group.get();
        return new GroupState(Optional.of(new Group(group.id, group.name, group.capacity - 1, Optional.ofNullable(group.users))));
    }

    GroupState unregisterUser(String id) {
        Group group = this.group.get();
        PSequence<String> users = group.users;
        users.minus(id);
        return new GroupState(Optional.of(new Group(group.id, group.name, group.capacity + 1, Optional.of(users))));
    }
}
