package org.innovateuk.ifs.financecheck.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * View model for the project cost breakdown table.
 */
public class ProjectFinanceCostBreakdownViewModel {
    private List<ProjectFinanceResource> finances;
    private List<PartnerOrganisationResource> organisationResources;
    private BigDecimal total;
    private List<FinanceRowType> financeRowTypes;

    public ProjectFinanceCostBreakdownViewModel() {}

    public ProjectFinanceCostBreakdownViewModel(List<ProjectFinanceResource> finances, List<PartnerOrganisationResource> organisationResources, CompetitionResource competition) {
        this.finances = finances;
        this.organisationResources = organisationResources;
        this.total = finances.stream().map(ProjectFinanceResource::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        this.financeRowTypes = competition.getFinanceRowTypes();
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

    public List<FinanceRowType> getFinanceRowTypes() {
        return financeRowTypes;
    }
}
