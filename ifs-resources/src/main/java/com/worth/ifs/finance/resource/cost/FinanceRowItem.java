package com.worth.ifs.finance.resource.cost;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.math.BigDecimal;

/**
 * {@code FinanceRowItem} interface is used to handle the different type of costItems
 * for an application.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AcademicCost.class, name = "academic"),
        @JsonSubTypes.Type(value = CapitalUsage.class, name = "capitalUsage"),
        @JsonSubTypes.Type(value = GrantClaim.class, name = "grantClaim"),
        @JsonSubTypes.Type(value = LabourCost.class, name = "labourCost"),
        @JsonSubTypes.Type(value = Materials.class, name = "materials"),
        @JsonSubTypes.Type(value = OtherCost.class, name = "otherCost"),
        @JsonSubTypes.Type(value = OtherFunding.class, name = "otherFunding"),
        @JsonSubTypes.Type(value = Overhead.class, name = "overhead"),
        @JsonSubTypes.Type(value = SubContractingCost.class, name = "subContractingCost"),
        @JsonSubTypes.Type(value = TravelCost.class, name = "travelCost")
})
public interface FinanceRowItem {
    int MAX_DIGITS = 20;
    int MAX_DIGITS_INT = 10;
    int MAX_FRACTION = 8;
    int MAX_STRING_LENGTH = 255; // when to show the validation message and when to disable mark as complete
    int MAX_DB_STRING_LENGTH = 255; // max string length to send to the db.

    String MAX_LENGTH_MESSAGE = "{validation.field.too.many.characters}";
    String NOT_BLANK_MESSAGE = "{validation.field.must.not.be.blank}";
    String MAX_DIGITS_MESSAGE = "{validation.field.max.number.of.digits}";
    String VALUE_MUST_BE_HIGHER_MESSAGE = "{validation.field.max.value.or.higher}";
    String VALUE_MUST_BE_LOWER_MESSAGE = "{validation.field.max.value.or.lower}";

    public Long getId();
    public BigDecimal getTotal();
    public FinanceRowType getCostType();
    public String getName();
    public boolean isEmpty();
    public default boolean excludeInRowCount(){
        return isEmpty();
    }
    public int getMinRows();
}
