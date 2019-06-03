package org.innovateuk.ifs.project.grantofferletter.viewmodel;

import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/*
* Holder of values for the industrial finance table, used on the grant offer letter template page
*/

public class IndustrialFinanceTableModel extends BaseFinanceTableModel {

    private boolean showTotalsColumn;
    private Map<String, ProjectFinanceResource> finances;
    private List<String> organisations;

    public IndustrialFinanceTableModel(boolean showTotalsColumn,
                                       Map<String, ProjectFinanceResource> finances,
                                       List<String> organisations) {
        this.showTotalsColumn = showTotalsColumn;
        this.finances = finances;
        this.organisations = organisations;
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
}
