package cz.tul.hombre.regsystem.main;

import cz.tul.hombre.regsystem.database.DatabaseAccessVerticle;
import cz.tul.hombre.regsystem.registration.RegistrationFormVerticle;
import io.vertx.core.Vertx;

/**
 * @author ondrej.dlabola(at)gmail.com
 */
public class MainVerticle {

    public static void main(String[] args) throws InterruptedException {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new RegistrationFormVerticle());
        vertx.deployVerticle(new DatabaseAccessVerticle());
    }
}
