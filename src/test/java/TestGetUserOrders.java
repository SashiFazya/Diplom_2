import api.client.OrderMethods;
import api.client.UserMethods;
import api.model.User;
import api.util.UserGenerator;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class TestGetUserOrders extends OrderMethods {
    private User user;
    private UserMethods userMethods;

    @Before
    public void SetUp() {
        user = UserGenerator.randomUser();
        userMethods = new UserMethods();
        userMethods.createUser(user);
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя:\n" +
            "авторизованный пользователь")
    public void checkGetAuthorizedUserOrders() {
        generateAuthUserOrder(user);
        String token = userMethods.getUserToken(user);
        getAuthorizedUserOrders(token)
                .assertThat().statusCode(SC_OK)
                .assertThat().body("orders", notNullValue(),
                        "success", equalTo(true));
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя:\n" +
            "неавторизованный пользователь")
    public void checkGetUnauthorizedUserOrders() {
        generateAuthUserOrder(user);
        String token = "";
        getAuthorizedUserOrders(token)
                .assertThat().statusCode(SC_UNAUTHORIZED)
                .assertThat().body("message", equalTo("You should be authorised"),
                        "success", equalTo(false));
    }

    @After
    public void cleanUp() {
        if (userMethods.loginUser(user).extract().statusCode() == SC_OK)
            userMethods.deleteUser(user).statusCode(SC_ACCEPTED);
    }
}
