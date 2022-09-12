package ru.geekbrains.lesson3;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;


import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class HomeTaskTest extends AbstractTest {

    @BeforeAll
    static void afterAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void useGetSpoonacular() {
        given()
                .queryParam("minProtein", 45)
                .queryParam("sort", "amount")
                .queryParam("sortDirection", "desc")
                .queryParam("number", "1")
                .queryParam("apiKey", getApiKey())
                .when()
                .get(getBaseUrl()+"recipes/complexSearch")
                .then()
                .statusCode(200);

        given()
                .queryParam("maxCalories", 200)
                .queryParam("sort", "amount")
                .queryParam("sortDirection", "asc")
                .queryParam("number", "1")
                .queryParam("apiKey", getApiKey())
                .when()
                .get(getBaseUrl()+"recipes/complexSearch")
                .then()
                .header("Content-Length", Integer::parseInt, lessThan(3000));

        given()
                .queryParam("minCalories", 100)
                .queryParam("sort", "amount")
                .queryParam("sortDirection", "desc")
                .queryParam("number", "1")
                .queryParam("apiKey", getApiKey())
                .when()
                .get(getBaseUrl()+"recipes/complexSearch")
                .then()
                .time(lessThan(2000L));

        given()
                .queryParam("maxReadyTime", 5)
                .queryParam("number", "1")
                .queryParam("apiKey", getApiKey())
                .when()
                .get(getBaseUrl()+"recipes/complexSearch")
                .then()
                .statusLine(containsString("OK"));

        given()
                .queryParam("includeIngredients", "meat")
                .queryParam("number", "1")
                .queryParam("apiKey", getApiKey())
                .when()
                .get(getBaseUrl()+"recipes/complexSearch")
                .then()
                .statusLine("HTTP/1.1 200 OK");

        given()
                .queryParam("cuisine", "italian")
                .queryParam("number", "1")
                .queryParam("apiKey", getApiKey())
                .when()
                .get(getBaseUrl()+"recipes/complexSearch")
                .then()
                .header("Connection", "keep-alive");
    }


    @Test
    void usePostSpoonacular() {
        given()
                .queryParam("apiKey", getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title","Falafel Burger")
                .when()
                .post(getBaseUrl()+"recipes/cuisine")
                .then()
                .statusCode(200);

        given()
                .queryParam("apiKey", getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title","Tex-Mex Burger")
                .when()
                .post(getBaseUrl()+"recipes/cuisine")
                .then()
                .statusLine("HTTP/1.1 200 OK");

        given()
                .queryParam("apiKey", getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title","Ratatouille")
                .when()
                .post(getBaseUrl()+"recipes/cuisine")
                .then()
                .statusLine(containsString("OK"));

        JsonPath response = given()
                .queryParam("apiKey", getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title","Sushi")
                .when()
                .post(getBaseUrl()+"recipes/cuisine")
                .body()
                .jsonPath();

        assertThat(response.get("cuisine"), equalTo("Japanese"));
        assertThat(response.get("confidence"), equalTo(0.85));
        given()
                .queryParam("apiKey", getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title","Pudding")
                .formParam("title","ingredientList")
                .when()
                .post(getBaseUrl()+"recipes/cuisine")
                .then()
                .time(lessThan(2000L));

        response = given()
                .queryParam("apiKey", getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title","Beer")
                .when()
                .post(getBaseUrl()+"recipes/cuisine")
                .body()
                .jsonPath();

        assertThat(response.get("cuisine"), equalTo("Mediterranean"));


    }

    @Test
    void useMealPlan() {
        String id = given()
                .queryParam("hash", "dabfa8dac3933af397c87daf4877ede2b160b8bb")
                .queryParam("apiKey", getApiKey())
                .pathParam("username", "pushshok")
                .body("{\n"
                        + " \"date\": 12092022,\n"
                        + " \"slot\": 1,\n"
                        + " \"position\": 0,\n"
                        + " \"type\": \"INGREDIENTS\",\n"
                        + " \"value\": {\n"
                        + " \"ingredients\": [\n"
                        + " {\n"
                        + " \"name\": \"1 apple\"\n"
                        + " }\n"
                        + " ]\n"
                        + " }\n"
                        + "}")
                .when()
                .post("https://api.spoonacular.com/mealplanner/{username}/items")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("id")
                .toString();

        given()
                .queryParam("hash", "dabfa8dac3933af397c87daf4877ede2b160b8bb")
                .queryParam("apiKey", getApiKey())
                .pathParam("username", "pushshok")
                .delete("https://api.spoonacular.com/mealplanner/{username}/items/" + id)
                .then()
                .statusCode(200);
    }
}
