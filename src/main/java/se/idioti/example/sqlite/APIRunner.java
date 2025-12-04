package se.idioti.example.sqlite;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.CreatedResponse;
import io.javalin.http.NotFoundResponse;

import java.util.List;

/**
 * This demonstrates how to expose the storage through a REST API using Spark.
 * 
 * @author "Johan Holmberg, Malmö university"
 * @since 1.0
 */
public class APIRunner {

	private Storage storage = null;
	private Gson gson = null;

	public APIRunner() {
		try {
			storage = new Storage();
			storage.setup();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		// Set a decent date format
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	}

	public static void main(String[] args) throws Exception {
		APIRunner runner = new APIRunner();
		Javalin app = Javalin.create(config -> {}).start(5000);

        app.get("/", ctx -> {
            List<Unicorn> unicornList = runner.storage.fetchUnicorns();

            ctx.contentType("application/json");
            ctx.result(runner.gson.toJson(unicornList));
        });

        app.get("/{id}", ctx -> {
            Unicorn unicorn = runner.storage.fetchUnicorn(Integer.parseInt(ctx.pathParam("id")));
            if (unicorn.id == 0) {
                throw new NotFoundResponse();
            }

            ctx.contentType("application/json");
            ctx.result(runner.gson.toJson(unicorn));
        });

        app.post("/", ctx -> {
            try {
                Unicorn unicorn = runner.gson.fromJson(ctx.body(), Unicorn.class);
                runner.storage.addUnicorn(unicorn);
                throw new CreatedResponse();
            } catch (JsonSyntaxException e) {
                throw new BadRequestResponse();
            }
        });

        app.put("/{id}", ctx -> {
            //Update unicorn
        });

        app.delete("/{id}", ctx -> {
            //Delete unicorn
        });
	}

}
