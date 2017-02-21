package org.innovateuk.ifs.finance.resource;

import org.innovateuk.ifs.finance.resource.category.ChangedFinanceRowPair;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.user.resource.OrganisationSize;

import java.util.List;
import java.util.Map;

/**
 * Project finance resource holds the organisation's finance resources for a project during Finance Checks
 */
public class ProjectFinanceResource extends BaseFinanceResource {

    private Map<FinanceRowType, List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>>> costChanges;

    public Long getProject() {
        return super.getTarget();
    }

    public void setProject(Long target) {
        super.setTarget(target);
    }

    public ProjectFinanceResource(Long id, Long organisation, Long projectId, OrganisationSize organisationSize) {
        super(id, organisation, projectId, organisationSize);
    }

    public ProjectFinanceResource(BaseFinanceResource originalFinance) {
        super(originalFinance);
    }

    // for mapstruct
    public ProjectFinanceResource() {
    }

    public Map<FinanceRowType, List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>>> getCostChanges() {
        return costChanges;
    }

    public void setCostChanges(Map<FinanceRowType, List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>>> costChanges) {
        this.costChanges = costChanges;
    }
}
