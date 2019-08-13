package org.innovateuk.ifs.project.grantofferletter.viewmodel;

import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.OTHER_FUNDING;

/**
 *  Holder of values for the industrial finance table, used on the grant offer letter template page
 */
public class IndustrialFinanceTableModel {

    private final boolean showTotalsColumn;
    private final Map<String, ProjectFinanceResource> finances;
    private final List<String> organisations;
    private final BigDecimal totalEligibleCosts;
    private final BigDecimal totalGrant;

    public IndustrialFinanceTableModel(boolean showTotalsColumn,
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

    public List<String> getOrganisations() {
        return organisations;
    }

    public BigDecimal getTotalForType(FinanceRowType type) {
        return finances
                .values()
                .stream()
                .map(finance -> finance.getFinanceOrganisationDetails(type))
                .map(FinanceRowCostCategory::getTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalEligibleCosts() {
        return totalEligibleCosts;
    }

    public BigDecimal getTotalGrant() {
        return totalGrant;
    }

    public boolean showOtherFundingRow() {
        return getTotalForType(OTHER_FUNDING).compareTo(BigDecimal.ZERO) > 0;
    }
}

