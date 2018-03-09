package org.innovateuk.ifs.assessment.overview.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.UserApplicationRole;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.OrganisationApplicationFinanceOverviewImpl;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.section.YourProjectCostsSectionPopulator;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.section.AbstractSectionViewModel;
import org.innovateuk.ifs.assessment.common.service.AssessmentService;
import org.innovateuk.ifs.assessment.overview.viewmodel.AssessmentDetailedFinancesViewModel;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.competition.resource.AssessorFinanceView;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.file.service.FileEntryRestService;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.application.resource.SectionType.PROJECT_COST_FINANCES;
import static org.innovateuk.ifs.competition.resource.AssessorFinanceView.DETAILED;

@Component
public class AssessmentDetailedFinancesModelPopulator {

    @Autowired
    private CompetitionService competitionService;

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
    YourProjectCostsSectionPopulator projectCostsSectionPopulator;

    public AssessmentDetailedFinancesViewModel populateModel(long assessmentId, long organisationId, Model model) {
        AssessmentResource assessment = assessmentService.getById(assessmentId);
        CompetitionResource competition = competitionService.getById(assessment.getCompetition());
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();

        addApplicationAndOrganisationDetails(model, assessment.getApplication(), organisation, competition.getAssessorFinanceView());
        addFinanceDetails(model, competition.getId(), assessment.getApplication());
        addDetailedFinances(model, competition.getId(), assessment.getApplication(), organisationId);

        return new AssessmentDetailedFinancesViewModel(assessmentId, assessment.getApplication(),
                assessment.getApplicationName(), getFinanceView(organisation.getOrganisationType()));
    }

    private String getFinanceView(Long organisationType) {
        return organisationType.equals(OrganisationTypeEnum.BUSINESS.getId()) ? "finance" :"academic-finance";
    }

    private void addDetailedFinances(Model model, long competitionId, long applicationId, long organisationId) {
        SectionResource costSection = sectionService.getSectionsForCompetitionByType(competitionId, PROJECT_COST_FINANCES).get(0);
        ProcessRoleResource applicantProcessRole = getApplicantProcessRole(applicationId, organisationId).get();
        ApplicantSectionResource applicantSection = applicantRestService.getSection(applicantProcessRole.getUser(), applicationId, costSection.getId());
        ApplicationForm form = new ApplicationForm();

        AbstractSectionViewModel sectionViewModel = projectCostsSectionPopulator.populate(
                applicantSection, form, model, null, true, Optional.of(organisationId), true);
        model.addAttribute("detailedCostings", sectionViewModel);
        model.addAttribute("form", form);
        model.addAttribute("readonly", true);
    }

    private Optional<ProcessRoleResource> getApplicantProcessRole(long applicationId, long organisationId) {
        return processRoleService.getByApplicationId(applicationId)
                .stream()
                .filter(processRole -> processRole.getOrganisationId().equals(organisationId))
                .findFirst();
    }

    private void addApplicationAndOrganisationDetails(Model model, long applicationId, OrganisationResource organisation, AssessorFinanceView financeVew) {
        List<ProcessRoleResource> userApplicationRoles = processRoleService
                .findProcessRolesByApplicationId(applicationId)
                .stream()
                .filter(role -> role.getOrganisationId() != null && role.getOrganisationId().equals(organisation.getId()))
                .collect(toList());
        addOrganisationDetails(model, userApplicationRoles, organisation, financeVew);
    }

    private void addOrganisationDetails(Model model, List<ProcessRoleResource> userApplicationRoles, OrganisationResource organisation, AssessorFinanceView financeView) {
        model.addAttribute("applicationOrganisation", organisation);

        Optional<OrganisationResource> leadOrganisation = getApplicationLeadOrganisation(userApplicationRoles);
        leadOrganisation.ifPresent(org ->
                model.addAttribute("leadOrganisation", org)
        );

        model.addAttribute("showAssessorDetailedFinanceLink", financeView.equals(DETAILED) ? true : false);
    }

    private Optional<OrganisationResource> getApplicationLeadOrganisation(List<ProcessRoleResource> userApplicationRoles) {

        return userApplicationRoles.stream()
                .filter(uar -> uar.getRoleName().equals(UserApplicationRole.LEAD_APPLICANT.getRoleName()))
                .map(uar -> organisationRestService.getOrganisationById(uar.getOrganisationId()).getSuccess())
                .findFirst();
    }

    public void addFinanceDetails(Model model, Long competitionId, Long applicationId) {
        OrganisationApplicationFinanceOverviewImpl organisationFinanceOverview = new OrganisationApplicationFinanceOverviewImpl(financeService, fileEntryRestService, applicationId);
        Map<Long, BaseFinanceResource> organisationFinances = organisationFinanceOverview.getFinancesByOrganisation();
        model.addAttribute("organisationFinances", organisationFinances);
    }
}

