package regsystem.registration.impl;

import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
class RegistrationEventTag {

    static final AggregateEventTag<RegistrationEvent> INSTANCE =
            AggregateEventTag.of(RegistrationEvent.class);
}
