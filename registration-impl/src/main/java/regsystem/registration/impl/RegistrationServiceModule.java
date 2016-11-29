package regsystem.registration.impl;

import com.google.inject.AbstractModule;

import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

import regsystem.player.api.PlayerService;
import regsystem.registration.api.RegistrationService;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public class RegistrationServiceModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        bindServices(serviceBinding(RegistrationService.class, RegistrationServiceImpl.class));
        bindClient(PlayerService.class);
    }

}
