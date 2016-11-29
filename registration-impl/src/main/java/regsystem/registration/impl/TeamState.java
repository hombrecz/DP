package regsystem.registration.impl;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.serialization.CompressedJsonable;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import regsystem.registration.api.Team;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
@SuppressWarnings("serial")
@Immutable
@JsonDeserialize
public class TeamState implements CompressedJsonable {

    public final Optional<Team> team;

    @JsonCreator
    public TeamState(Optional<Team> team) {
        this.team = Preconditions.checkNotNull(team, "team is null");
    }

    public TeamState registerPlayer() {
        Team t = team.get();
        return new TeamState(Optional.of(new Team(t.teamId, t.teamName, t.capacity - 1)));


    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof TeamState && equalTo((TeamState) another);
    }

    private boolean equalTo(TeamState another) {
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
        return MoreObjects.toStringHelper("TeamState").add("team", team).toString();
    }
}
