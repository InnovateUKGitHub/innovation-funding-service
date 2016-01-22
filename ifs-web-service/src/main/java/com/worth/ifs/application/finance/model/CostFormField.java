package com.worth.ifs.application.finance.model;

public class CostFormField {
    String fieldName;
    String costName;
    String keyType;
    String value;
    String id;

    public CostFormField(String fieldName, String value, String id, String costName, String keyType) {
        this.fieldName = fieldName;
        this.value = value;
        this.id = id;
        this.keyType = keyType;
        this.costName = costName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getKeyType() {
        return keyType;
    }

    public String getCostName() {
        return costName;
    }

    public String getValue() {
        return value;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "CostFormField : " + this.fieldName + " " + this.costName +
                " " + this.value + " " + this.id + " " + this.keyType;
    }
}
