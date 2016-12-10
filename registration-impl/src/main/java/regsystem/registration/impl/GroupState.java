package regsystem.registration.impl;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.serialization.CompressedJsonable;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import regsystem.registration.api.Group;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
@SuppressWarnings("serial")
@Immutable
@JsonDeserialize
public class GroupState implements CompressedJsonable {

    public final Optional<Group> group;

    @JsonCreator
    public GroupState(Optional<Group> group) {
        this.group = Preconditions.checkNotNull(group, "group is null");
    }

    public GroupState registerUser() {
        Group t = group.get();
        return new GroupState(Optional.of(new Group(t.groupId, t.groupName,t.capacity - 1, Optional.ofNullable(t.users))));


    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof GroupState && equalTo((GroupState) another);
    }

    private boolean equalTo(GroupState another) {
        return group.equals(another.group);
    }

    @Override
    public int hashCode() {
        int h = 31;
        h = h * 17 + group.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("GroupState").add("group", group).toString();
    }
}
