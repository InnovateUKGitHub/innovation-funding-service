package org.innovateuk.ifs.project.financecheck.viewmodel;

import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * View model for the project cost breakdown table.
 */
public class ProjectFinanceCostBreakdownViewModel {
    private List<ProjectFinanceResource> finances;
    private List<PartnerOrganisationResource> organisationResources;
    private BigDecimal total;

    public ProjectFinanceCostBreakdownViewModel() {}

    public ProjectFinanceCostBreakdownViewModel(List<ProjectFinanceResource> finances, List<PartnerOrganisationResource> organisationResources) {
        this.finances = finances;
        this.organisationResources = organisationResources;
        this.total = finances.stream().map(ProjectFinanceResource::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<ProjectFinanceResource> getFinances() {
        return finances;
    }

    public ProjectFinanceResource getPartnerFinances(Long partnerId) {
        return simpleFindFirst(finances, f -> f.getOrganisation().equals(partnerId)).orElse(null);
    }

    public List<PartnerOrganisationResource> getOrganisationResources() {
        return organisationResources;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public PartnerOrganisationResource getPartnerFromBreakdown(Long organisationId) {
        return getOrganisationResources().stream().filter(org -> organisationId.equals(org.getOrganisation())).findFirst()
                .orElse(new PartnerOrganisationResource());
    }
}
