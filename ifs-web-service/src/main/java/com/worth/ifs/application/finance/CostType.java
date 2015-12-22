package com.worth.ifs.application.finance;

import org.apache.commons.logging.LogFactory;

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
    FINANCE("finance"),
    OTHER_FUNDING("other_funding");

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
        LogFactory.getLog(CostType.class).error("Not a valid costype " + type);
        throw new IllegalArgumentException("Not a valid CostType : " + type);
    }

    public String getType() {
        return type;
    }
}
