package org.innovateuk.ifs.project.pendingpartner.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
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

    public ProjectTermsModelPopulator(ProjectRestService projectRestService,
                                      CompetitionRestService competitionRestService,
                                      OrganisationRestService organisationRestService,
                                      PendingPartnerProgressRestService pendingPartnerProgressRestService) {
        this.projectRestService = projectRestService;
        this.competitionRestService = competitionRestService;
        this.organisationRestService = organisationRestService;
        this.pendingPartnerProgressRestService = pendingPartnerProgressRestService;
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
                competition.getTermsAndConditions().getTemplate(),
                pendingPartnerProgressResource.isTermsAndConditionsComplete(),
                pendingPartnerProgressResource.getTermsAndConditionsCompletedOn()
        );
    }
}
