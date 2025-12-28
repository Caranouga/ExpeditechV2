package fr.caranouga.expeditech.common.utils;

public enum Locale {
    EN_US("en_us"),
    FR_FR("fr_fr")
    ;

    private final String name;

    Locale(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
