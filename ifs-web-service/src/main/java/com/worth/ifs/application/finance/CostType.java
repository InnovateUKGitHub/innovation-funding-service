package com.worth.ifs.application.finance;

/**
 * Cost types are used to identify the different categories that costs can have
 */
public enum CostType {
    LABOUR("labour"),
    OVERHEADS("overheads"),
    MATERIALS("materials"),
    CAPITAL_USAGE("capital_usage"),
    SUBCONTRACTING_COSTS("subcontracting_costs"),
    TRAVEL("travel"),
    OTHER_COSTS("other_costs"),
    YOUR_FINANCE("your_finance"),
    FINANCE("finance");

    private String type;

    CostType(String type) {
        this.type = type;
    }

    public static CostType fromString(String type) {
        if(type!=null) {
            for(CostType costType : CostType.values()) {
                if(type.equalsIgnoreCase(costType.type)) {
                    return costType;
                }
            }
        }
        throw new IllegalArgumentException("Not a valid CostType : " + type);
    }

    public String getType() {
        return type;
    }
}
