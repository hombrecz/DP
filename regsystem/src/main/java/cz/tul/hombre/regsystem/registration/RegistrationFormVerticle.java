package cz.tul.hombre.regsystem.registration;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;

/**
 * @author ondrej.dlabola(at)gmail.com
 */
public class RegistrationFormVerticle extends AbstractVerticle {

    private static final String TEMPLATES_PATH = "src/main/resources/templates/";
    private static final String REGISTRATION_TEMPLATE = "registration.html";

    @Override
    public void start() {
        EventBus eventBus = getVertx().eventBus();

        final Router router = Router.router(vertx);
        final ThymeleafTemplateEngine engine = ThymeleafTemplateEngine.create();

        router.route().handler(BodyHandler.create());

        router.get("/registration").handler(ctx -> {
            ctx.put("welcome", "Hello stranger!");
            engine.render(ctx, TEMPLATES_PATH + REGISTRATION_TEMPLATE, render -> {
                if (render.succeeded()) {
                    ctx.response().end(render.result());
                } else {
                    ctx.fail(render.cause());
                }
            });
        });

        router.post("/registration-form").handler(ctx -> {
            JsonObject registrationFields = getFormEntries(ctx);

            eventBus.send("database-verticle", registrationFields, reply -> {
                String replyMessage;
                if (reply.succeeded()) {
                    replyMessage = ((JsonObject) reply.result().body()).getString("status");
                } else {
                    replyMessage = "Unsuccessful try to send registration data!";
                }
                ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/plain");
                ctx.response().end(replyMessage);
            });
        });

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

    private JsonObject getFormEntries(RoutingContext ctx) {
        JsonObject registrationFields = new JsonObject();
        registrationFields.put("name", ctx.request().getParam("name"));
        registrationFields.put("surname", ctx.request().getParam("surname"));
        registrationFields.put("nickname", ctx.request().getParam("nickname"));
        registrationFields.put("email", ctx.request().getParam("email"));
        return registrationFields;
    }
}
