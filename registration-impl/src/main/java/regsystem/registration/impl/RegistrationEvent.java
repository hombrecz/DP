package regsystem.registration.impl;

import com.google.common.base.Preconditions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.serialization.Jsonable;

import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import lombok.EqualsAndHashCode;
import lombok.ToString;
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
    @ToString
    @EqualsAndHashCode
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
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    @ToString
    @EqualsAndHashCode
    public final class UserRegistered implements RegistrationEvent {

        public final User user;
        public final Group group;

        @JsonCreator
        public UserRegistered(User user, Group group) {
            this.user = Preconditions.checkNotNull(user, "user is null");
            this.group = Preconditions.checkNotNull(group, "group is null");
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    @ToString
    @EqualsAndHashCode
    public final class UserAccepted implements RegistrationEvent {

        public final User user;
        public final Group group;

        @JsonCreator
        public UserAccepted(User user, Group group) {
            this.user = Preconditions.checkNotNull(user, "user is null");
            this.group = Preconditions.checkNotNull(group, "group is null");
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    @ToString
    @EqualsAndHashCode
    public final class UserExceeded implements RegistrationEvent {

        public final User user;
        public final Group group;

        @JsonCreator
        public UserExceeded(User user, Group group) {
            this.user = Preconditions.checkNotNull(user, "user is null");
            this.group = Preconditions.checkNotNull(group, "group is null");
        }
    }
}
