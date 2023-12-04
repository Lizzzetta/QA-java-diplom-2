package practicum;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import practicum.client.*;
import practicum.pojo.*;

import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsEmptyCollection.empty;

public class OrderTest extends TestBase
{
    private final OrderClient orderClient;
    private final IngredientsClient ingredientsClient;

    public OrderTest()
    {
        super();
        this.orderClient = new OrderClient();
        this.ingredientsClient = new IngredientsClient();
    }

    @Test
    @DisplayName("Create order by authorized user")
    public void createOrderByAuthorizedUser()
    {
        String email = "test_login_email_" + new Random().nextInt(10000) + "@yandex.ru";
        String password = "test_password";
        String name = "test_name";

        UserLoginResponse userLoginResponse = userClient.registerUser(email, password, name).thenReturn().as(UserLoginResponse.class);
        List<Ingredient> ingredients = ingredientsClient.getIngredients();

        checkOrderCreatedSuccessful(
                orderClient.createOrder(
                        userLoginResponse.getAccessToken(),
                        new CreateOrderParameters(chooseRandomIngredients(ingredients))
                )
        );

        cleanUpCreatedUser(userLoginResponse.getAccessToken());
    }

    @Test
    @DisplayName("Create order by not authorized user")
    public void createOrderByNotAuthorizedUser()
    {
        List<Ingredient> ingredients = ingredientsClient.getIngredients();

        checkOrderCreatedSuccessful(
                orderClient.createOrder(
                        new CreateOrderParameters(chooseRandomIngredients(ingredients))
                )
        );
    }

    @Test
    @DisplayName("Create order by authorized user without ingredients is fail")
    public void createOrderByAuthorizedUserWithoutIngredients()
    {
        String email = "test_login_email_" + new Random().nextInt(10000) + "@yandex.ru";
        String password = "test_password";
        String name = "test_name";

        UserLoginResponse userLoginResponse = userClient.registerUser(email, password, name).thenReturn().as(UserLoginResponse.class);
        checkOrderNotCreatedWithoutIngredients(
                orderClient.createOrder(
                        userLoginResponse.getAccessToken(),
                        new CreateOrderParameters(Collections.emptyList())
                )
        );

        cleanUpCreatedUser(userLoginResponse.getAccessToken());
    }

    @Test
    @DisplayName("Create order by not authorized user without ingredients is fail")
    public void createOrderByNotAuthorizedUserWithoutIngredients()
    {
        checkOrderNotCreatedWithoutIngredients(
                orderClient.createOrder(
                        new CreateOrderParameters(Collections.emptyList())
                )
        );
    }

    @Test
    @DisplayName("Create order by authorized user with wrong ingredients is fail")
    public void createOrderByAuthorizedUserWithWrongIngredients()
    {
        String email = "test_login_email_" + new Random().nextInt(10000) + "@yandex.ru";
        String password = "test_password";
        String name = "test_name";

        UserLoginResponse userLoginResponse = userClient.registerUser(email, password, name).thenReturn().as(UserLoginResponse.class);
        checkOrderNotCreatedWithWrongIngredients(
                orderClient.createOrder(
                        userLoginResponse.getAccessToken(),
                        new CreateOrderParameters(Collections.singletonList("wrong_hash_42"))
                )
        );

        cleanUpCreatedUser(userLoginResponse.getAccessToken());
    }

    @Test
    @DisplayName("Create order by not authorized user with wrong ingredients is fail")
    public void createOrderByNotAuthorizedUserWithWrongIngredients()
    {
        checkOrderNotCreatedWithWrongIngredients(
                orderClient.createOrder(
                        new CreateOrderParameters(Collections.singletonList("wrong_hash_42"))
                )
        );
    }

    @Test
    @DisplayName("Get orders for authorized user")
    public void getOrdersForAuthorizedUser()
    {
        String email = "test_login_email_" + new Random().nextInt(10000) + "@yandex.ru";
        String password = "test_password";
        String name = "test_name";

        UserLoginResponse userLoginResponse = userClient.registerUser(email, password, name).thenReturn().as(UserLoginResponse.class);
        List<Ingredient> ingredients = ingredientsClient.getIngredients();

        orderClient.createOrder(
                userLoginResponse.getAccessToken(),
                new CreateOrderParameters(chooseRandomIngredients(ingredients))
        ).then().statusCode(200);

        checkOrdersForAuthorizedUser(orderClient.getUserOrders(userLoginResponse.getAccessToken()));

        cleanUpCreatedUser(userLoginResponse.getAccessToken());
    }

    @Test
    @DisplayName("Get orders for not authorized user is fail")
    public void getOrdersForNotAuthorizedUserIsFail()
    {
        checkGetOrdersFailForNotAuthorizedUser(orderClient.getUserOrders(""));
    }

    @Step("Check order created successful")
    private void checkOrderCreatedSuccessful(Response response)
    {
        response.then()
                .statusCode(200)
                .and()
                .body("order.number", notNullValue());
    }

    @Step("Check order not created without ingredients")
    private void checkOrderNotCreatedWithoutIngredients(Response response)
    {
        response.then()
                .statusCode(400)
                .and()
                .body("success", is(false))
                .and()
                .body("message", is("Ingredient ids must be provided"));
    }

    @Step("Check order not created with wrong ingredients")
    private void checkOrderNotCreatedWithWrongIngredients(Response response)
    {
        response.then().statusCode(500);
    }

    @Step("Check orders for authorized user")
    private void checkOrdersForAuthorizedUser(Response response)
    {
        response.then()
                .statusCode(200)
                .and()
                .body("orders", is(not(empty())));
    }

    @Step("Check get orders fail for not authorized user")
    private void checkGetOrdersFailForNotAuthorizedUser(Response response)
    {
        response.then()
                .statusCode(401)
                .and()
                .body("success", is(false))
                .and()
                .body("message", is("You should be authorised"));
    }

    @Step("Choose random ingredients")
    private List<String> chooseRandomIngredients(List<Ingredient> candidates)
    {
        int count = new Random().nextInt(candidates.size());
        List<String> result = new ArrayList<>();
        for (int i = 0; i < count; i++)
        {
            result.add(candidates.get(new Random().nextInt(candidates.size())).get_id());
        }
        return result;
    }
}
