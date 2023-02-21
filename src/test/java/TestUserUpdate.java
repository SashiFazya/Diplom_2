import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class TestUserUpdate extends UserMethods{
    private User user;

    @Before
    public void setUp() {
        user = UserGenerator.randomUser();
        createUser(user);
    }

    @Test
    @DisplayName("Изменение данных пользователя:" +
            "с авторизацией, update email")
    public void checkUserUpdateEmail() {
        String originalEmail = user.getEmail();
        String originalName = user.getName();
        String newEmail = "another_" + originalEmail;
        String json = String.format("{\"email\": \"%s\"}",
                newEmail);

        updateAuthorizedUserData(user, json)
                .assertThat().statusCode(SC_OK)
                .assertThat().body("success", equalTo(true),
                        "user.email", equalTo(newEmail),
                        "user.name", equalTo(originalName))
                .log().all();

        //для удаления обновим данные юзера
        user.setEmail(newEmail);
    }

    @Test
    @DisplayName("Изменение данных пользователя:" +
            "с авторизацией, update name")
    public void checkUserUpdateName() {
        String originalEmail = user.getEmail();
        String originalName = user.getName();
        String newName = "another_" + originalName;
        String json = String.format("{\"name\": \"%s\"}",
                newName);

        updateAuthorizedUserData(user, json)
                .assertThat().statusCode(SC_OK)
                .assertThat().body("success", equalTo(true),
                        "user.email", equalTo(originalEmail),
                        "user.name", equalTo(newName))
                .log().all();

        //для удаления обновим данные юзера
        user.setName(newName);
    }

    @Test
    @DisplayName("Изменение данных пользователя:" +
            "без авторизации, update email")
    public void checkUnauthorizedUserUpdateEmail() {
        String originalEmail = user.getEmail();
        String newEmail = "another_" + originalEmail;
        String json = String.format("{\"email\": \"%s\"}",
                newEmail);

        updateUnauthorizedUserData(user, json)
                .assertThat().statusCode(SC_UNAUTHORIZED)
                .assertThat().body("success", equalTo(false),
                        "message", equalTo("You should be authorised"))
                .log().all();
    }

    @Test
    @DisplayName("Изменение данных пользователя:" +
            "без авторизации, update name")
    public void checkUnauthorizedUserUpdateName() {
        String originalName = user.getName();
        String newName = "another_" + originalName;
        String json = String.format("{\"name\": \"%s\"}",
                newName);

        updateUnauthorizedUserData(user, json)
                .assertThat().statusCode(SC_UNAUTHORIZED)
                .assertThat().body("success", equalTo(false),
                        "message", equalTo("You should be authorised"))
                .log().all();
    }

    @After
    public void cleanUp() {
        if (loginUser(user).extract().statusCode() == SC_OK)
            deleteUser(user).statusCode(SC_ACCEPTED);
        else System.out.println("не удалю");
    }
}
