package org.innovateuk.ifs.setup.resource;

public enum ApplicationFinanceType {
    NONE("No finances"),
    LIGHT("Light finances"),
    FULL("Full application finances");

    private final String name;

    ApplicationFinanceType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
