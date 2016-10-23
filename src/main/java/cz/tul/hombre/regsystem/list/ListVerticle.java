package cz.tul.hombre.regsystem.list;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;

/**
 * @author ondrej.dlabola(at)gmail.com
 */
public class ListVerticle extends AbstractVerticle{

    private static final String TEMPLATES_PATH = "src/main/resources/templates/";
    private static final String REGISTRATION_TEMPLATE = "list.html";

    @Override
    public void start() {
        EventBus eventBus = getVertx().eventBus();

        final Router router = Router.router(vertx);
        final ThymeleafTemplateEngine engine = ThymeleafTemplateEngine.create();

        router.route().handler(BodyHandler.create());

//        router.get("/list").handler(ctx -> {
//            eventBus.send("database-list-verticle", "", reply -> {
//                if (reply.succeeded()) {
//                    reply.result().
//                    ctx.put("players", "Registration system");
//                    engine.render(ctx, TEMPLATES_PATH + REGISTRATION_TEMPLATE, render -> {
//                        if (render.succeeded()) {
//                            ctx.response().end(render.result());
//                        } else {
//                            ctx.fail(render.cause());
//                        }
//                    });
//                }
//            });
//        });
    }
}
