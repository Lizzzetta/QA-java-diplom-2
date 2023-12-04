package practicum.client;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import practicum.pojo.CreateOrderParameters;

import static io.restassured.RestAssured.given;

public class OrderClient
{
    private static final String ORDERS_PATH = "/orders";

    @Step("Create order by non authorized user")
    public Response createOrder(CreateOrderParameters createOrderParameters)
    {
        return given()
                .when()
                .contentType(ContentType.JSON)
                .body(createOrderParameters)
                .post(ORDERS_PATH);
    }

    @Step("Create order by authorized user")
    public Response createOrder(String accessToken, CreateOrderParameters createOrderParameters)
    {
        return given()
                .when()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body(createOrderParameters)
                .post(ORDERS_PATH);
    }

    @Step("Get user orders")
    public Response getUserOrders(String accessToken)
    {
        return given()
                .when()
                .header("Authorization", accessToken)
                .get(ORDERS_PATH);
    }
}
