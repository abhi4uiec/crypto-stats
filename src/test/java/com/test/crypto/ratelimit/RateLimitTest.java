package com.test.crypto.ratelimit;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("rate-limit")
class RateLimitTest {

    private final static String BASE_URI = "http://localhost";

    @LocalServerPort
    private int port;

    @BeforeEach
    public void configureRestAssured() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = port;
    }

    @Test
    void shouldFailOnExceedingRateLimitSet() {
        // first 4 times will pass
        IntStream.range(0, 4).forEach(i -> given().queryParam("currency", "BTC")
                .get("/api/statistics")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("oldestPrice", Matchers.is(46813.21F))
                .body("newestPrice", Matchers.is(38415.79F))
                .body("minPrice", Matchers.is(33276.59F))
                .body("maxPrice", Matchers.is(47722.66F)));

        // next time it'll fail because of rate limit
        given().queryParam("currency", "BTC")
                .get("/api/statistics")
                .then()
                .assertThat()
                .statusCode(HttpStatus.TOO_MANY_REQUESTS.value())
                .body("message", Matchers.is("You have exhausted your API Request Quota"));
    }

}