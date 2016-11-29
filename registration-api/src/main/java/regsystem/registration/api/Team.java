package regsystem.registration.api;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.annotation.Nullable;

import jdk.nashorn.internal.ir.annotations.Immutable;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
@Immutable
@JsonDeserialize
public class Team {

    public final String teamId;

    public final String teamName;

    public final Integer capacity;

    public Team(String teamId, String teamName, Integer capacity) {
        this.teamId = Preconditions.checkNotNull(teamId, "teamId is null");
        this.teamName = Preconditions.checkNotNull(teamName, "teamName is null");
        this.capacity = Preconditions.checkNotNull(capacity, "capacity is null");
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof Team && equalTo((Team) another);
    }

    private boolean equalTo(Team another) {
        return teamId.equals(another.teamId) && teamName.equals(another.teamName) && capacity.equals(another.capacity);
    }

    @Override
    public int hashCode() {
        int result = teamId != null ? teamId.hashCode() : 0;
        result = 31 * result + (teamName != null ? teamName.hashCode() : 0);
        result = 31 * result + (capacity != null ? capacity.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("Team")
                .add("teamId", teamId)
                .add("teamName", teamName)
                .add("capacity", capacity)
                .toString();
    }
}
