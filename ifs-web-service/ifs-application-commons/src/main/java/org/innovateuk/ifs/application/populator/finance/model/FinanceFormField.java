package org.innovateuk.ifs.application.populator.finance.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final FinanceFormField that = (FinanceFormField) o;

        return new EqualsBuilder()
                .append(fieldName, that.fieldName)
                .append(costName, that.costName)
                .append(keyType, that.keyType)
                .append(value, that.value)
                .append(questionId, that.questionId)
                .append(id, that.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fieldName)
                .append(costName)
                .append(keyType)
                .append(value)
                .append(questionId)
                .append(id)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "FinanceFormField : " + this.fieldName + " " + this.costName +
                " " + this.value + " " + this.id + " " + this.questionId + " " + this.keyType;
    }
}
