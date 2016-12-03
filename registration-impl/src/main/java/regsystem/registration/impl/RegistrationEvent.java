package regsystem.registration.impl;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.serialization.Jsonable;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import regsystem.registration.api.Group;
import regsystem.user.api.User;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public interface RegistrationEvent extends Jsonable, AggregateEvent<RegistrationEvent> {

    @Override
    default AggregateEventTag<RegistrationEvent> aggregateTag() {
        return RegistrationEventTag.INSTANCE;
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class GroupCreated implements RegistrationEvent {
        public final String groupId;
        public final String groupName;
        public final Integer capacity;

        @JsonCreator
        public GroupCreated(String groupId, String groupName, Integer capacity) {

            this.groupId = Preconditions.checkNotNull(groupId, "groupId is null");
            this.groupName = Preconditions.checkNotNull(groupName, "groupName");
            this.capacity = Preconditions.checkNotNull(capacity, "capacity");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof GroupCreated && equalTo((GroupCreated) another);
        }

        private boolean equalTo(GroupCreated another) {
            return groupId.equals(another.groupId);
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
            return MoreObjects.toStringHelper("GroupCreated")
                    .add("groupId", groupId)
                    .add("groupName", groupName)
                    .add("capacity", capacity)
                    .toString();
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class UserRegistered implements RegistrationEvent {
        public final User user;

        public final Group group;

        @JsonCreator
        public UserRegistered(User user, Group group) {

            this.user = Preconditions.checkNotNull(user, "user is null");
            this.group = Preconditions.checkNotNull(group, "group is null");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof UserRegistered && equalTo((UserRegistered) another);
        }

        private boolean equalTo(UserRegistered another) {
            return user.equals(another.user) && group.equals(another.group);
        }

        @Override
        public int hashCode() {
            int h = 31;
            h = h * 17 + user.hashCode();
            h = h * 17 + group.hashCode();
            return h;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper("UserRegistered")
                    .add("user", user)
                    .add("group", group)
                    .toString();
        }
    }
}
