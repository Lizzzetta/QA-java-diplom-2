package practicum.client;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import practicum.pojo.*;

import static io.restassured.RestAssured.given;

public class UserClient
{
    private static final String AUTH_REGISTER_PATH = "/auth/register";
    private static final String AUTH_LOGIN_PATH = "/auth/login";
    private static final String AUTH_LOGOUT_PATH = "/auth/logout";
    private static final String AUTH_USER_PATH = "/auth/user";

    @Step("Register user")
    public Response registerUser(String email, String password, String name)
    {
        return given()
                .when()
                .contentType(ContentType.JSON)
                .body(new RegisterUserParameters(email, password, name))
                .post(AUTH_REGISTER_PATH);
    }

    @Step("Login by user")
    public Response loginByUser(String email, String password)
    {
        return given()
                .when()
                .contentType(ContentType.JSON)
                .body(new LoginUserParameters(email, password))
                .post(AUTH_LOGIN_PATH);
    }

    @Step("Logout user")
    public Response logoutByUser(String refreshToken)
    {
        return given()
                .when()
                .contentType(ContentType.JSON)
                .body(new LogoutParameters(refreshToken))
                .post(AUTH_LOGOUT_PATH);
    }

    @Step("Delete user")
    public Response deleteUser(String accessToken)
    {
        return given()
                .when()
                .header("Authorization", accessToken)
                .delete(AUTH_USER_PATH);
    }

    @Step("Update user")
    public Response updateUser(String accessToken, String email, String name)
    {
        return given()
                .when()
                .header("Authorization", accessToken)
                .contentType(ContentType.JSON)
                .body(new UpdateUserParameters(email, name))
                .patch(AUTH_USER_PATH);
    }
}
