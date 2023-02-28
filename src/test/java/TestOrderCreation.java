import api.client.OrderMethods;
import api.client.UserMethods;
import api.model.Order;
import api.model.User;
import api.util.UserGenerator;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class TestOrderCreation extends OrderMethods {
    private Order order;
    private User user;
    private UserMethods userMethods;
    private String accessToken;

    @Before
    public void SetUp() {
        order = new Order();
        user = UserGenerator.randomUser();
        userMethods = new UserMethods();
        userMethods.createUser(user);
        accessToken = userMethods.getUserToken(user);
    }

    @Test
    @DisplayName("Создание заказа:" +
            "без авторизации, без ингредиентов")
    public void checkUnauthorizedOrderCreationWithoutIngredients() {
        List<String> ingredients = List.of();
        order.setIngredients(ingredients);

        createOrderUnauthorized(order)
                .assertThat().statusCode(SC_BAD_REQUEST)
                .assertThat().body("message", equalTo("Ingredient ids must be provided"),
                        "success", equalTo(false));
    }

    @Test
    @DisplayName("Создание заказа:" +
            "без авторизации, с ингредиентами")
    public void checkUnauthorizedOrderCreationWithIngredients() {
        List<String> ingredients = List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa72", "61c0c5a71d1f82001bdaaa77");
        String name = "Фалленианский spicy флюоресцентный бургер";

        order.setIngredients(ingredients);

        createOrderUnauthorized(order)
                .assertThat().statusCode(SC_OK)
                .assertThat().body("name", equalTo(name),
                        "success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа:" +
            "без авторизации, с неверным хешем ингредиентов")
    public void checkUnauthorizedOrderCreationWithWrongIngredients() {
        List<String> ingredients = List.of("abrada6ra", "krak0zyabra", "shmurgelburger56");

        order.setIngredients(ingredients);

        createOrderUnauthorized(order)
                .assertThat().statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Создание заказа:" +
            "без авторизации, с неверным хешем ингредиентов" +
            "Баг: неожиданное поведение при неверном хеше начинки, но с верной булочкой")
    public void checkUnauthorizedOrderCreationWithWrongIngredients2() {
        List<String> ingredients = List.of("61c0c5a71d1f82001bdaaa6d", "00c0c0a0000000000bdaaa00", "00c0c0a00d0f00000bdaaa0e");

        order.setIngredients(ingredients);

        createOrderUnauthorized(order)
                .assertThat().statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Создание заказа:" +
            "без авторизации, с неверным хешем ингредиентов" +
            "Баг: неожиданное поведение при неверном хеше начинки и с верной булочкой")
    public void checkUnauthorizedOrderCreationWithWrongIngredients3() {
        List<String> ingredients = List.of("00c0c5a71d1f82001bdaaa6d", "00c0c0a0000000000bdaaa00", "00c0c0a00d0f00000bdaaa0e");

        order.setIngredients(ingredients);

        createOrderUnauthorized(order)
                .assertThat().statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Создание заказа:" +
            "с авторизацией, без ингредиентов")
    public void checkAuthorizedOrderCreationWithoutIngredients() {
        List<String> ingredients = List.of();
        order.setIngredients(ingredients);

        createOrderAuthorized(order, user)
                .assertThat().statusCode(SC_BAD_REQUEST)
                .assertThat().body("message", equalTo("Ingredient ids must be provided"),
                        "success", equalTo(false));
    }

    @Test
    @DisplayName("Создание заказа:" +
            "с авторизацией, с ингредиентами")
    public void checkAuthorizedOrderCreationWithIngredients() {
        List<String> ingredients = List.of("61c0c5a71d1f82001bdaaa6c", "61c0c5a71d1f82001bdaaa73", "61c0c5a71d1f82001bdaaa79", "61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa7a");
        String name = "Экзо-плантаго space краторный бессмертный астероидный бургер";

        order.setIngredients(ingredients);
        createOrderAuthorized(order, user)
                .assertThat().statusCode(SC_OK)
                .assertThat().body("name", equalTo(name),
                        "success", equalTo(true),
                        "order.owner.email", equalTo(user.getEmail()));
    }

    @Test
    @DisplayName("Создание заказа:" +
            "с авторизацией, с неверным хешем ингредиентов")
    public void checkAuthorizedOrderCreationWithWrongIngredients() {
        List<String> ingredients = List.of("abrada6ra", "krak0zyabra", "shmurgelburger56");

        order.setIngredients(ingredients);

        createOrderAuthorized(order, user)
                .assertThat().statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @After
    public void cleanUp() {
        if (accessToken != null)
            userMethods.deleteUser(accessToken).statusCode(SC_ACCEPTED);
    }
}
