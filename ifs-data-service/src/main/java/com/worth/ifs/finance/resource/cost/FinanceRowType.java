package com.worth.ifs.finance.resource.cost;

/**
 * FinanceRow types are used to identify the different categories that costs can have
 */
public enum FinanceRowType {
    LABOUR("labour"),
    OVERHEADS("overheads"),
    MATERIALS("materials"),
    CAPITAL_USAGE("capital_usage"),
    SUBCONTRACTING_COSTS("subcontracting"),
    TRAVEL("travel"),
    OTHER_COSTS("other_costs"),
    YOUR_FINANCE("your_finance"),
    FINANCE("finance"),
    OTHER_FUNDING("other_funding"),
    ACADEMIC("academic");

    private String type;

    FinanceRowType(String type) {
        this.type = type;
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
}
