package regsystem.user.impl;

import com.google.common.base.Preconditions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;

import javax.annotation.concurrent.Immutable;

import akka.Done;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import regsystem.user.api.User;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public interface UserCommand extends Jsonable {

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    @ToString
    @EqualsAndHashCode
    public final class CreateUser implements UserCommand, CompressedJsonable, PersistentEntity.ReplyType<Done> {

        public final User user;

        @JsonCreator
        public CreateUser(User user) {
            this.user = Preconditions.checkNotNull(user, "user is null");
        }
    }

}
