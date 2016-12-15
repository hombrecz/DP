package regsystem.registration.api;

import com.google.common.base.Preconditions;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Immutable
@JsonDeserialize
@ToString
@EqualsAndHashCode
public final class RegistrationTicket {

    public final String groupId;
    public final String userName;

    public RegistrationTicket(String groupId, String userName) {
        this.groupId = Preconditions.checkNotNull(groupId, "id is null");
        this.userName = Preconditions.checkNotNull(userName, "userName is null");
    }
}
