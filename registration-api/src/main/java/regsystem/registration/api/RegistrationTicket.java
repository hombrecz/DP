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
public class RegistrationTicket {

    public final String registrationId;

    public final String teamId;

    public final String name;

    public RegistrationTicket(String registrationId, String teamId, String name) {
        this.registrationId = Preconditions.checkNotNull(registrationId, "playerId is null");
        this.teamId = Preconditions.checkNotNull(teamId, "teamId is null");
        this.name = Preconditions.checkNotNull(name, "name is null");
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof RegistrationTicket && equalTo((RegistrationTicket) another);
    }

    private boolean equalTo(RegistrationTicket another) {
        return registrationId.equals(another.registrationId) && teamId.equals(another.teamId) && name.equals(another.name);
    }

    @Override
    public int hashCode() {
        int result = registrationId != null ? registrationId.hashCode() : 0;
        result = 31 * result + (teamId != null ? teamId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("Team")
                .add("playerId", registrationId)
                .add("teamId", teamId)
                .add("name", name)
                .toString();
    }

}
