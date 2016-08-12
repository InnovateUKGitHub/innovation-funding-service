package com.worth.ifs.finance.resource.category;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;

import java.math.BigDecimal;
import java.util.List;

/**
 * {@code FinanceRowCostCategory} interface is for defined for retrieving updating and calculating costs
 * of which a cost Category consists.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes({
        @JsonSubTypes.Type(value=DefaultCostCategory.class, name="defaultCostCategory"),
        @JsonSubTypes.Type(value=LabourCostCategory.class, name="labourCostCategory"),
        @JsonSubTypes.Type(value=OtherFundingCostCategory.class, name="otherFundingCostCategory"),
        @JsonSubTypes.Type(value=OverheadCostCategory.class, name="overheadCostCategory"),
        @JsonSubTypes.Type(value=GrantClaimCategory.class, name="grantClaimCategory")
})
public interface FinanceRowCostCategory {
    public List<FinanceRowItem> getCosts();

    public BigDecimal getTotal();
    public void calculateTotal();
    public void addCost(FinanceRowItem costItem);
    public boolean excludeFromTotalCost();
    public void setCosts(List<FinanceRowItem> costItems);
}
