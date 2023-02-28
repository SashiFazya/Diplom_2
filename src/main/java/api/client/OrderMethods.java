package api.client;

import api.model.Order;
import api.model.User;
import api.util.SetSpecification;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;

public class OrderMethods extends SetSpecification {
    private static final String CREATE_ORDER_URL = "/api/orders";
    private static final String GET_AUTH_USER_ORDERS = "/api/orders";
    private UserMethods userMethods = new UserMethods();

    @Step("Создать заказ без авторизации пользователя")
    public ValidatableResponse createOrderUnauthorized(Order order) {

        return given()
                .spec(getSpec())
                .body(order).log().all()
                .when()
                .post(CREATE_ORDER_URL)
                .then().log().all();
    }

    @Step("Создать заказ авторизованным пользователем {user.email}")
    public ValidatableResponse createOrderAuthorized(Order order, User user) {
        return given()
                .spec(getSpec())
                .header("Authorization", userMethods.getUserToken(user))
                .body(order).log().all()
                .when()
                .post(CREATE_ORDER_URL)
                .then().log().all();
    }

    @Step("Получить заказы конкретного пользователя")
    public ValidatableResponse getAuthorizedUserOrders(String token) {
        return given()
                .spec(getSpec())
                .header("Authorization", token).log().all()
                .when()
                .get(GET_AUTH_USER_ORDERS)
                .then().log().all();
    }

    @Step("Сгенерить тестовый заказ для пользователя {user.email}")
    public void generateAuthUserOrder(User user) {
        List<String> ingredients = List.of("61c0c5a71d1f82001bdaaa6c", "61c0c5a71d1f82001bdaaa73", "61c0c5a71d1f82001bdaaa79", "61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa7a");
        Order order = new Order();

        order.setIngredients(ingredients);
        createOrderAuthorized(order, user)
                .statusCode(SC_OK);
    }
}
