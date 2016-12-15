package regsystem.user.impl;

import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;

class UserEventTag {

    static final AggregateEventTag<UserEvent> INSTANCE =
            AggregateEventTag.of(UserEvent.class);
}
