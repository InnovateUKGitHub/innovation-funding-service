package org.innovateuk.ifs.project.pendingpartner.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.pendingpartner.viewmodel.ProjectYourFundingViewModel;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProjectYourFundingViewModelPopulator {

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Autowired
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    public ProjectYourFundingViewModel populate(long projectId, long organisationId) {
        PendingPartnerProgressResource progress = pendingPartnerProgressRestService.getPendingPartnerProgress(projectId, organisationId).getSuccess();
        ProjectFinanceResource projectFinance = projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        ApplicationResource application = applicationRestService.getApplicationById(project.getApplication()).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        boolean organisationSectionRequired = !competition.applicantShouldUseJesFinances(organisation.getOrganisationTypeEnum());
        boolean locked = organisationSectionRequired && !progress.isYourOrganisationComplete();
        boolean fundingOverridden = grantClaimMaximumRestService.isMaximumFundingLevelOverridden(competition.getId()).getSuccess();
        boolean isLead = application.getLeadOrganisationId() == organisationId;

        return new ProjectYourFundingViewModel(project, organisationId, progress.isYourFundingComplete(),
                organisation.getOrganisationTypeEnum().equals(OrganisationTypeEnum.BUSINESS),
                projectFinance.getMaximumFundingLevel(),
                locked,
                competition.getId(),
                fundingOverridden,
                isLead,
                competition.getFundingType());
    }
}
