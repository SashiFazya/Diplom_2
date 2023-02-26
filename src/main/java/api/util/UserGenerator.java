package api.util;

import api.model.User;
import com.github.javafaker.Faker;

public class UserGenerator {
    private static String email;
    private static String password;
    private static String name;

    private static Faker faker = new Faker();

    public static User randomUser() {
        password = faker.internet().password(true);
        name = faker.name().username();
        email = name + (int) (Math.random() * 100) + "@yandex.ru";

        return new User(email, password, name);
    }
}
