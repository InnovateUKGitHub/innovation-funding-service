package org.innovateuk.ifs.project.grantofferletter.viewmodel;


import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *  Holder of values for the academic finance table, used on the grant offer letter template page
 */
public class AcademicFinanceTableModel {

    private final boolean showTotalsColumn;
    private final Map<String, ProjectFinanceResource> finances;
    private final List<String> organisations;
    private final BigDecimal totalEligibleCosts;
    private final BigDecimal totalGrant;

    public AcademicFinanceTableModel(boolean showTotalsColumn,
                                     Map<String, ProjectFinanceResource> finances,
                                     List<String> organisations,
                                     BigDecimal totalEligibleCosts,
                                     BigDecimal totalGrant) {
        this.showTotalsColumn = showTotalsColumn;
        this.finances = finances;
        this.organisations = organisations;
        this.totalEligibleCosts = totalEligibleCosts;
        this.totalGrant = totalGrant;
    }

    public boolean isShowTotalsColumn() {
        return showTotalsColumn;
    }

    public Map<String, ProjectFinanceResource> getFinances() {
        return finances;
    }


    public BigDecimal getByOrgAndName(String org, String name) {
        return getCostsFromProjectFinance(finances.get(org), name);
    }

    public BigDecimal getTotalByName(String costName) {
        return finances.values()
                .stream()
                .map(finance -> getCostsFromProjectFinance(finance, costName))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<String> getOrganisations() {
        return organisations;
    }

    private BigDecimal getCostsFromProjectFinance(ProjectFinanceResource finance, String costName) {
        return finance.getFinanceOrganisationDetails()
                .values()
                .stream()
                .map(FinanceRowCostCategory::getCosts)
                .flatMap(List::stream)
                .filter(cost -> costName.equals(cost.getName()))
                .map(FinanceRowItem::getTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalEligibleCosts() {
        return totalEligibleCosts;
    }

    public BigDecimal getTotalGrant() {
        return totalGrant;
    }
}

