package regsystem.player.impl;

import com.google.common.base.Preconditions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.serialization.CompressedJsonable;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import regsystem.player.api.Player;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
@SuppressWarnings("serial")
@Immutable
@JsonDeserialize
public final class PlayerState implements CompressedJsonable {

    public final Optional<Player> player;

    @JsonCreator
    public PlayerState(Optional<Player> player) {
        this.player = Preconditions.checkNotNull(player, "player is null");
    }

}
