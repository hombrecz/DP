package regsystem.player.impl;

import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public class PlayerEventTag {
    public static final AggregateEventTag<PlayerEvent> INSTANCE =
            AggregateEventTag.of(PlayerEvent.class);
}
