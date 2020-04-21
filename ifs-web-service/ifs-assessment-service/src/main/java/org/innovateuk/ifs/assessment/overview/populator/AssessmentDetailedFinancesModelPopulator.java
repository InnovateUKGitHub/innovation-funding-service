package org.innovateuk.ifs.assessment.overview.populator;

import org.innovateuk.ifs.application.finance.populator.OrganisationApplicationFinanceOverviewImpl;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.forms.academiccosts.form.AcademicCostForm;
import org.innovateuk.ifs.application.forms.academiccosts.populator.AcademicCostViewModelPopulator;
import org.innovateuk.ifs.application.forms.academiccosts.populator.ApplicationAcademicCostFormPopulator;
import org.innovateuk.ifs.application.forms.academiccosts.viewmodel.AcademicCostViewModel;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.populator.YourProjectCostsViewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.assessment.overview.viewmodel.AssessmentDetailedFinancesViewModel;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionAssessmentConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.competition.resource.AssessorFinanceView.DETAILED;
import static org.innovateuk.ifs.form.resource.SectionType.PROJECT_COST_FINANCES;

@Component
public class AssessmentDetailedFinancesModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;
    @Autowired
    private ApplicationRestService applicationRestService;
    @Autowired
    private AssessmentRestService assessmentRestService;
    @Autowired
    private UserRestService userRestService;
    @Autowired
    private OrganisationRestService organisationRestService;
    @Autowired
    private FileEntryRestService fileEntryRestService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private FinanceService financeService;
    @Autowired
    private YourProjectCostsViewModelPopulator yourProjectCostsViewModelPopulator;
    @Autowired
    private ApplicationYourProjectCostsFormPopulator yourProjectCostsFormPopulator;
    @Autowired
    private AcademicCostViewModelPopulator academicCostViewModelPopulator;
    @Autowired
    private ApplicationAcademicCostFormPopulator applicationAcademicCostFormPopulator;
    @Autowired
    private CompetitionAssessmentConfigRestService competitionAssessmentConfigRestService;

    public AssessmentDetailedFinancesViewModel populateModel(long applicationId, long organisationId, Model model, UserResource user) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        List<AssessmentResource> assessments = assessmentRestService.getByUserAndApplication(user.getId(), applicationId).getSuccess();
        Long assessmentId = assessments.isEmpty() ? null : assessments.get(0).getId();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        CompetitionAssessmentConfigResource competitionAssessmentConfigResource = competitionAssessmentConfigRestService.findOneByCompetitionId(competition.getId()).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        List<ProcessRoleResource> applicationRoles = userRestService.findProcessRole(application.getId()).getSuccess();

        boolean academic = isAcademicFinance(organisation.getOrganisationType(), competition);
        SectionResource costSection = sectionService.getSectionsForCompetitionByType(competition.getId(), PROJECT_COST_FINANCES).get(0);

        if (academic) {
            addAcademicFinance(model, applicationId, costSection.getId(), organisationId);
        } else {
            addIndustrialFinance(model, applicationId, costSection.getId(), organisationId);
        }

        addApplicationAndOrganisationDetails(model, applicationRoles, organisation, competitionAssessmentConfigResource.getAssessorFinanceView());
        addFinanceDetails(model, applicationId);

        return new AssessmentDetailedFinancesViewModel(assessmentId, applicationId, application,
                application.getName(), academic);
    }

    private void addAcademicFinance(Model model, long applicationId, long sectionId, long organisationId) {
        AcademicCostViewModel viewModel = academicCostViewModelPopulator.populate(organisationId, applicationId, sectionId, false);
        AcademicCostForm form = new AcademicCostForm();
        applicationAcademicCostFormPopulator.populate(form, applicationId, organisationId);

        model.addAttribute("costsViewModel", viewModel);
        model.addAttribute("form", form);
    }

    private void addIndustrialFinance(Model model, long applicationId, long sectionId, long organisationId) {
        YourProjectCostsViewModel viewModel = yourProjectCostsViewModelPopulator.populate(applicationId, sectionId, organisationId, true);
        YourProjectCostsForm form = yourProjectCostsFormPopulator.populateForm(applicationId, organisationId);
        model.addAttribute("costsViewModel", viewModel);
        model.addAttribute("form", form);
    }


    private void addApplicationAndOrganisationDetails(Model model, List<ProcessRoleResource> userApplicationRoles, OrganisationResource organisation, AssessorFinanceView financeView) {

        Optional<OrganisationResource> leadOrganisation = getApplicationLeadOrganisation(userApplicationRoles, organisation.getId());
        leadOrganisation.ifPresent(org ->
                model.addAttribute("leadOrganisation", org)
        );

        model.addAttribute("applicationOrganisation", organisation);
        model.addAttribute("showAssessorDetailedFinanceLink", financeView.equals(DETAILED) ? true : false);
    }

    private void addFinanceDetails(Model model, long applicationId) {
        OrganisationApplicationFinanceOverviewImpl organisationFinanceOverview = new OrganisationApplicationFinanceOverviewImpl(financeService, fileEntryRestService, applicationId);
        Map<Long, BaseFinanceResource> organisationFinances = organisationFinanceOverview.getFinancesByOrganisation();
        model.addAttribute("organisationFinances", organisationFinances);
    }

    private Optional<ProcessRoleResource> getApplicantProcessRole(List<ProcessRoleResource> userApplicationRoles, long organisationId) {
        return userApplicationRoles
                .stream()
                .filter(processRole -> processRole.getOrganisationId().equals(organisationId))
                .findFirst();
    }

    private Optional<OrganisationResource> getApplicationLeadOrganisation(List<ProcessRoleResource> userApplicationRoles, long organisationId) {

        return userApplicationRoles.stream()
                .filter(role -> role.getOrganisationId() != null && role.getOrganisationId().equals(organisationId))
                .filter(uar -> uar.getRoleName().equals(Role.LEADAPPLICANT.getName()))
                .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisationId()).getSuccess())
                .findFirst();
    }

    private boolean isAcademicFinance(Long organisationType, CompetitionResource competition) {
        return competition.applicantShouldUseJesFinances(OrganisationTypeEnum.getFromId(organisationType));
    }
}

