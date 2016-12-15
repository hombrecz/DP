package regsystem.user.api;

import com.google.common.base.Preconditions;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.annotation.concurrent.Immutable;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@Immutable
@JsonDeserialize
@ToString
@EqualsAndHashCode
public final class User {

    public final String userId;
    public final String name;

    public User(String userId, String name) {
        this.userId = Preconditions.checkNotNull(userId, "userId is null");
        this.name = Preconditions.checkNotNull(name, "name is null");
    }
}
