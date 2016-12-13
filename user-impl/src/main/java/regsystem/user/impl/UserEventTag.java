package regsystem.user.impl;

import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
class UserEventTag {

    static final AggregateEventTag<UserEvent> INSTANCE =
            AggregateEventTag.of(UserEvent.class);
}
