package cz.tul.hombre.regsystem.database;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PlainTextAuthProvider;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.utils.UUIDs;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.ExecutionException;

/**
 * @author ondrej.dlabola(at)gmail.com
 */
public class DatabaseAccessVerticle extends AbstractVerticle {

    private Cluster cluster;

    @Override
    public void start() {

        EventBus eventBus = getVertx().eventBus();

        eventBus.consumer("database-registrate-verticle", message -> {
            Insert query = getInsertQuery(message);

            cluster = Cluster.builder()
                    .addContactPoint("127.0.0.1")
                    .withPort(9042)
                    .withAuthProvider(new PlainTextAuthProvider("cassandra", "cassandra"))
                    .build();

            Session session = cluster.connect("test");
            ResultSetFuture future = session.executeAsync(query);

            try {
                ResultSet rs = future.get();

                JsonObject replyMessage = new JsonObject();
                replyMessage.put("status", "Registration data delivered and saved to Cassandra");

                message.reply(replyMessage);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

        eventBus.publisher("database-list-verticle", message -> {
            String[] columns = {"name", "surname", "nickname"};
            Select query = QueryBuilder.select(columns).from("test", "registrations");

            cluster = Cluster.builder()
                    .addContactPoint("127.0.0.1")
                    .withPort(9042)
                    .withAuthProvider(new PlainTextAuthProvider("cassandra", "cassandra"))
                    .build();

            Session session = cluster.connect("test");
            ResultSetFuture future = session.executeAsync(query);

            try {
                ResultSet rs = future.get();

                JsonObject replyMessage = new JsonObject();
                replyMessage.put("status", "Registration data delivered and saved to Cassandra");
                replyMessage.put("players", rs.all());

                message
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    private Insert getInsertQuery(Message<Object> message) {
        JsonObject customMessage = (JsonObject) message.body();

        String[] names = {"name", "surname", "nickname", "email"};

        String[] values = {
                customMessage.getString("name"),
                customMessage.getString("surname"),
                customMessage.getString("nickname"),
                customMessage.getString("email")
        };

        return QueryBuilder.insertInto("test", "registrations")
                .value("id", UUIDs.random())
                .values(names, values);
    }

    @Override
    public void stop() throws Exception {
        cluster.close();
    }
}
