package regsystem.registration.impl;

import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;

class RegistrationEventTag {

    static final AggregateEventTag<RegistrationEvent> INSTANCE =
            AggregateEventTag.of(RegistrationEvent.class);
}
