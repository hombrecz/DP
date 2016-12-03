package regsystem.user.api;

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
public class User {

    public final String userId;

    public final String groupId;

    public final String name;

    public User(String userId, String groupId, String name) {
        this.userId = Preconditions.checkNotNull(userId, "userId is null");
        this.groupId = Preconditions.checkNotNull(groupId, "groupId is null");
        this.name = Preconditions.checkNotNull(name, "name is null");
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof User && equalTo((User) another);
    }

    private boolean equalTo(User another) {
        return userId.equals(another.userId)
                && groupId.equals(another.groupId)
                && name.equals(another.name);
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (groupId != null ? groupId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("User")
                .add("userId", userId)
                .add("groupId", groupId)
                .add("name", name)
                .toString();
    }
}
