package org.innovateuk.ifs.project.pendingpartner.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.financecheck.FinanceCheckService;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummaryResource;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.yourorganisation.viewmodel.ProjectYourOrganisationViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * A populator to build a YourOrganisationViewModel for the "Your organisation" pages.
 */
@Component
public class YourOrganisationViewModelPopulator {

    @Autowired
    private ProjectYourOrganisationRestService yourOrganisationRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;

    @Autowired
    private FinanceCheckService financeCheckService;

    private UserResource userResource;

    public ProjectYourOrganisationViewModel populate(long projectId, long organisationId) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        Optional<FinanceCheckSummaryResource> financeCheckSummary = financeCheckService.getFinanceCheckSummary(projectId).getOptionalSuccessObject();

        boolean showStateAidAgreement =
                yourOrganisationRestService.isShowStateAidAgreement(projectId, organisationId).getSuccess();

        PendingPartnerProgressResource pendingPartner = pendingPartnerProgressRestService.getPendingPartnerProgress(projectId, organisationId).getSuccess();

        return new ProjectYourOrganisationViewModel(showStateAidAgreement,
                pendingPartner.isYourFundingComplete(),
                competition.isH2020(),
                projectId,
                project.getName(),
                organisationId,
                pendingPartner.isYourOrganisationComplete(),
                true,
                userResource,
                financeCheckSummary.get().isAllEligibilityAndViabilityInReview());
    }
}