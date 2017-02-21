package org.innovateuk.ifs.application.finance.viewmodel;

import org.innovateuk.ifs.finance.resource.category.ChangedFinanceRowPair;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Base viewmodel for academic finances
 */
public class ProjectFinanceViewModel extends FinanceViewModel {
    private Map<FinanceRowType, BigDecimal> sectionDifferences;

    private Map<FinanceRowType, List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>>> changes;

    public Map<FinanceRowType, BigDecimal> getSectionDifferences() {
        return sectionDifferences;
    }

    public void setSectionDifferences(Map<FinanceRowType, BigDecimal> sectionDifferences) {
        this.sectionDifferences = sectionDifferences;
    }

    public Map<FinanceRowType, List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>>> getChanges() {
        return changes;
    }

    public void setChanges(Map<FinanceRowType, List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>>> changes) {
        this.changes = changes;
    }
}
