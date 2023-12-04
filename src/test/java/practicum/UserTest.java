package practicum;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import practicum.pojo.UserLoginResponse;

import java.util.Random;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;

public class UserTest extends TestBase
{
    @Test
    @DisplayName("Create uniq user")
    public void createUniqUser()
    {
        String email = "test_login_email_" + new Random().nextInt(10000) + "@yandex.ru";
        String password = "test_password";
        String name = "test_name";

        checkUserCreated(userClient.registerUser(email, password, name));
        cleanUpCreatedUser(email, password);
    }

    @Test
    @DisplayName("Create duplicate user")
    public void createDuplicateUser()
    {
        String email = "test_login_email_" + new Random().nextInt(10000) + "@yandex.ru";
        String password = "test_password";
        String name = "test_name";

        userClient.registerUser(email, password, name).then().statusCode(200);
        checkDuplicateUserCreateForbidden(userClient.registerUser(email, password, name));
        cleanUpCreatedUser(email, password);
    }

    public static Stream<Arguments> registerMissedParameters()
    {
        return Stream.of(
                Arguments.of("test_login_email@yandex.ru", "test_password", null),
                Arguments.of("test_login_email@yandex.ru", null, "test_name"),
                Arguments.of(null, "test_password", "test_name")
        );
    }

    @ParameterizedTest
    @MethodSource("registerMissedParameters")
    @DisplayName("Create user without required parameter is forbidden")
    public void createUserWithoutRequiredParameterIsForbidden(String email, String password, String name)
    {
        checkUserCreateParameterIsMissed(userClient.registerUser(email, password, name));
    }

    @Test
    @DisplayName("Login successful")
    public void loginSuccessful()
    {
        String email = "test_login_email_" + new Random().nextInt(10000) + "@yandex.ru";
        String password = "test_password";
        String name = "test_name";

        userClient.registerUser(email, password, name).then().statusCode(200);
        checkLoginSuccessful(userClient.loginByUser(email, password));
        cleanUpCreatedUser(email, password);
    }

    @Test
    @DisplayName("Login fail with wrong email")
    public void loginFailWithWrongEmail()
    {
        checkLoginFail(userClient.loginByUser("wrong_email@yandex.ru", "password"));
    }

    @Test
    @DisplayName("Login fail with empty email")
    public void loginFailWithEmptyEmail()
    {
        checkLoginFail(userClient.loginByUser(null, "password"));
    }

    @Test
    @DisplayName("Login fail with wrong password")
    public void loginFailWithWrongPassword()
    {
        String email = "test_login_email_" + new Random().nextInt(10000) + "@yandex.ru";
        String password = "test_password";
        String name = "test_name";

        userClient.registerUser(email, password, name).then().statusCode(200);
        checkLoginFail(userClient.loginByUser(email, "wrong_password"));
        cleanUpCreatedUser(email, password);
    }

    @Test
    @DisplayName("Login fail with empty password")
    public void loginFailWithEmptyPassword()
    {
        String email = "test_login_email_" + new Random().nextInt(10000) + "@yandex.ru";
        String password = "test_password";
        String name = "test_name";

        userClient.registerUser(email, password, name).then().statusCode(200);
        checkLoginFail(userClient.loginByUser(email, null));
        cleanUpCreatedUser(email, password);
    }

    @Test
    @DisplayName("Change user email successful")
    public void changeUserEmail()
    {
        String email = "test_login_email_" + new Random().nextInt(10000) + "@yandex.ru";
        String password = "test_password";
        String name = "test_name";

        String newEmail = "new_" + email;
        UserLoginResponse userLoginResponse = userClient.registerUser(email, password, name).thenReturn().as(UserLoginResponse.class);
        checkChangeUserParameterSuccessful(userClient.updateUser(userLoginResponse.getAccessToken(), newEmail, name), newEmail, name);
        cleanUpCreatedUser(userLoginResponse.getAccessToken());
    }

    @Test
    @DisplayName("Change user name successful")
    public void changeUserName()
    {
        String email = "test_login_email_" + new Random().nextInt(10000) + "@yandex.ru";
        String password = "test_password";
        String name = "test_name";

        String newName = "new_test_name";
        UserLoginResponse userLoginResponse = userClient.registerUser(email, password, name).thenReturn().as(UserLoginResponse.class);
        checkChangeUserParameterSuccessful(userClient.updateUser(userLoginResponse.getAccessToken(), email, newName), email, newName);
        cleanUpCreatedUser(userLoginResponse.getAccessToken());
    }

    @Test
    @DisplayName("Change user email fail")
    public void changeUserEmailFail()
    {
        String email = "test_login_email_" + new Random().nextInt(10000) + "@yandex.ru";
        String password = "test_password";
        String name = "test_name";

        String newEmail = "new_" + email;
        UserLoginResponse userLoginResponse = userClient.registerUser(email, password, name).thenReturn().as(UserLoginResponse.class);
        userClient.logoutByUser(userLoginResponse.getRefreshToken()).then().statusCode(200);
        checkChangeUserParameterFail(userClient.updateUser("", newEmail, name));
        cleanUpCreatedUser(email, password);
    }

    @Test
    @DisplayName("Change user name fail")
    public void changeUserNameFail()
    {
        String email = "test_login_email_" + new Random().nextInt(10000) + "@yandex.ru";
        String password = "test_password";
        String name = "test_name";

        String newName = "new_test_name";
        UserLoginResponse userLoginResponse = userClient.registerUser(email, password, name).thenReturn().as(UserLoginResponse.class);
        userClient.logoutByUser(userLoginResponse.getRefreshToken()).then().statusCode(200);
        checkChangeUserParameterFail(userClient.updateUser("", email, newName));
        cleanUpCreatedUser(email, password);
    }

    @Step("Check user created")
    private void checkUserCreated(Response response)
    {
        response.then()
                .statusCode(200)
                .and()
                .body("success", is(true));
    }

    @Step("Check duplicate user create forbidden")
    private void checkDuplicateUserCreateForbidden(Response response)
    {
        response.then()
                .statusCode(403)
                .and()
                .body("message", is("User already exists"))
                .and()
                .body("success", is(false));
    }

    @Step("Check user create parameter is missed")
    private void checkUserCreateParameterIsMissed(Response response)
    {
        response.then()
                .statusCode(403)
                .and()
                .body("message", is("Email, password and name are required fields"))
                .and()
                .body("success", is(false));
    }

    @Step("Check login successful")
    private void checkLoginSuccessful(Response response)
    {
        response.then()
                .statusCode(200)
                .and()
                .body("success", is(true));
    }

    @Step("Check user login parameter is wrong or missed")
    private void checkLoginFail(Response response)
    {
        response.then()
                .statusCode(401)
                .and()
                .body("message", is("email or password are incorrect"))
                .and()
                .body("success", is(false));
    }

    @Step("Check change user parameter successful")
    private void checkChangeUserParameterSuccessful(Response response, String email, String name)
    {
        response.then()
                .statusCode(200)
                .and()
                .body("user.email", is(email))
                .and()
                .body("user.name", is(name))
                .and()
                .body("success", is(true));
    }

    @Step("Check change user parameter fail")
    private void checkChangeUserParameterFail(Response response)
    {
        response.then()
                .statusCode(401)
                .and()
                .body("message", is("You should be authorised"))
                .and()
                .body("success", is(false));
    }
}
