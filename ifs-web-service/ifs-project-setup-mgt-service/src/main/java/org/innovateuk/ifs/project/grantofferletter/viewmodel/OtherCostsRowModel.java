package org.innovateuk.ifs.project.grantofferletter.viewmodel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

/*
 * Holder of values for the other costs rows on GOL finance tables, which are handled differently
 */
public class OtherCostsRowModel {

    private final String otherCostName;
    private Map<String, List<BigDecimal>> otherCostValues;

    public OtherCostsRowModel(String otherCostName,
                              Map<String, List<BigDecimal>> otherCostValues) {
        this.otherCostName = otherCostName;
        this.otherCostValues = otherCostValues;
    }

    public String getOtherCostName() {
        return otherCostName;
    }

    public Map<String, List<BigDecimal>> getOtherCostValues() {
        return otherCostValues;
    }

    public void addToCostValues(String orgName, BigDecimal cost) {
        if(otherCostValues.keySet().contains(orgName)) {
            otherCostValues.get(orgName).add(cost);
        } else {
            otherCostValues.put(orgName, singletonList(cost));
        }
    }
}
