package api.client;

import api.model.LoginData;
import api.model.User;
import api.util.SetSpecification;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserMethods extends SetSpecification {
    private static final String CREATE_USER_URL = "/api/auth/register";
    private static final String DELETE_USER_URL = "/api/auth/user";
    private static final String LOGIN_USER_URL = "/api/auth/login";
    private static final String GET_USER_INFO_URL = "/api/auth/user";
    private static final String UPDATE_USER_INFO_URL = "/api/auth/user";

    @Step("Создать пользователя {user.email}, {user.password}, {user.name}")
    public ValidatableResponse createUser(User user) {

        return given()
                .spec(getSpec())
                .body(user).log().all()
                .when()
                .post(CREATE_USER_URL)
                .then().log().all();
    }

    @Step("Создать пользователя {bodyJson}")
    public ValidatableResponse createCustomUser(String bodyJson) {

        return given()
                .spec(getSpec())
                .body(bodyJson).log().all()
                .when()
                .post(CREATE_USER_URL)
                .then().log().all();
    }

    public String getUserToken(User user) {
        LoginData loginData = new LoginData(user);
        return given()
                .spec(getSpec())
                .body(loginData).log().all()
                .when()
                .post(LOGIN_USER_URL)
                .path("accessToken");
    }

    @Step("Залогиниться пользователем {user.email}, {user.password}")
    public ValidatableResponse loginUser(User user) {
        LoginData loginData = new LoginData(user);
        return given()
                .spec(getSpec())
                .body(loginData).log().all()
                .when()
                .post(LOGIN_USER_URL)
                .then().log().all();
    }

    @Step("Удалить пользователя {user.email}")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken).log().all()
                .delete(DELETE_USER_URL)
                .then().log().all();
    }

    public Response getUserData(User user) {
        return given()
                .spec(getSpec())
                .header("Authorization", getUserToken(user))
                .get(GET_USER_INFO_URL);
    }

    public String getUserEmail(User user){
        Response response = getUserData(user);
        return response.path("email");
    }

    public String getUserName(User user){
        Response response = getUserData(user);
        return response.path("name");
    }

    @Step("Попытаться обновить данные авторизованного пользователя {user.email}")
    public ValidatableResponse updateAuthorizedUserData(User user, String requestBody){
        return given()
                .spec(getSpec())
                .header("Authorization", getUserToken(user))
                .body(requestBody).log().all()
                .patch(UPDATE_USER_INFO_URL)
                .then().log().all();
    }

    @Step("Попытаться обновить данные без авторизации пользователя")
    public ValidatableResponse updateUnauthorizedUserData(User user, String requestBody){
        return given()
                .spec(getSpec())
                .body(requestBody).log().all()
                .patch(UPDATE_USER_INFO_URL)
                .then().log().all();
    }
}
