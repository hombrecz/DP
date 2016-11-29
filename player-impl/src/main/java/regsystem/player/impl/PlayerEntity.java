package regsystem.player.impl;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.util.Optional;

import regsystem.player.api.Player;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public class PlayerEntity extends PersistentEntity<PlayerCommand, PlayerEvent, PlayerState> {

    @Override
    public Behavior initialBehavior(Optional<PlayerState> snapshotState) {
        BehaviorBuilder builder = newBehaviorBuilder(snapshotState.orElse(
                new PlayerState(Optional.empty())));

        builder.setCommandHandler(PlayerCommand.CreatePlayer.class, (cmd, ctx) -> {
            if (state().player.isPresent()) {
                ctx.invalidCommand("Player" + entityId() + " is already created");
                return ctx.done();
            } else {
                Player player = cmd.player;
                PlayerEvent event = new PlayerEvent.PlayerCreated(player.playerId, player.teamId, player.name);
                return ctx.thenPersist(event);
            }
        });

        builder.setEventHandler(PlayerEvent.PlayerCreated.class,
                evt -> new PlayerState(Optional.of(new Player(evt.playerId, evt.teamId, evt.name))));

        return builder.build();
    }
}
