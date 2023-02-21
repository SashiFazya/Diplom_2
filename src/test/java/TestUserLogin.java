import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class TestUserLogin extends UserMethods{
    private User user;

    @Before
    public void setUp() {
        user = UserGenerator.randomUser();
        createUser(user);
    }

    @Test
    @DisplayName("логин под существующим пользователем")
    public void checkExistUserLogin() {
        loginUser(user)
                .assertThat().statusCode(SC_OK)
                .assertThat().body("success", equalTo(true),
                        "accessToken", notNullValue())
                .log().all();
    }

    @Test
    @DisplayName("логин с неверным логином")
    public void checkWrongEmailUserLogin() {
        String correctEmail = user.getEmail();
        user.setEmail("wrong@email.ru");
        loginUser(user)
                .assertThat().statusCode(SC_UNAUTHORIZED)
                .assertThat().body("success", equalTo(false),
                        "message", equalTo("email or password are incorrect"))
                .log().all();

        //для удаления пользователя вернем верный email
        user.setPassword(correctEmail);
    }

    @Test
    @DisplayName("логин с неверным паролем")
    public void checkWrongPassUserLogin() {
        String correctPass = user.getPassword();
        user.setPassword("wrongPass123");
        loginUser(user)
                .log().all()
                .assertThat().statusCode(SC_UNAUTHORIZED)
                .assertThat().body("success", equalTo(false),
                        "message", equalTo("email or password are incorrect"))
                .log().all();

        //для удаления пользователя вернем верный пароль
        user.setPassword(correctPass);
    }

    @After
    public void cleanUp() {
        if (loginUser(user).extract().statusCode() == SC_OK)
            deleteUser(user).statusCode(SC_ACCEPTED);
        else System.out.println("не удалю");
    }
}
