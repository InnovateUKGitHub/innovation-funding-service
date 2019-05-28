package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.GrantOfferLetterTemplateViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GrantOfferLetterTemplatePopulator {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private UserRestService userRestService;

    public GrantOfferLetterTemplateViewModel populate(long projectId) {

        ProjectResource projectResource = projectRestService.getProjectById(projectId).getSuccess();
        String projectName = projectResource.getName();
        long applicationId = projectResource.getApplication();
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(projectResource.getCompetition()).getSuccess();
        String competitionName = competitionResource.getName();
        ProjectUserResource projectUserResource = projectService.getProjectManager(projectId).get(); // do validation here for non existing
        OrganisationResource leadOrg = organisationRestService.getOrganisationById(projectUserResource.getOrganisation()).getSuccess();
        String leadOrgName = leadOrg.getName();
        UserResource user = userRestService.retrieveUserById(projectUserResource.getUser()).getSuccess();
        String projectManagerFirstName = user.getFirstName();
        String projectManagerLastName = user.getLastName();


        return new GrantOfferLetterTemplateViewModel(applicationId,
                                                     projectManagerFirstName,
                                                     projectManagerLastName,
                                                     "",
                                                     competitionName,
                                                     projectName,
                                                     leadOrgName);
    }
}
