package practicum;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import practicum.client.UserClient;
import practicum.pojo.UserLoginResponse;

public abstract class TestBase
{
    protected final UserClient userClient;

    public TestBase()
    {
        this.userClient = new UserClient();
    }

    @BeforeEach
    public void setUp()
    {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/api";
    }

    @Step("Delete test user")
    protected void cleanUpCreatedUser(String email, String password)
    {
        UserLoginResponse courierLoginResponse = userClient.loginByUser(email, password).thenReturn().as(UserLoginResponse.class);
        userClient.deleteUser(courierLoginResponse.getAccessToken()).then().statusCode(202);
    }

    @Step("Delete test user")
    protected void cleanUpCreatedUser(String accessToken)
    {
        userClient.deleteUser(accessToken).then().statusCode(202);
    }
}
