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
public final class RegistrationTicket {

    public final String groupId;

    public final String userName;

    public RegistrationTicket(String registrationId, String groupId, String userName) {
        this.groupId = Preconditions.checkNotNull(groupId, "groupId is null");
        this.userName = Preconditions.checkNotNull(userName, "userName is null");
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof RegistrationTicket && equalTo((RegistrationTicket) another);
    }

    private boolean equalTo(RegistrationTicket another) {
        return groupId.equals(another.groupId) && userName.equals(another.userName);
    }

    @Override
    public int hashCode() {
        int result = groupId != null ? groupId.hashCode() : 0;
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("Group")
                .add("groupId", groupId)
                .add("userName", userName)
                .toString();
    }

}
