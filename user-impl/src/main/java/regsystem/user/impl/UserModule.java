package regsystem.user.impl;

import com.google.inject.AbstractModule;

import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

import regsystem.user.api.UserService;

public class UserModule extends AbstractModule implements ServiceGuiceSupport {

    @Override
    protected void configure() {
        bindServices(serviceBinding(UserService.class, UserServiceImpl.class));
    }
}
