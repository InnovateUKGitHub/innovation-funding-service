package org.innovateuk.ifs.assessment.overview.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.OrganisationApplicationFinanceOverviewImpl;
import org.innovateuk.ifs.application.populator.section.YourProjectCostsSectionPopulator;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.section.AbstractSectionViewModel;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.overview.viewmodel.AssessmentDetailedFinancesViewModel;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleService;
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
    private AssessmentService assessmentService;

    @Autowired
    private ProcessRoleService processRoleService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private FileEntryRestService fileEntryRestService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private ApplicantRestService applicantRestService;

    @Autowired
    private YourProjectCostsSectionPopulator projectCostsSectionPopulator;

    public AssessmentDetailedFinancesViewModel populateModel(long assessmentId, long organisationId, Model model) {
        AssessmentResource assessment = assessmentService.getById(assessmentId);
        CompetitionResource competition = competitionRestService.getCompetitionById(assessment.getCompetition()).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        List<ProcessRoleResource> applicationRoles = processRoleService.getByApplicationId(assessment.getApplication());

        addApplicationAndOrganisationDetails(model, applicationRoles, organisation, competition.getAssessorFinanceView());
        addFinanceDetails(model, assessment.getApplication());
        addDetailedFinances(model, applicationRoles, competition.getId(), assessment.getApplication(), organisationId);

        return new AssessmentDetailedFinancesViewModel(assessmentId, assessment.getApplication(),
                assessment.getApplicationName(), getFinanceView(organisation.getOrganisationType()));
    }

    private void addDetailedFinances(Model model, List<ProcessRoleResource> applicationRoles, long competitionId, long applicationId, long organisationId) {
        SectionResource costSection = sectionService.getSectionsForCompetitionByType(competitionId, PROJECT_COST_FINANCES).get(0);
        ProcessRoleResource applicantProcessRole = getApplicantProcessRole(applicationRoles, organisationId).get();
        ApplicantSectionResource applicantSection = applicantRestService.getSection(applicantProcessRole.getUser(), applicationId, costSection.getId());
        ApplicationForm form = new ApplicationForm();

        AbstractSectionViewModel sectionViewModel = projectCostsSectionPopulator.populate(
                applicantSection, form, model, null, true, Optional.of(organisationId), true);
        model.addAttribute("detailedCostings", sectionViewModel);
        model.addAttribute("form", form);
        model.addAttribute("readonly", true);
    }

    private void addApplicationAndOrganisationDetails(Model model, List<ProcessRoleResource> userApplicationRoles, OrganisationResource organisation, AssessorFinanceView financeView) {

        Optional<OrganisationResource> leadOrganisation = getApplicationLeadOrganisation(userApplicationRoles, organisation.getId());
        leadOrganisation.ifPresent(org ->
                model.addAttribute("leadOrganisation", org)
        );

        model.addAttribute("applicationOrganisation", organisation);
        model.addAttribute("showAssessorDetailedFinanceLink", financeView.equals(DETAILED) ? true : false);
    }

    public void addFinanceDetails(Model model, long applicationId) {
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

    private String getFinanceView(Long organisationType) {
        return organisationType.equals(OrganisationTypeEnum.BUSINESS.getId()) ? "finance" :"academic-finance";
    }
}

