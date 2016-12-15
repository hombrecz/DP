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

    //String id contains UUID
    public final String id;
    public final String name;

    public User(String id, String name) {
        this.id = Preconditions.checkNotNull(id, "id is null");
        this.name = Preconditions.checkNotNull(name, "name is null");
    }
}
