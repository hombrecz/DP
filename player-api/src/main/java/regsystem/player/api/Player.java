package regsystem.player.api;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;


@Immutable
@JsonDeserialize
public class Player {

    public final String playerId;

    public final String teamId;

    public final String name;

    public Player(String playerId, String teamId, String name) {
        this.playerId = Preconditions.checkNotNull(playerId, "playerId is null");
        this.teamId = Preconditions.checkNotNull(teamId, "teamId is null");
        this.name = Preconditions.checkNotNull(name, "name is null");
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof Player && equalTo((Player) another);
    }

    private boolean equalTo(Player another) {
        return playerId.equals(another.playerId)
                && teamId.equals(another.teamId)
                && name.equals(another.name);
    }

    @Override
    public int hashCode() {
        int result = playerId != null ? playerId.hashCode() : 0;
        result = 31 * result + (teamId != null ? teamId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("Player")
                .add("playerId", playerId)
                .add("teamId", teamId)
                .add("name", name)
                .toString();
    }
}
