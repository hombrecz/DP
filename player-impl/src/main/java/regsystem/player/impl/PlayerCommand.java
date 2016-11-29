package regsystem.player.impl;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import akka.Done;
import regsystem.player.api.Player;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public interface PlayerCommand extends Jsonable {

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class CreatePlayer implements PlayerCommand, CompressedJsonable, PersistentEntity.ReplyType<Done> {
        public final Player player;

        @JsonCreator
        public CreatePlayer(Player player) {
            this.player = Preconditions.checkNotNull(player, "player is null");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof CreatePlayer && equalTo((CreatePlayer) another);
        }

        private boolean equalTo(CreatePlayer another) {
            return player.equals(another.player);
        }

        @Override
        public int hashCode() {
            int h = 31;
            h = h * 17 + player.hashCode();
            return h;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper("CreatePlayer").add("player", player).toString();
        }
    }

}
