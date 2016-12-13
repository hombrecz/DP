package regsystem.registration.api;

import com.google.common.base.Preconditions;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import java.util.Optional;

import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
@Immutable
@JsonDeserialize
@ToString
@EqualsAndHashCode
public final class Group {

    public final String groupId;
    public final String groupName;
    public final PSequence<String> users;
    public final Integer capacity;

    public Group(String groupId, String groupName, Integer capacity, Optional<PSequence<String>> users) {
        this.groupId = Preconditions.checkNotNull(groupId, "groupId is null");
        this.groupName = Preconditions.checkNotNull(groupName, "groupName is null");
        this.users = users.orElse(TreePVector.empty());
        this.capacity = Preconditions.checkNotNull(capacity, "capacity is null");
    }
}
