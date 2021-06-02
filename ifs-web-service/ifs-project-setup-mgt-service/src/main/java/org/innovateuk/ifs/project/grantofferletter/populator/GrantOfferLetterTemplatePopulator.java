package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.FundingRules;
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

    @Autowired
    private IndustrialFinanceTableModelPopulator industrialFinanceTableModelPopulator;

    @Autowired
    private AcademicFinanceTableModelPopulator academicFinanceTableModelPopulator;

    @Autowired
    private SummaryFinanceTableModelPopulator summaryFinanceTableModelPopulator;

    public GrantOfferLetterTemplateViewModel populate(ProjectResource project, CompetitionResource competition) {
        String projectName = project.getName();
        long applicationId = project.getApplication();
        ProjectUserResource projectUserResource = projectService.getProjectManager(project.getId()).get();
        OrganisationResource leadOrg = organisationRestService.getOrganisationById(projectUserResource.getOrganisation()).getSuccess();
        String leadOrgName = leadOrg.getName();
        UserResource user = userRestService.retrieveUserById(projectUserResource.getUser()).getSuccess();
        String projectManagerFirstName = user.getFirstName();
        String projectManagerLastName = user.getLastName();
        List<ProjectFinanceResource> allProjectFinances = projectFinanceRestService.getProjectFinances(project.getId()).getSuccess();
        List<NoteResource> allProjectNotes = new ArrayList<>();
        allProjectFinances.forEach(projectFinance ->
                                           projectFinanceNotesRestService.findAll(projectFinance.getId())
                                                   .ifSuccessful(allProjectNotes::addAll)
        );

        Map<OrganisationResource, ProjectFinanceResource> financesForOrgs = getFinancesForOrgs(project, allProjectFinances);
        IndustrialFinanceTableModel industrialFinanceTableModel = industrialFinanceTableModelPopulator.createTable(financesForOrgs, competition);
        AcademicFinanceTableModel academicFinanceTableModel = academicFinanceTableModelPopulator.createTable(financesForOrgs, competition);
        SummaryFinanceTableModel summaryFinanceTableModel = summaryFinanceTableModelPopulator.createTable(financesForOrgs, competition);

        Map<String, String> termsAndConditions = termsAndConditions(competition, allProjectFinances);

        return new GrantOfferLetterTemplateViewModel(applicationId,
                                                     projectManagerFirstName,
                                                     projectManagerLastName,
                                                     getAddressLines(project),
                                                     project.getCompetitionName(),
                                                     projectName,
                                                     leadOrgName,
                                                     allProjectNotes,
                                                     termsAndConditions,
                                                     industrialFinanceTableModel,
                                                     academicFinanceTableModel,
                                                     summaryFinanceTableModel,
                                                     competition.isProcurement());
    }

    private Map<String, String> termsAndConditions(CompetitionResource competition, List<ProjectFinanceResource> projectFinances) {
        boolean subsidyControlCompetition = FundingRules.SUBSIDY_CONTROL == competition.getFundingRules();

        String mainType = subsidyControlCompetition ? "subsidy control" : "state aid";
        String otherType = subsidyControlCompetition ? "state aid" : "subsidy control";

        Map<String, String> termsMap = new HashMap<>();
        if (shouldPopulateRegularTerms(projectFinances)) {
            termsMap.put(mainType, competition.getTermsAndConditions().getTemplate());
        }
        if (shouldPopulateAdditionalTerms(projectFinances)) {
            termsMap.put(otherType, competition.getOtherFundingRulesTermsAndConditions().getTemplate());
        }

        return termsMap;
    }

    private boolean shouldPopulateRegularTerms(List<ProjectFinanceResource> projectFinances) {
        return projectFinances.stream()
                .anyMatch(projectFinance -> !Boolean.TRUE.equals(projectFinance.getNorthernIrelandDeclaration()));
    }

    private boolean shouldPopulateAdditionalTerms(List<ProjectFinanceResource> projectFinances) {
        return projectFinances.stream()
                        .anyMatch(projectFinance -> Boolean.TRUE.equals(projectFinance.getNorthernIrelandDeclaration()));
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

    private Map<OrganisationResource, ProjectFinanceResource> getFinancesForOrgs(ProjectResource project, List<ProjectFinanceResource> projectFinances) {
        Map<OrganisationResource, ProjectFinanceResource> orgFinances = new HashMap<>();

        List<OrganisationResource> projectOrganisations = projectService.getPartnerOrganisationsForProject(project.getId());
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
