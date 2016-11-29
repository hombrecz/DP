package regsystem.player.impl;

import com.google.inject.AbstractModule;

import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

import regsystem.player.api.PlayerService;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public class PlayerModule extends AbstractModule implements ServiceGuiceSupport {

    @Override
    protected void configure() {
        bindServices(serviceBinding(PlayerService.class, PlayerServiceImpl.class));
    }
}
