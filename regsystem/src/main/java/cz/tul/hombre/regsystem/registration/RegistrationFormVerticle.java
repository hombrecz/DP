package cz.tul.hombre.regsystem.registration;

import cz.tul.hombre.regsystem.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.templ.ThymeleafTemplateEngine;

/**
 * @author ondrej.dlabola(at)morosystems.cz
 */
public class RegistrationFormVerticle extends AbstractVerticle{

    public static void main(String[] args) {
        Runner.runExample(RegistrationFormVerticle.class);
    } //TODO - remove

    @Override
    public void start() {
        final Router router = Router.router(vertx);
        final ThymeleafTemplateEngine engine = ThymeleafTemplateEngine.create();

        router.route().handler(BodyHandler.create());

        router.get("/registration").handler(ctx -> {
            ctx.put("welcome", "Hello stranger!");

            engine.render(ctx, "registration.html", res -> {
                if (res.succeeded()) {
                    ctx.response().end(res.result());
                } else {
                    ctx.fail(res.cause());
                }
            });
        });

        router.post("/registration-form").handler(ctx -> {
            ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/plain");
            ctx.response().end("Hello " + ctx.request().getParam("name") + " " + ctx.request().getParam("surname") + "!");
        });

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }
}
