package regsystem.registration.impl;

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
import regsystem.registration.api.Group;
import regsystem.user.api.User;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public interface RegistrationCommand extends Jsonable {

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    @ToString
    @EqualsAndHashCode
    public final class CreateGroup implements RegistrationCommand, CompressedJsonable, PersistentEntity.ReplyType<Done> {

        public final Group group;

        @JsonCreator
        public CreateGroup(Group group) {
            this.group = Preconditions.checkNotNull(group, "group is null");
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    @ToString
    @EqualsAndHashCode
    public final class RegisterUser implements RegistrationCommand, CompressedJsonable, PersistentEntity.ReplyType<Done> {

        public final User user;

        @JsonCreator
        public RegisterUser(User user) {
            this.user = Preconditions.checkNotNull(user, "user is null");
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    @ToString
    @EqualsAndHashCode
    public final class CheckCapacity implements RegistrationCommand, PersistentEntity.ReplyType<Done> {

        public final User user;

        @JsonCreator
        public CheckCapacity(User user) {
            this.user = Preconditions.checkNotNull(user, "user is null");
        }
    }
}
