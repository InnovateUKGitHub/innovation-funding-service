package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceNotesRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.AcademicFinanceTableModel;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.GrantOfferLetterTemplateViewModel;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.IndustrialFinanceTableModel;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.SummaryFinanceTableModel;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GrantOfferLetterTemplatePopulator {

    private final ProjectRestService projectRestService;

    private final CompetitionRestService competitionRestService;

    private final ProjectService projectService;

    private final OrganisationRestService organisationRestService;

    private final UserRestService userRestService;

    private final ProjectFinanceRestService projectFinanceRestService;

    private final ProjectFinanceNotesRestService projectFinanceNotesRestService;

    private final IndustrialFinanceTableModelPopulator industrialFinanceTableModelPopulator;

    private final AcademicFinanceTableModelPopulator academicFinanceTableModelPopulator;

    private final SummaryFinanceTableModelPopulator summaryFinanceTableModelPopulator;

    @Autowired
    public GrantOfferLetterTemplatePopulator(
            ProjectRestService projectRestService,
            CompetitionRestService competitionRestService,
            ProjectService projectService,
            OrganisationRestService organisationRestService,
            UserRestService userRestService,
            ProjectFinanceRestService projectFinanceRestService,
            ProjectFinanceNotesRestService projectFinanceNotesRestService,
            IndustrialFinanceTableModelPopulator industrialFinanceTableModelPopulator,
            AcademicFinanceTableModelPopulator academicFinanceTableModelPopulator,
            SummaryFinanceTableModelPopulator summaryFinanceTableModelPopulator
    ) {
        this.projectRestService = projectRestService;
        this.competitionRestService = competitionRestService;
        this.projectService = projectService;
        this.organisationRestService = organisationRestService;
        this.userRestService = userRestService;
        this.projectFinanceRestService = projectFinanceRestService;
        this.projectFinanceNotesRestService = projectFinanceNotesRestService;
        this.industrialFinanceTableModelPopulator = industrialFinanceTableModelPopulator;
        this.academicFinanceTableModelPopulator = academicFinanceTableModelPopulator;
        this.summaryFinanceTableModelPopulator = summaryFinanceTableModelPopulator;
    }

    public GrantOfferLetterTemplateViewModel populate(long projectId) {

        ProjectResource projectResource = projectRestService.getProjectById(projectId).getSuccess();
        String projectName = projectResource.getName();
        long applicationId = projectResource.getApplication();
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(projectResource.getCompetition()).getSuccess();
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

        Map<OrganisationResource, ProjectFinanceResource> financesForOrgs = getFinancesForOrgs(projectResource, allProjectFinances);
        IndustrialFinanceTableModel industrialFinanceTableModel = industrialFinanceTableModelPopulator.createTable(financesForOrgs, competitionResource);
        AcademicFinanceTableModel academicFinanceTableModel = academicFinanceTableModelPopulator.createTable(financesForOrgs, competitionResource);
        SummaryFinanceTableModel summaryFinanceTableModel = summaryFinanceTableModelPopulator.createTable(financesForOrgs, competitionResource);

        return new GrantOfferLetterTemplateViewModel(applicationId,
                                                     projectManagerFirstName,
                                                     projectManagerLastName,
                                                     getAddressLines(projectResource),
                                                     projectResource.getCompetitionName(),
                                                     projectName,
                                                     leadOrgName,
                                                     allProjectNotes,
                                                     competitionResource.getTermsAndConditions().getTemplate(),
                                                     industrialFinanceTableModel,
                                                     academicFinanceTableModel,
                                                     summaryFinanceTableModel);
    }

    private List<String> getAddressLines(ProjectResource project) {
        List<String> addressLines = new ArrayList<>();
        if (project.getAddress() != null) {
            AddressResource address = project.getAddress();
            addressLines.add(address.getAddressLine1() != null ? address.getAddressLine1() : "");
            addressLines.add(address.getAddressLine2() != null ? address.getAddressLine2() : "");
            addressLines.add((address.getAddressLine3() != null ? address.getAddressLine3() : ""));
            addressLines.add(address.getTown() != null ? address.getTown() : "");
            addressLines.add(address.getCountry() != null ? address.getCountry() : "");
            addressLines.add(address.getPostcode() != null ? address.getPostcode() : "");
        }
        return addressLines;
    }

    private Map<OrganisationResource, ProjectFinanceResource> getFinancesForOrgs(ProjectResource projectResource, List<ProjectFinanceResource> projectFinances) {
        Map<OrganisationResource, ProjectFinanceResource> orgFinances = new HashMap<>();

        List<OrganisationResource> projectOrganisations = projectService.getPartnerOrganisationsForProject(projectResource.getId());
        projectOrganisations.forEach(org -> {
            ProjectFinanceResource financesForOrg =
                    projectFinances
                            .stream()
                            .filter(finances -> finances.getOrganisation().equals(org.getId()))
                            .findAny()
                            .get();

            orgFinances.put(org, financesForOrg);
        });

        return orgFinances;
    }

}
