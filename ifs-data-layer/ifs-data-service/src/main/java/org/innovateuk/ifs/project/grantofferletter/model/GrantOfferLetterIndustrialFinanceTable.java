package org.innovateuk.ifs.project.grantofferletter.model;

import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GrantOfferLetterIndustrialFinanceTable {

    private Map<String, BigDecimal> labour;
    private Map<String, BigDecimal> materials;
    private Map<String, BigDecimal> overheads;
    private Map<String, BigDecimal> capitalUsage;
    private Map<String, BigDecimal> subcontract;
    private Map<String, BigDecimal> travel;
    private Map<String, BigDecimal> otherCosts;

    public GrantOfferLetterIndustrialFinanceTable() {

    }

    public void populate(Map<String,List<ProjectFinanceRow>> financials) {
        labour = sumByFinancialType(financials, FinanceRowType.LABOUR);
        materials = sumByFinancialType(financials, FinanceRowType.MATERIALS);
        overheads = sumByFinancialType(financials, FinanceRowType.OVERHEADS);
        capitalUsage = sumByFinancialType(financials, FinanceRowType.CAPITAL_USAGE);
        subcontract = sumByFinancialType(financials, FinanceRowType.SUBCONTRACTING_COSTS);
        travel = sumByFinancialType(financials, FinanceRowType.TRAVEL);
        otherCosts = sumByFinancialType(financials, FinanceRowType.OTHER_COSTS);
    }





    private Map<String, BigDecimal> sumByFinancialType(Map<String, List<ProjectFinanceRow>> financials, FinanceRowType type) {
        Map<String, BigDecimal> financeMap = new HashMap<>();
        financials.forEach( (orgName, finances) -> {
            BigDecimal financeSum = finances
                    .stream()
                    .filter(pfr -> type.getName().equals(pfr.getName()))
                    .map(ProjectFinanceRow::getCost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            financeMap.put(orgName, financeSum);
        });
        return financeMap;
    }


    public Map<String, BigDecimal> getMaterials() {
        return materials;
    }

    public Map<String, BigDecimal> getLabour() {
        return labour;
    }

    public Map<String, BigDecimal> getOverheads() {
        return overheads;
    }

    public Map<String, BigDecimal> getCapitalUsage() {
        return capitalUsage;
    }

    public Map<String, BigDecimal> getSubcontract() {
        return subcontract;
    }

    public Map<String, BigDecimal> getTravel() {
        return travel;
    }

    public Map<String, BigDecimal> getOtherCosts() {
        return otherCosts;
    }



}
