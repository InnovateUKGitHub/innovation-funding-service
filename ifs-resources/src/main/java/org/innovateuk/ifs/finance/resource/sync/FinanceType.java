package org.innovateuk.ifs.finance.resource.sync;

/**
 * Unique names to identify application or project finances for communications between the finance-data-service.
 */
public enum FinanceType {
    APPLICATION("APPLICATION"),
    PROJECT("PROJECT");

    private String name;

    FinanceType(String typeName) {
        this.name = typeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
