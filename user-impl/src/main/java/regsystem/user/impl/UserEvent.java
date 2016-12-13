package regsystem.user.impl;

import com.google.common.base.Preconditions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.serialization.Jsonable;

import javax.annotation.concurrent.Immutable;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public interface UserEvent extends Jsonable, AggregateEvent<UserEvent> {

    @Override
    default public AggregateEventTag<UserEvent> aggregateTag() {
        return UserEventTag.INSTANCE;
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    @ToString
    @EqualsAndHashCode
    public class UserCreated implements UserEvent {

        public final String userId;
        public final String name;

        @JsonCreator
        public UserCreated(String userId, String name) {
            this.userId = Preconditions.checkNotNull(userId, "userId is null");
            this.name = Preconditions.checkNotNull(name, "name is null");
        }
    }
}
