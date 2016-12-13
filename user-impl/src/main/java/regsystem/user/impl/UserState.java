package regsystem.user.impl;

import com.google.common.base.Preconditions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.serialization.CompressedJsonable;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import regsystem.user.api.User;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
@SuppressWarnings("serial")
@Immutable
@JsonDeserialize
@ToString
@EqualsAndHashCode
public final class UserState implements CompressedJsonable {

    public final Optional<User> user;

    @JsonCreator
    public UserState(Optional<User> user) {
        this.user = Preconditions.checkNotNull(user, "user is null");
    }
}
