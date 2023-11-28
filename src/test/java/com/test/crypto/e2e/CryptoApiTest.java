package com.test.crypto.e2e;

import com.test.crypto.model.HighestNormalizedData;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("it")
class CryptoApiTest {

    private final static String BASE_URI = "http://localhost";

    @LocalServerPort
    private int port;

    @BeforeEach
    public void configureRestAssured() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = port;
    }

    @Test
    void shouldThrowCurrencyUnsupportedException() {
        given().queryParam("currency", "ABCD")
                .get("/api/statistics")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", Matchers.is("Currency not supported"));
    }

    @Test
    void shouldThrowInvalidDateException() {
        given().queryParam("date", "2022-21-01")
                .get("/api/max-normalized-range")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", Matchers.startsWith("Date provided is invalid and must be in format YYYY-MM-dd"));
    }

    @Test
    void shouldThrowRecordMissingException() {
        given().queryParam("date", "2023-03-01")
                .get("/api/max-normalized-range")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", Matchers.startsWith("No record found in csv"));
    }



    @Test
    void shouldFetchStatistics() {
        given().queryParam("currency", "BTC")
                .get("/api/statistics")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("oldestPrice", Matchers.is(46813.21F))
                .body("newestPrice", Matchers.is(38415.79F))
                .body("minPrice", Matchers.is(33276.59F))
                .body("maxPrice", Matchers.is(47722.66F));
    }

    @Test
    void shouldFindHighestNormalizedCrypto() {
        given().queryParam("date", "2022-01-03")
                .get("/api/max-normalized-range")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("currency", Matchers.is("ETH"))
                .body("normalizedRange", Matchers.is(1.00F));
    }

    @Test
    void shouldFetchsSortedNormalizedList() {
        List<HighestNormalizedData> response = given().get("/api/normalized-list").
                body().as(new TypeRef<>() {
                });

        Assertions.assertEquals(5, response.size());
    }

}