package ru.yandex.praktikum.constants;

public enum CourierJsonFields {
    LOGIN("login"),
    PASSWORD("password"),
    FIRST_NAME("firstName");

    private String value;

    CourierJsonFields(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
