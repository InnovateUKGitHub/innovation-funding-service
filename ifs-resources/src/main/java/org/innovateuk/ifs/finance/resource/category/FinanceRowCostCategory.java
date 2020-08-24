package org.innovateuk.ifs.finance.resource.category;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;

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
        @JsonSubTypes.Type(value=ExcludedCostCategory.class, name="excludedCostCategory"),
        @JsonSubTypes.Type(value=AdditionalCompanyCostCategory.class, name="additionalCompanyCostCategory"),
        @JsonSubTypes.Type(value=VatCostCategory.class, name="vatCategory")
})
public interface FinanceRowCostCategory {
    List<FinanceRowItem> getCosts();
    BigDecimal getTotal();
    void calculateTotal();
    void addCost(FinanceRowItem costItem);
    boolean excludeFromTotalCost();
}
