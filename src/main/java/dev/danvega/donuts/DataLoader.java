package dev.danvega.donuts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);
    private static final String DONUTS_JSON_PATH = "classpath:data/donuts-menu.json";

    private final JsonMapper jsonMapper;
    private final ResourceLoader resourceLoader;
    private List<Donut> donuts;

    public DataLoader(JsonMapper jsonMapper, ResourceLoader resourceLoader) {
        this.jsonMapper = jsonMapper;
        this.resourceLoader = resourceLoader;
    }

    public List<Donut> getDonuts() {
        return donuts;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Loading Donuts \uD83C\uDF69");

        try {
            Resource resource = resourceLoader.getResource(DONUTS_JSON_PATH);

            if(!resource.exists()) {
                log.error("Donut menu file not found at: {}", DONUTS_JSON_PATH);
                return;
            }

            this.donuts = jsonMapper.readValue(
                    resource.getInputStream(),
                    new TypeReference<>() {}
            );

            donuts.forEach(System.out::println);

            // Demonstrate serialization back to JSON (using the configured JsonMapper)
            validateSerialization(donuts);

        } catch (JacksonException e) {
            // Jackson 3: All exceptions extend JacksonException (RuntimeException)
            // This is an unchecked exception, making it easier to use in lambdas
            log.error("Failed to load donut data from JSON file: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            // Handle other potential exceptions (e.g., resource loading)
            log.error("Unexpected error loading donut data", e);
            throw e;
        }
    }

    private void validateSerialization(List<Donut> donuts) {
        log.info("Serializing Donuts \uD83C\uDF69");

        if(!donuts.isEmpty()) {
            try {
                // Get the first donut and serialize it
                String json = jsonMapper.writeValueAsString(donuts.getFirst());

                // NOTE: How are properties in the JSON sorted?
                log.info("\n{}", json);

            } catch (JacksonException e) {
                // Jackson 3: Unchecked exception for serialization errors
                log.error("Failed to serialize donut to JSON: {}", e.getMessage(), e);
            }
        }
    }


}
