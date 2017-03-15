package org.innovateuk.ifs.finance.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.finance.resource.category.ChangedFinanceRowPair;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.util.List;
import java.util.Map;

/**
 * Project finance resource holds the organisation's finance resources for a project during Finance Checks
 */
public class ProjectFinanceResource extends BaseFinanceResource {

    private Map<FinanceRowType, List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>>> costChanges;

    public ProjectFinanceResource(Long id, Long organisation, Long projectId, Long organisationSize) {
        super(id, organisation, projectId, organisationSize);
    }

    public ProjectFinanceResource(BaseFinanceResource originalFinance) {
        super(originalFinance);
    }

    // for mapstruct
    public ProjectFinanceResource() {
    }

    public Long getProject() {
        return super.getTarget();
    }

    public void setProject(Long target) {
        super.setTarget(target);
    }

    public Map<FinanceRowType, List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>>> getCostChanges() {
        return costChanges;
    }

    public void setCostChanges(Map<FinanceRowType, List<ChangedFinanceRowPair<FinanceRowItem, FinanceRowItem>>> costChanges) {
        this.costChanges = costChanges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectFinanceResource that = (ProjectFinanceResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(organisation, that.organisation)
                .append(target, that.target)
                .append(organisationSize, that.organisationSize)
                .append(financeOrganisationDetails, that.financeOrganisationDetails)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(organisation)
                .append(target)
                .append(organisationSize)
                .append(financeOrganisationDetails)
                .toHashCode();
    }
}
