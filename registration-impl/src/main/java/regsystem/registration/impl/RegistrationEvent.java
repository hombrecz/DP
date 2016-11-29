package regsystem.registration.impl;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.serialization.Jsonable;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import regsystem.player.api.Player;
import regsystem.registration.api.Team;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public interface RegistrationEvent extends Jsonable, AggregateEvent<RegistrationEvent> {

    @Override
    default AggregateEventTag<RegistrationEvent> aggregateTag() {
        return RegistrationEventTag.INSTANCE;
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class TeamCreated implements RegistrationEvent {
        public final String teamId;
        public final String teamName;
        public final Integer capacity;

        @JsonCreator
        public TeamCreated(String teamId, String teamName, Integer capacity) {

            this.teamId = Preconditions.checkNotNull(teamId, "teamId is null");
            this.teamName = Preconditions.checkNotNull(teamName, "teamName");
            this.capacity = Preconditions.checkNotNull(capacity, "capacity");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof TeamCreated && equalTo((TeamCreated) another);
        }

        private boolean equalTo(TeamCreated another) {
            return teamId.equals(another.teamId);
        }

        @Override
        public int hashCode() {
            int h = 31;
            h = h * 17 + teamId.hashCode();
            h = h * 17 + teamName.hashCode();
            h = h * 17 + capacity.hashCode();
            return h;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper("TeamCreated")
                    .add("teamId", teamId)
                    .add("teamName", teamName)
                    .add("capacity", capacity)
                    .toString();
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class PlayerRegistered implements RegistrationEvent {
        public final Player player;

        public final Team team;

        @JsonCreator
        public PlayerRegistered(Player player, Team team) {

            this.player = Preconditions.checkNotNull(player, "player is null");
            this.team = Preconditions.checkNotNull(team, "team is null");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof PlayerRegistered && equalTo((PlayerRegistered) another);
        }

        private boolean equalTo(PlayerRegistered another) {
            return player.equals(another.player) && team.equals(another.team);
        }

        @Override
        public int hashCode() {
            int h = 31;
            h = h * 17 + player.hashCode();
            h = h * 17 + team.hashCode();
            return h;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper("PlayerRegistered")
                    .add("player", player)
                    .add("team", team)
                    .toString();
        }
    }
}
