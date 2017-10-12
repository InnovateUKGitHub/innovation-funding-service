package org.innovateuk.ifs.finance.resource.cost;

import org.innovateuk.ifs.form.resource.FormInputType;

import java.util.Optional;

import static java.util.Arrays.asList;

/**
 * FinanceRow types are used to identify the different categories that costs can have
 */
public enum FinanceRowType implements CostCategoryGenerator<FinanceRowType> {
    LABOUR("labour", true, "Labour"),
    OVERHEADS("overheads", true, "Overheads"),
    MATERIALS("materials", true, "Materials"),
    CAPITAL_USAGE("capital_usage", true, "Capital usage"),
    SUBCONTRACTING_COSTS("subcontracting", true, "Subcontracting"),
    TRAVEL("travel", true, "Travel and subsistence"),
    OTHER_COSTS("other_costs", true, "Other costs"),
    YOUR_FINANCE("your_finance"),
    FINANCE("finance", false, "Finance"),
    OTHER_FUNDING("other_funding", false, "Other Funding"),
    ACADEMIC("academic");

    private String type;
    private boolean spendCostCategory;
    private String name;

    FinanceRowType(String type) {
        this(type, false, null);
    }

    FinanceRowType(String type, boolean spendCostCategory, String name) {
        this.type = type;
        this.spendCostCategory = spendCostCategory;
        this.name = name;
    }

    public static FinanceRowType fromType(FormInputType formInputType) {
        if (formInputType != null) {
            for (FinanceRowType costType : values()) {
                if (formInputType == FormInputType.findByName(costType.getType())) {
                    return costType;
                }
            }
        }
        throw new IllegalArgumentException("No valid FinanceRowType found for FormInputType: " + formInputType);
    }

    public String getType() {
        return type;
    }

    public FormInputType getFormInputType() {
        return FormInputType.findByName(getType());
    }

    public boolean isSpendCostCategory() {
        return spendCostCategory;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    public static Optional<FinanceRowType> getByTypeName(String typeName) {
        return asList(FinanceRowType.values()).stream().filter(frt -> frt.getType().equals(typeName)).findFirst();
    }
}
