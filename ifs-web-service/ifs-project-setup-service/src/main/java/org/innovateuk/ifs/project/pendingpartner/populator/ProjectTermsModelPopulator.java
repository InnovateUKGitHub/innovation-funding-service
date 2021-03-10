package org.innovateuk.ifs.project.pendingpartner.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.pendingpartner.viewmodel.ProjectTermsViewModel;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.stereotype.Component;

@Component
public class ProjectTermsModelPopulator {
    private ProjectRestService projectRestService;
    private CompetitionRestService competitionRestService;
    private OrganisationRestService organisationRestService;
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;
    private ProjectFinanceRestService projectFinanceRestService;

    public ProjectTermsModelPopulator(ProjectRestService projectRestService,
                                      CompetitionRestService competitionRestService,
                                      OrganisationRestService organisationRestService,
                                      PendingPartnerProgressRestService pendingPartnerProgressRestService,
                                      ProjectFinanceRestService projectFinanceRestService) {
        this.projectRestService = projectRestService;
        this.competitionRestService = competitionRestService;
        this.organisationRestService = organisationRestService;
        this.pendingPartnerProgressRestService = pendingPartnerProgressRestService;
        this.projectFinanceRestService = projectFinanceRestService;
    }

    public ProjectTermsViewModel populate(long projectId,
                                          long organisationId) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        PendingPartnerProgressResource pendingPartnerProgressResource = pendingPartnerProgressRestService.getPendingPartnerProgress(projectId, organisationId).getSuccess();

        return new ProjectTermsViewModel(
                projectId,
                organisation.getId(),
                getTermsAndConditionsTemplate(competition, projectId, organisationId),
                pendingPartnerProgressResource.isTermsAndConditionsComplete(),
                pendingPartnerProgressResource.getTermsAndConditionsCompletedOn()
        );
    }

    private String getTermsAndConditionsTemplate(CompetitionResource competition, long projectId, Long organisationId) {
        if (competition.isFinanceType() && organisationId != null) {
            ProjectFinanceResource projectFinanceResource = projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();
            if (projectFinanceResource != null && isNorthernIrelandDeclaration(projectFinanceResource)
                    && competition.getOtherFundingRulesTermsAndConditions() != null) {
                return competition.getOtherFundingRulesTermsAndConditions().getTemplate();
            }
        }
        return competition.getTermsAndConditions().getTemplate();
    }

    private boolean isNorthernIrelandDeclaration(ProjectFinanceResource projectFinanceResource) {
        return projectFinanceResource.getNorthernIrelandDeclaration() != null && projectFinanceResource.getNorthernIrelandDeclaration();
    }
}
