package regsystem.registration.impl;

import com.google.inject.AbstractModule;

import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

import regsystem.registration.api.RegistrationService;
import regsystem.user.api.UserService;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public class RegistrationServiceModule extends AbstractModule implements ServiceGuiceSupport {

    @Override
    protected void configure() {
        bindServices(serviceBinding(RegistrationService.class, RegistrationServiceImpl.class));
        bindClient(UserService.class);
    }

}
