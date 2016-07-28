package com.worth.ifs.application.finance.model;

public class FinanceFormField {
    private String fieldName;
    private String costName;
    private String keyType;
    private String value;
    private String questionId;
    private String id;

    public FinanceFormField(String fieldName, String value, String id, String questionId, String costName, String keyType) {
        this.fieldName = fieldName;
        this.value = value;
        this.id = id;
        this.questionId = questionId;
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

    public String getQuestionId() {
        return questionId;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "FinanceFormField : " + this.fieldName + " " + this.costName +
                " " + this.value + " " + this.id + " " + this.questionId + " " + this.keyType;
    }
}
