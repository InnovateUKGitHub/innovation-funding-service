package org.innovateuk.ifs.finance.resource.category;

/**
 * Enumn describing the type of change to a finance row / cost.
 */
public enum TypeOfChange {
    NEW("New"), REMOVE("Remove"), CHANGE("Change");

    private String typeName;

    TypeOfChange(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
