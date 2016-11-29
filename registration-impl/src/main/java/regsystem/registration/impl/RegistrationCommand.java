package regsystem.registration.impl;

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
import regsystem.registration.api.Team;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public interface RegistrationCommand extends Jsonable {

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class CreateTeam implements RegistrationCommand, CompressedJsonable, PersistentEntity.ReplyType<Done> {
        public final Team team;

        @JsonCreator
        public CreateTeam(Team team) {
            this.team = Preconditions.checkNotNull(team, "team is null");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof CreateTeam && equalTo((CreateTeam) another);
        }

        private boolean equalTo(CreateTeam another) {
            return team.equals(another.team);
        }

        @Override
        public int hashCode() {
            int h = 31;
            h = h * 17 + team.hashCode();
            return h;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper("CreateTeam").add("team", team).toString();
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class RegisterPlayer implements RegistrationCommand, CompressedJsonable, PersistentEntity.ReplyType<Done> {

        public final Player player;

        @JsonCreator
        public RegisterPlayer(Player player) {
            this.player = Preconditions.checkNotNull(player, "player is null");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof RegisterPlayer && equalTo((RegisterPlayer) another);
        }

        private boolean equalTo(RegisterPlayer another) {
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
            return MoreObjects.toStringHelper("RegisterPlayer")
                    .add("player", player)
                    .toString();
        }
    }
}
