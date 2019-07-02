package org.innovateuk.ifs.project.grantofferletter.viewmodel;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

/*
 * Holder of values for the other costs rows on GOL finance tables, which are handled differently
 */
public class OtherCostsRowModel {

    private final String description;
    private Map<String, List<BigDecimal>> otherCostValues;

    public OtherCostsRowModel(String description,
                              Map<String, List<BigDecimal>> otherCostValues) {
        this.description = description;
        this.otherCostValues = otherCostValues;
    }

    public String getDescription() {
        return description;
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

    public Collection<String> getOrganisations() {
        return otherCostValues.keySet();
    }

    public BigDecimal getCostValuesForOrg(String organisation) {
        if(!otherCostValues.containsKey(organisation)) {
            return BigDecimal.ZERO;
        }
        return otherCostValues.get(organisation)
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotal() {
        return otherCostValues == null ?
                BigDecimal.ZERO :
                otherCostValues
                        .values()
                        .stream()
                        .flatMap(List::stream)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
