package regsystem.registration.impl;

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
import regsystem.registration.api.Group;
import regsystem.user.api.User;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public interface RegistrationCommand extends Jsonable {

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class CreateGroup implements RegistrationCommand, CompressedJsonable, PersistentEntity.ReplyType<Done> {
        public final Group group;

        @JsonCreator
        public CreateGroup(Group group) {
            this.group = Preconditions.checkNotNull(group, "group is null");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof CreateGroup && equalTo((CreateGroup) another);
        }

        private boolean equalTo(CreateGroup another) {
            return group.equals(another.group);
        }

        @Override
        public int hashCode() {
            int h = 31;
            h = h * 17 + group.hashCode();
            return h;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper("CreateGroup").add("group", group).toString();
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class RegisterUser implements RegistrationCommand, CompressedJsonable, PersistentEntity.ReplyType<Done> {

        public final User user;

        @JsonCreator
        public RegisterUser(User user) {
            this.user = Preconditions.checkNotNull(user, "user is null");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof RegisterUser && equalTo((RegisterUser) another);
        }

        private boolean equalTo(RegisterUser another) {
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
            return MoreObjects.toStringHelper("RegisterUser")
                    .add("user", user)
                    .toString();
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class CheckCapacity implements RegistrationCommand,PersistentEntity.ReplyType<Done> {

        public final User user;

        @JsonCreator
        public CheckCapacity(User user) {
            this.user = Preconditions.checkNotNull(user, "user is null");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof CheckCapacity && equalTo((CheckCapacity) another);
        }

        private boolean equalTo(CheckCapacity another) {
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
            return MoreObjects.toStringHelper("CheckCapacity")
                    .add("user", user)
                    .toString();
        }
    }
}
