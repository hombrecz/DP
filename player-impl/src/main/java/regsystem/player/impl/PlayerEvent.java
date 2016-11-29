package regsystem.player.impl;

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
public interface PlayerEvent extends Jsonable, AggregateEvent<PlayerEvent> {

    @Override
    default public AggregateEventTag<PlayerEvent> aggregateTag() {
        return PlayerEventTag.INSTANCE;
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public class PlayerCreated implements PlayerEvent {
        public final String playerId;
        public final String teamId;
        public final String name;

        @JsonCreator
        public PlayerCreated(String playerId, String teamId, String name) {
            this.playerId = Preconditions.checkNotNull(playerId, "playerId is null");
            this.teamId = Preconditions.checkNotNull(teamId, "teamId is null");
            this.name = Preconditions.checkNotNull(name, "name is null");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof PlayerCreated && equalTo((PlayerCreated) another);
        }

        private boolean equalTo(PlayerCreated another) {
            return playerId.equals(another.playerId)
                    && teamId.equals(another.teamId)
                    && name.equals(another.name);
        }

        @Override
        public int hashCode() {
            int h = 31;
            h = h * 17 + playerId.hashCode();
            h = h * 17 + teamId.hashCode();
            h = h * 17 + name.hashCode();
            return h;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper("PlayerCreated")
                    .add("playerId", playerId)
                    .add("teamId", teamId)
                    .add("name", name)
                    .toString();
        }
    }
}
