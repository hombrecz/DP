package regsystem.user.impl;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.serialization.CompressedJsonable;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import regsystem.user.api.User;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
@SuppressWarnings("serial")
@Immutable
@JsonDeserialize
public final class UserState implements CompressedJsonable {

    public final Optional<User> user;

    @JsonCreator
    public UserState(Optional<User> user) {
        this.user = Preconditions.checkNotNull(user, "user is null");
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof UserState && equalTo((UserState) another);
    }

    private boolean equalTo(UserState another) {
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
        return MoreObjects.toStringHelper("UserState").add("user", user).toString();
    }

}
