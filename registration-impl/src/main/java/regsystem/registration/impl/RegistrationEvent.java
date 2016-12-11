package regsystem.registration.impl;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.serialization.Jsonable;

import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import java.util.Optional;

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
        public final PSequence<String> users;

        @JsonCreator
        public GroupCreated(String groupId, String groupName, Integer capacity, Optional<PSequence<String>> users) {

            this.groupId = Preconditions.checkNotNull(groupId, "groupId is null");
            this.groupName = Preconditions.checkNotNull(groupName, "groupName");
            this.capacity = Preconditions.checkNotNull(capacity, "capacity");
            this.users = users.orElse(TreePVector.empty());
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
            h = h * 17 + users.hashCode();
            return h;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper("GroupCreated")
                    .add("groupId", groupId)
                    .add("groupName", groupName)
                    .add("capacity", capacity)
                    .add("users", users)
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
            return user.equals(another.user)
                    && group.equals(another.group);
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

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class UserAccepted implements RegistrationEvent {
        public final User user;

        public final Group group;

        @JsonCreator
        public UserAccepted(User user, Group group) {

            this.user = Preconditions.checkNotNull(user, "user is null");
            this.group = Preconditions.checkNotNull(group, "group is null");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof UserAccepted && equalTo((UserAccepted) another);
        }

        private boolean equalTo(UserAccepted another) {
            return user.equals(another.user)
                    && group.equals(another.group);
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
            return MoreObjects.toStringHelper("UserAccepted")
                    .add("user", user)
                    .add("group", group)
                    .toString();
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class UserExceeded implements RegistrationEvent {
        public final User user;

        public final Group group;

        @JsonCreator
        public UserExceeded(User user, Group group) {

            this.user = Preconditions.checkNotNull(user, "user is null");
            this.group = Preconditions.checkNotNull(group, "group is null");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof UserExceeded && equalTo((UserExceeded) another);
        }

        private boolean equalTo(UserExceeded another) {
            return user.equals(another.user)
                    && group.equals(another.group);
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
            return MoreObjects.toStringHelper("UserExceeded")
                    .add("user", user)
                    .add("group", group)
                    .toString();
        }
    }
}
