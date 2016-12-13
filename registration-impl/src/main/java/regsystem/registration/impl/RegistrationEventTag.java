package regsystem.registration.impl;

import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public class RegistrationEventTag {

    public static final AggregateEventTag<RegistrationEvent> INSTANCE =
            AggregateEventTag.of(RegistrationEvent.class);
}
