package fr.caranouga.expeditech.common.grids;

public enum GridType {
    ENERGY("energy")
    ;

    private final String name;

    GridType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
