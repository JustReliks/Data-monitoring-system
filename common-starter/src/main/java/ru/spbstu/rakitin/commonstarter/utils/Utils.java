package ru.spbstu.rakitin.commonstarter.utils;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class Utils {

    public String getParamsStringFromArray(List<?> list) {
        String listString = list.toString();
        return new StringBuilder(listString).substring(1, listString.length() - 1).toString();
    }

}
