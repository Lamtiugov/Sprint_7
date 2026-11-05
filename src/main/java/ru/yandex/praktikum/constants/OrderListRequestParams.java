package ru.yandex.praktikum.constants;

public enum OrderListRequestParams {
        COURIER_ID_NUM("courierId"),
        NEAREST_STATION_STRING_LIST("nearestStation"),
        LIMIT_NUM("limit"),
        PAGE_NUM("page");

        private String value;
        OrderListRequestParams(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }