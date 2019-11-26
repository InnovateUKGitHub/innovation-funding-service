package org.innovateuk.ifs.project.pendingpartner.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.pendingpartner.viewmodel.PendingPartnerProgressLandingPageViewModel;
import org.innovateuk.ifs.project.projectteam.PendingPartnerProgressRestService;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PendingPartnerProgressLandingPageViewModelPopulator {

    @Autowired
    private PendingPartnerProgressRestService pendingPartnerProgressRestService;

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    public PendingPartnerProgressLandingPageViewModel populate(long projectId, long organisationId) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();
        PendingPartnerProgressResource progress = pendingPartnerProgressRestService.getPendingPartnerProgress(projectId, organisationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();

        return new PendingPartnerProgressLandingPageViewModel(
                project,
                organisationId,
                progress,
                !competition.applicantShouldUseJesFinances(organisation.getOrganisationTypeEnum())
        );
    }
}
