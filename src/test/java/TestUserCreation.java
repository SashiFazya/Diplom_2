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

public class TestUserCreation extends UserMethods {
    private User user;

    @Before
    public void setUp() {
        user = UserGenerator.randomUser();
    }

    @Test
    @DisplayName("создать уникального пользователя")
    public void checkUniqueUserCreation() {
            createUser(user)
                .assertThat().statusCode(SC_OK)
                .assertThat().body("success", equalTo(true),
                        "accessToken", notNullValue());
    }

    @Test
    @DisplayName("создать пользователя, который уже зарегистрирован")
    public void checkExistUserCreation() {
        createUser(user).statusCode(SC_OK);
        createUser(user)
                .assertThat().statusCode(SC_FORBIDDEN)
                .assertThat().body("success", equalTo(false),
                        "message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("создать пользователя и не заполнить одно из обязательных полей - email")
    public void checkUserCreationWithoutEmail() {
        String noEmailJson = String.format("{\"password\": \"%s\", \"name\": \"%s\"}",
                user.getPassword(), user.getName());

        createCustomUser(noEmailJson)
                .assertThat().statusCode(SC_FORBIDDEN)
                .assertThat().body("success", equalTo(false),
                        "message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("создать пользователя и не заполнить одно из обязательных полей - password")
    public void checkUserCreationWithoutPassword() {
        String noPassJson = String.format("{\"email\": \"%s\", \"name\": \"%s\"}",
                user.getEmail(), user.getName());

        createCustomUser(noPassJson)
                .assertThat().statusCode(SC_FORBIDDEN)
                .assertThat().body("success", equalTo(false),
                        "message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("создать пользователя и не заполнить одно из обязательных полей - password")
    public void checkUserCreationWithoutName() {
        String noNameJson = String.format("{\"email\": \"%s\", \"password\": \"%s\"}",
                user.getEmail(), user.getPassword());

        createCustomUser(noNameJson)
                .assertThat().statusCode(SC_FORBIDDEN)
                .assertThat().body("success", equalTo(false),
                        "message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void cleanUp() {
        if (loginUser(user).extract().statusCode() == SC_OK)
            deleteUser(user).statusCode(SC_ACCEPTED);
    }
}
