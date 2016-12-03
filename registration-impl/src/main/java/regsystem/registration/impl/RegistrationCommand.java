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
import regsystem.user.api.User;
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

        public final User user;

        @JsonCreator
        public RegisterPlayer(User user) {
            this.user = Preconditions.checkNotNull(user, "user is null");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof RegisterPlayer && equalTo((RegisterPlayer) another);
        }

        private boolean equalTo(RegisterPlayer another) {
            return user.equals(another.user);
        }

        @Override
        public int hashCode() {
            int h = 31;
            h = h * 17 + user.hashCode();
            return h;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper("RegisterPlayer")
                    .add("user", user)
                    .toString();
        }
    }
}
