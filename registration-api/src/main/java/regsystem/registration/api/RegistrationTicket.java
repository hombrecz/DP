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

    public final String groupId;

    public final String name;

    public RegistrationTicket(String registrationId, String groupId, String name) {
        this.registrationId = Preconditions.checkNotNull(registrationId, "registrationId is null");
        this.groupId = Preconditions.checkNotNull(groupId, "groupId is null");
        this.name = Preconditions.checkNotNull(name, "name is null");
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof RegistrationTicket && equalTo((RegistrationTicket) another);
    }

    private boolean equalTo(RegistrationTicket another) {
        return registrationId.equals(another.registrationId) && groupId.equals(another.groupId) && name.equals(another.name);
    }

    @Override
    public int hashCode() {
        int result = registrationId != null ? registrationId.hashCode() : 0;
        result = 31 * result + (groupId != null ? groupId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("Group")
                .add("registrationId", registrationId)
                .add("groupId", groupId)
                .add("name", name)
                .toString();
    }

}
