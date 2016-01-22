package com.worth.ifs.finance.resource.category;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.LabourCost;

import java.math.BigDecimal;
import java.util.List;

/**
 * {@code CostCategory} interface is for defined for retrieving updating and calculating costs
 * of which a cost category consists.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes({
        @JsonSubTypes.Type(value=DefaultCostCategory.class, name="defaultCostCategory"),
        @JsonSubTypes.Type(value=LabourCostCategory.class, name="labourCostCategory"),
        @JsonSubTypes.Type(value=OtherFundingCostCategory.class, name="otherFundingCostCategory"),

})
public interface CostCategory {
    public List<CostItem> getCosts();

    public BigDecimal getTotal();
    public void calculateTotal();
    public void addCost(CostItem costItem);
    public boolean excludeFromTotalCost();
    public void setCosts(List<CostItem> costItems);
}
