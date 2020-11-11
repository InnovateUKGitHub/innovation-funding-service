package org.innovateuk.ifs.financecheck.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.financecheck.FinanceCheckService;
import org.innovateuk.ifs.financecheck.viewmodel.FinanceCheckSummaryEntryViewModel;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class FinanceCheckSummaryEntryViewModelPopulator {

    @Autowired
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Autowired
    private FinanceCheckService financeCheckService;

    public FinanceCheckSummaryEntryViewModel populate(CompetitionResource competition, ProjectResource project,
                                                                       FinanceCheckEligibilityResource eligibilityOverview, OrganisationResource organisation, boolean isLeadPartnerOrganisation) {
        BigDecimal contributionToProject;
        BigDecimal percentageContributionToProject;

        if (competition.isKtp()) {
            List<PartnerOrganisationResource> partnerOrgs = partnerOrganisationRestService.getProjectPartnerOrganisations(project.getId()).getSuccess();
            PartnerOrganisationResource otherOrg = partnerOrgs.stream().filter(org -> !org.getOrganisation().equals(organisation.getId())).findFirst().get();
            FinanceCheckEligibilityResource otherEligibilityResource = financeCheckService.getFinanceCheckEligibilityDetails(project.getId(), otherOrg.getOrganisation());

            if (isLeadPartnerOrganisation) {
                contributionToProject = new BigDecimal("0.00");
                percentageContributionToProject = new BigDecimal("0.0");
            } else {
                contributionToProject = otherEligibilityResource.getContributionToProject();
                percentageContributionToProject = getPercentageContributionToProject(otherEligibilityResource);
            }

        } else {
            contributionToProject = eligibilityOverview.getContributionToProject();
            percentageContributionToProject = getPercentageContributionToProject(eligibilityOverview);
        }

        return new FinanceCheckSummaryEntryViewModel(
                eligibilityOverview.getProjectId(),
                eligibilityOverview.getOrganisationId(),
                eligibilityOverview.getDurationInMonths(),
                eligibilityOverview.getTotalCost(),
                eligibilityOverview.getPercentageGrant(),
                eligibilityOverview.getFundingSought(),
                eligibilityOverview.getOtherPublicSectorFunding(),
                contributionToProject,
                percentageContributionToProject,
                eligibilityOverview.isHasApplicationFinances());
    }

    private BigDecimal getPercentageContributionToProject(FinanceCheckEligibilityResource resource) {
        if (resource.getTotalCost().signum() == 0 || resource.getContributionToProject().signum() == 0) {
            return BigDecimal.ZERO;
        }
        return resource.getContributionToProject().multiply(new BigDecimal(100)).divide(resource.getTotalCost(), 1, RoundingMode.HALF_UP);
    }
}
