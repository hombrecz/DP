package regsystem.user.impl;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.serialization.Jsonable;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

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
    public class UserCreated implements UserEvent {
        public final String userId;
        public final String name;

        @JsonCreator
        public UserCreated(String userId, String name) {
            this.userId = Preconditions.checkNotNull(userId, "userId is null");
            this.name = Preconditions.checkNotNull(name, "name is null");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof UserCreated && equalTo((UserCreated) another);
        }

        private boolean equalTo(UserCreated another) {
            return userId.equals(another.userId)
                    && name.equals(another.name);
        }

        @Override
        public int hashCode() {
            int h = 31;
            h = h * 17 + userId.hashCode();
            h = h * 17 + name.hashCode();
            return h;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper("UserCreated")
                    .add("userId", userId)
                    .add("name", name)
                    .toString();
        }
    }
}
