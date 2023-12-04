package practicum.client;

import io.qameta.allure.Step;
import practicum.pojo.*;

import java.util.List;

import static io.restassured.RestAssured.given;

public class IngredientsClient
{
    private static final String INGREDIENTS_PATH = "/ingredients";

    @Step("Get ingredients")
    public List<Ingredient> getIngredients()
    {
        return given()
                .when()
                .get(INGREDIENTS_PATH)
                .thenReturn().as(IngredientsResponse.class).getData();
    }
}
