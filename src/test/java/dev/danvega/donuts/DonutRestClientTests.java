package dev.danvega.donuts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DonutRestClientTests {

    @LocalServerPort
    private int port;

    private RestTestClient restTestClient;

    @BeforeEach
    void setUp() {
        // Build RestTestClient for live server testing
        this.restTestClient = RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void testSummaryView() {
        // Call the summary endpoint and verify it returns only type and price
        restTestClient.get()
                .uri("/api/donuts/summary")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].type").exists()
                .jsonPath("$[0].price").exists()
                .jsonPath("$[0].glaze").doesNotExist()
                .jsonPath("$[0].toppings").doesNotExist()
                .jsonPath("$[0].isVegan").doesNotExist()
                .jsonPath("$[0].calories").doesNotExist()
                .jsonPath("$[0].bakedAt").doesNotExist();
    }

    @Test
    void testPublicView() {
        // Call the public endpoint and verify it includes public fields but not internal ones
        restTestClient.get()
                .uri("/api/donuts/public")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].type").exists()
                .jsonPath("$[0].price").exists()
                .jsonPath("$[0].glaze").exists()
                .jsonPath("$[0].toppings").exists()
                .jsonPath("$[0].isVegan").exists()
                .jsonPath("$[0].calories").doesNotExist()
                .jsonPath("$[0].bakedAt").doesNotExist();
    }

    @Test
    void testInternalView() {
        // Call the internal endpoint and verify it includes all public + internal fields
        restTestClient.get()
                .uri("/api/donuts/internal")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].type").exists()
                .jsonPath("$[0].price").exists()
                .jsonPath("$[0].glaze").exists()
                .jsonPath("$[0].toppings").exists()
                .jsonPath("$[0].isVegan").exists()
                .jsonPath("$[0].calories").exists()
                .jsonPath("$[0].bakedAt").exists();
    }

    @Test
    void testAdminView() {
        // Call the admin endpoint and verify it returns all fields
        // (In this case, same as internal since all fields are exposed)
        restTestClient.get()
                .uri("/api/donuts/admin")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].type").exists()
                .jsonPath("$[0].price").exists()
                .jsonPath("$[0].glaze").exists()
                .jsonPath("$[0].toppings").exists()
                .jsonPath("$[0].isVegan").exists()
                .jsonPath("$[0].calories").exists()
                .jsonPath("$[0].bakedAt").exists();
    }

    @Test
    void testSummaryViewOnlyHasTwoFields() {
        // Verify that the summary view returns exactly the expected fields
        restTestClient.get()
                .uri("/api/donuts/summary")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(response -> {
                    String body = new String(response.getResponseBody());
                    // Summary should only contain type and price
                    assertThat(body).contains("\"type\"");
                    assertThat(body).contains("\"price\"");
                    assertThat(body).doesNotContain("\"glaze\"");
                    assertThat(body).doesNotContain("\"calories\"");
                    assertThat(body).doesNotContain("\"bakedAt\"");
                });
    }
}
