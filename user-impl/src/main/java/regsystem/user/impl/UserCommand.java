package regsystem.user.impl;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import akka.Done;
import regsystem.user.api.User;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public interface UserCommand extends Jsonable {

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class CreateUser implements UserCommand, CompressedJsonable, PersistentEntity.ReplyType<Done> {
        public final User user;

        @JsonCreator
        public CreateUser(User user) {
            this.user = Preconditions.checkNotNull(user, "user is null");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof CreateUser && equalTo((CreateUser) another);
        }

        private boolean equalTo(CreateUser another) {
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
            return MoreObjects.toStringHelper("CreateUser").add("user", user).toString();
        }
    }

}
