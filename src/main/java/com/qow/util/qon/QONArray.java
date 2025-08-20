package com.qow.util.qon;

public record QONArray(String[] list) {

    public String get(int index) {
        return list[index];
    }
}
