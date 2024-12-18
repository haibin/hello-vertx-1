package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.mysqlclient.MySQLBuilder;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import java.time.Duration;
import java.time.Instant;

public class MainVerticle extends AbstractVerticle {

  int MAX = 100000;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    MySQLConnectOptions connectOptions = new MySQLConnectOptions()
      .setPort(3307)
      .setHost("127.0.0.1")
      .setDatabase("my_db")
      .setUser("root")
      .setPassword("my-secret-pw");

    // Pool options
    PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

    Pool pool = MySQLBuilder.pool()
      .with(poolOptions)
      .connectingTo(connectOptions)
      .using(vertx)
      .build();

    var insert = sql();
    vertx.createHttpServer().requestHandler(req -> {
        db(insert, pool, req);
    }).listen(8888).onComplete(http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }

  private String sql() {
    StringBuffer insert = new StringBuffer("INSERT INTO test (id, name) VALUES ");
    for (int i = 0; i < MAX; i++) {
      insert.append(String.format("(%d, 'name%d')", i, i));
      if (i < MAX - 1) {
        insert.append(", ");
      }
    }
    return insert.toString();
  }

  public void db(String insert, Pool pool, HttpServerRequest req) {
    Instant start1 = Instant.now();

    // Record the start time
    Instant start = Instant.now();
    pool.query(insert).execute()
      .onSuccess(rows -> {
        // Record the end time
        Instant end = Instant.now();
        // Calculate the duration
        Duration duration = Duration.between(start, end);
        System.out.println("insert duration: " + duration.toMillis() + " milliseconds");

        System.out.println("rows = " + rows.rowCount());
        req.response()
          .putHeader("content-type", "text/plain")
          .end("Hello from Vert.x!");
      })
      .onFailure(Throwable::printStackTrace);

    Instant end1 = Instant.now();
    Duration duration1 = Duration.between(start1, end1);
    System.out.println("db method duration: " + duration1.toMillis() + " milliseconds");
  }
}
