package com.worth.ifs.finance.resource.cost;

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
    FINANCE("finance"),
    OTHER_FUNDING("other_funding"),
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

    public static FinanceRowType fromString(String type) {
        if(type!=null) {
            for(FinanceRowType costType : FinanceRowType.values()) {
                if(type.equalsIgnoreCase(costType.type)) {
                    return costType;
                }
            }
        }
        throw new IllegalArgumentException("Not a valid FinanceType : " + type);
    }

    public String getType() {
        return type;
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
}
