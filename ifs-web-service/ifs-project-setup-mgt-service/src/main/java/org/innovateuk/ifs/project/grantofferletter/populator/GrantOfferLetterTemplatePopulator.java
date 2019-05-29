package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceNotesRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.GrantOfferLetterTemplateViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.threads.resource.NoteResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Autowired
    private ProjectFinanceNotesRestService projectFinanceNotesRestService;

    public GrantOfferLetterTemplateViewModel populate(long projectId) {

        ProjectResource projectResource = projectRestService.getProjectById(projectId).getSuccess();
        String projectName = projectResource.getName();
        long applicationId = projectResource.getApplication();
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(projectResource.getCompetition()).getSuccess();
        String competitionName = competitionResource.getName();
        ProjectUserResource projectUserResource = projectService.getProjectManager(projectId).get();
        OrganisationResource leadOrg = organisationRestService.getOrganisationById(projectUserResource.getOrganisation()).getSuccess();
        String leadOrgName = leadOrg.getName();
        UserResource user = userRestService.retrieveUserById(projectUserResource.getUser()).getSuccess();
        String projectManagerFirstName = user.getFirstName();
        String projectManagerLastName = user.getLastName();
        List<ProjectFinanceResource> allProjectFinances = projectFinanceRestService.getProjectFinances(projectResource.getId()).getSuccess();
        List<NoteResource> allProjectNotes = new ArrayList<>();
        allProjectFinances.forEach(projectFinance ->
                                           projectFinanceNotesRestService.findAll(projectFinance.getId())
                                                   .ifSuccessful(allProjectNotes::addAll)
        );

        return new GrantOfferLetterTemplateViewModel(applicationId,
                                                     projectManagerFirstName,
                                                     projectManagerLastName,
                                                     getAddressLines(projectResource),
                                                     competitionName,
                                                     projectName,
                                                     leadOrgName,
                                                     allProjectNotes,
                                                     competitionResource.getTermsAndConditions().getTemplate());
    }

    private List<String> getAddressLines(ProjectResource project) {
        List<String> addressLines = new ArrayList<>();
        if (project.getAddress() != null) {
            AddressResource address = project.getAddress();
            addressLines.add(address.getAddressLine1() != null ? address.getAddressLine1() : "");
            addressLines.add(address.getAddressLine2() != null ? address.getAddressLine2() : "");
            addressLines.add((address.getAddressLine3() != null ? address.getAddressLine3() : ""));
            addressLines.add(address.getTown() != null ? address.getTown() : "");
            addressLines.add(address.getPostcode() != null ? address.getPostcode() : "");
        }
        return addressLines;
    }

}
