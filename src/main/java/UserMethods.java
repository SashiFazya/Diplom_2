import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserMethods extends SetSpecification {
    private static final String CREATE_USER_URL = "/api/auth/register";
    private static final String DELETE_USER_URL = "/api/auth/user";
    private static final String LOGIN_USER_URL = "/api/auth/login";
    private static final String GET_USER_INFO_URL = "/api/auth/user";
    private static final String UPDATE_USER_INFO_URL = "/api/auth/user";

    public ValidatableResponse createUser(User user) {

        return given()
                .spec(getSpec())
                .body(user).log().all()
                .when()
                .post(CREATE_USER_URL)
                .then();
    }

    public ValidatableResponse createCustomUser(String bodyJson) {

        return given()
                .spec(getSpec())
                .body(bodyJson).log().all()
                .when()
                .post(CREATE_USER_URL)
                .then();
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

    public ValidatableResponse loginUser(User user) {
        LoginData loginData = new LoginData(user);
        return given()
                .spec(getSpec())
                .body(loginData).log().all()
                .when()
                .post(LOGIN_USER_URL)
                .then().log().all();
    }

    public ValidatableResponse deleteUser(User user) {
        return given()
                .spec(getSpec())
                .header("Authorization", getUserToken(user)).log().all()
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

    public ValidatableResponse updateAuthorizedUserData(User user, String requestBody){
        return given()
                .spec(getSpec())
                .header("Authorization", getUserToken(user))
                .body(requestBody).log().all()
                .patch(UPDATE_USER_INFO_URL)
                .then();
    }

    public ValidatableResponse updateUnauthorizedUserData(User user, String requestBody){
        return given()
                .spec(getSpec())
                .body(requestBody).log().all()
                .patch(UPDATE_USER_INFO_URL)
                .then();
    }
}
