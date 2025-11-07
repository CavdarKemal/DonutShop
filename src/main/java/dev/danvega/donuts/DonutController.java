package dev.danvega.donuts;

import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/donuts")
public class DonutController {

    private static final Logger log = LoggerFactory.getLogger(DonutController.class);
    private final DataLoader dataLoader;

    public DonutController(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    // JSON Views

    @GetMapping("/summary")
    @JsonView(Views.Summary.class)
    public List<Donut> getSummary() {
        log.debug("Fetching donuts with Summary view");
        return dataLoader.getDonuts();
    }

    @GetMapping("/public")
    @JsonView(Views.Public.class)
    public List<Donut> getPublic() {
        log.debug("Fetching donuts with Public view");
        return dataLoader.getDonuts();
    }

    @GetMapping("/internal")
    @JsonView(Views.Internal.class)
    public List<Donut> getInternal() {
        log.debug("Fetching donuts with Internal view");
        return dataLoader.getDonuts();
    }

    @GetMapping("/admin")
    @JsonView(Views.Admin.class)
    public List<Donut> getAdmin() {
        log.debug("Fetching donuts with Admin view");
        return dataLoader.getDonuts();
    }

    // POST endpoint demonstrating @JsonView for deserialization
    // Only accepts 'type' and 'price' from client (Summary view)
    // Server generates the rest (glaze, toppings, calories, bakedAt, etc.)
    @PostMapping
    @JsonView(Views.Summary.class)
    public Donut createDonut(@RequestBody @JsonView(Views.Summary.class) Donut donut) {
        log.info("Received donut creation request");
        log.info("  Type: {}", donut.type());
        log.info("  Price: {}", donut.price());
        log.info("  Glaze: {} (should be null - not in Summary view)", donut.glaze());
        log.info("  Toppings: {} (should be null - not in Summary view)", donut.toppings());
        log.info("  IsVegan: {} (should be false - not in Summary view)", donut.isVegan());
        log.info("  Calories: {} (should be null - not in Summary view)", donut.calories());
        log.info("  BakedAt: {} (should be null - not in Summary view)", donut.bakedAt());

        // Server generates the missing fields
        Donut createdDonut = new Donut(
                donut.type(),
                Donut.Glaze.CHOCOLATE,  // Server default
                List.of("sprinkles"),    // Server default
                donut.price(),
                false,                   // Server calculates
                300,                     // Server calculates
                LocalDateTime.now()      // Server sets timestamp
        );

        log.info("Returning created donut with server-generated fields");
        return createdDonut;
    }
}
