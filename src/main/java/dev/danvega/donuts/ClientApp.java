package dev.danvega.donuts;

import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ClientApp implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ClientApp.class);
    private final RestClient client;

    public ClientApp(RestClient.Builder builder) {
        this.client = builder
                .baseUrl("http://localhost:8080")
                .build();
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(ClientApp.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {


        // Create a Donut with ALL fields populated
        Donut clientDonut = new Donut(
                "Maple Bar",
                Donut.Glaze.MAPLE,
                List.of("pecans", "bacon"),
                new BigDecimal("3.99"),
                false,
                450,  // Client tries to set calories
                LocalDateTime.now().minusHours(2)  // Client tries to set bakedAt
        );

        log.info("Client created donut with ALL fields:");
        log.info("  Type: {}", clientDonut.type());
        log.info("  Glaze: {}", clientDonut.glaze());
        log.info("  Toppings: {}", clientDonut.toppings());
        log.info("  Price: {}", clientDonut.price());
        log.info("  IsVegan: {}", clientDonut.isVegan());
        log.info("  Calories: {}", clientDonut.calories());
        log.info("  BakedAt: {}", clientDonut.bakedAt());

        log.info("\nUsing hint() with Views.Summary.class - only type and price will be sent");

        // POST with hint() - only Summary fields (type, price) are serialized and sent
        Donut createdDonut = this.client.post()
                .uri("/api/donuts")
                .hint(JsonView.class.getName(), Views.Summary.class)
                .body(clientDonut)
                .retrieve()
                .body(Donut.class);

        log.info("\nServer returned created donut with server-generated fields:");
        log.info("  Type: {} (from client)", createdDonut.type());
        log.info("  Glaze: {} (server default)", createdDonut.glaze());
        log.info("  Toppings: {} (server default)", createdDonut.toppings());
        log.info("  Price: {} (from client)", createdDonut.price());
        log.info("  IsVegan: {} (server calculated)", createdDonut.isVegan());
        log.info("  Calories: {} (server calculated)", createdDonut.calories());
        log.info("  BakedAt: {} (server timestamp)", createdDonut.bakedAt());

        log.info("\n=== Key Insight ===");
        log.info("Client tried to send: glaze={}, toppings={}, calories={}",
                clientDonut.glaze(), clientDonut.toppings(), clientDonut.calories());
        log.info("Server received: glaze=null, toppings=null, calories=null (filtered by hint())");
        log.info("Server generated: glaze={}, toppings={}, calories={}",
                createdDonut.glaze(), createdDonut.toppings(), createdDonut.calories());
    }
}
