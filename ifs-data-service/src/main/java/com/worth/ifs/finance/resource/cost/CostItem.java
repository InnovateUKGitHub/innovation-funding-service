package com.worth.ifs.finance.resource.cost;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.math.BigDecimal;

/**
 * {@code CostItem} interface is used to handle the different type of costItems
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
public interface CostItem {
    int MAX_DIGITS = 20;
    public Long getId();
    public BigDecimal getTotal();
    public CostType getCostType();
    public String getName();
}
