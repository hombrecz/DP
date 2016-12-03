package regsystem.registration.api;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.annotation.Nullable;

import jdk.nashorn.internal.ir.annotations.Immutable;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
@Immutable
@JsonDeserialize
public final class Group {

    public final String groupId;

    public final String groupName;

    public final Integer capacity;

    public Group(String groupId, String groupName, Integer capacity) {
        this.groupId = Preconditions.checkNotNull(groupId, "groupId is null");
        this.groupName = Preconditions.checkNotNull(groupName, "groupName is null");
        this.capacity = Preconditions.checkNotNull(capacity, "capacity is null");
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof Group && equalTo((Group) another);
    }

    private boolean equalTo(Group another) {
        return groupId.equals(another.groupId) && groupName.equals(another.groupName) && capacity.equals(another.capacity);
    }

    @Override
    public int hashCode() {
        int h = 31;
        h = h * 17 + groupId.hashCode();
        h = h * 17 + groupName.hashCode();
        h = h * 17 + capacity.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("Group")
                .add("groupId", groupId)
                .add("groupName", groupName)
                .add("capacity", capacity)
                .toString();
    }
}
