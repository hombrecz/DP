package regsystem.registration.api;

import com.google.common.base.Preconditions;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import java.util.Optional;

import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Immutable
@JsonDeserialize
@ToString
@EqualsAndHashCode
public final class Group {

    //String id contains UUID
    public final String id;
    public final String name;
    public final PSequence<String> users;
    public final Integer capacity;

    public Group(String id, String name, Integer capacity, Optional<PSequence<String>> users) {
        this.id = Preconditions.checkNotNull(id, "id is null");
        this.name = Preconditions.checkNotNull(name, "name is null");
        this.users = users.orElse(TreePVector.empty());
        this.capacity = Preconditions.checkNotNull(capacity, "capacity is null");
    }
}
