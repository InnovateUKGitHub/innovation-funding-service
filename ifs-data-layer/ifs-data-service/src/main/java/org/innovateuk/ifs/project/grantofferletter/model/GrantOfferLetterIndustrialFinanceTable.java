package org.innovateuk.ifs.project.grantofferletter.model;

import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates the grant offer letter industrial finance table, used by the html renderer for the grant offer letter
 */
@Component
public class GrantOfferLetterIndustrialFinanceTable extends GrantOfferLetterFinanceTable {

    private Map<String, BigDecimal> labour;
    private Map<String, BigDecimal> materials;
    private Map<String, BigDecimal> overheads;
    private Map<String, BigDecimal> capitalUsage;
    private Map<String, BigDecimal> subcontract;
    private Map<String, BigDecimal> travel;
    private Map<String, BigDecimal> otherCosts;
    private List<String> organisations;

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
        organisations = new ArrayList<>(financials.keySet());
    }

    private Map<String, BigDecimal> sumByFinancialType(Map<String, List<ProjectFinanceRow>> financials, FinanceRowType type) {
        Map<String, BigDecimal> financeMap = new HashMap<>();
        financials.forEach( (orgName, finances) -> {
            BigDecimal financeSum = finances
                    .stream()
                    .filter(pfr -> type.getType().equals(pfr.getName()))
                    .map(ProjectFinanceRow::getCost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            financeMap.put(orgName, financeSum);
        });
        return financeMap;
    }

    public List<String> getOrganisations() {
        return organisations;
    }

    public BigDecimal getLabour(String organisation) {
        return labour.get(organisation);
    }
    public BigDecimal getMaterials(String organisation) {
        return materials.get(organisation);
    }
    public BigDecimal getOverheads(String organisation) {
        return overheads.get(organisation);
    }
    public BigDecimal getCapitalUsage(String organisation) {
        return capitalUsage.get(organisation);
    }
    public BigDecimal getSubcontract(String organisation) {
        return subcontract.get(organisation);
    }
    public BigDecimal getTravel(String organisation) {
        return travel.get(organisation);
    }
    public BigDecimal getOtherCosts(String organisation) {
        return otherCosts.get(organisation);
    }

    public BigDecimal getLabourTotal() {
        return labour
                .values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }
    public BigDecimal getMaterialsTotal() {
        return materials
                .values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public BigDecimal getOverheadsTotal() {
        return overheads
                .values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public BigDecimal getCapitalUsageTotal() {
        return capitalUsage
                .values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public BigDecimal getSubcontractTotal() {
        return subcontract
                .values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public BigDecimal getTravelTotal() {
        return travel
                .values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public BigDecimal getOtherCostsTotal() {
        return otherCosts
                .values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
