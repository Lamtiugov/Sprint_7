package ru.yandex.praktikum;
import com.google.gson.Gson;

public class Courier {
    private String login;
    private String password;
    private String firstName;

    public Courier(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }

    public String getLogin() {
        return "\"login\":\"" + login + '"';
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return "\"password\":\"" + password + '"';
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return "\"firstName\":\"" + firstName + '"';
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
