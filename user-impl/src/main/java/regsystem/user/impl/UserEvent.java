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

public interface UserEvent extends Jsonable, AggregateEvent<UserEvent> {

    @Override
    default AggregateEventTag<UserEvent> aggregateTag() {
        return UserEventTag.INSTANCE;
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    @ToString
    @EqualsAndHashCode
    class UserCreated implements UserEvent {

        public final String id;
        public final String name;

        @JsonCreator
        UserCreated(String id, String name) {
            this.id = Preconditions.checkNotNull(id, "id is null");
            this.name = Preconditions.checkNotNull(name, "name is null");
        }
    }
}
