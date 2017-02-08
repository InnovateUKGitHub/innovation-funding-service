package org.innovateuk.ifs.project.financechecks.controller;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.populator.OpenProjectFinanceSectionModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.BaseSectionViewModel;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.innovateuk.ifs.project.finance.resource.Eligibility;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.EligibilityResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.financecheck.FinanceCheckService;
import org.innovateuk.ifs.project.financecheck.eligibility.form.FinanceChecksEligibilityForm;
import org.innovateuk.ifs.project.financecheck.eligibility.viewmodel.FinanceChecksEligibilityViewModel;
import org.innovateuk.ifs.project.financechecks.viewmodel.ProjectFinanceChecksViewModel;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.resource.SectionType.PROJECT_COST_FINANCES;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller will handle requests related to finance checks
 */
@Controller
@RequestMapping("/" + ProjectFinanceChecksController.BASE_DIR + "/{projectId}/partner-organisation/{organisationId}/finance-checks")
public class ProjectFinanceChecksController {

    private static final String FORM_ATTR_NAME = "form";

    public static final String BASE_DIR = "project";

    @Autowired
    ProjectService projectService;

    @Autowired
    ApplicationService applicationService;

    @Autowired
    OrganisationService organisationService;

    @Autowired
    UserAuthenticationService userAuthenticationService;

    @Autowired
    SectionService sectionService;

    @Autowired
    CompetitionService competitionService;

    @Autowired
    ProjectFinanceService financeService;

    @Autowired
    FinanceCheckService financeCheckService;

    @Autowired
    private ApplicationModelPopulator applicationModelPopulator;

    @Autowired
    private OpenProjectFinanceSectionModelPopulator openFinanceSectionModel;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @RequestMapping(method = GET)
    public String viewFinanceChecks(Model model,
                                               @PathVariable("projectId") final Long projectId,
                                               @PathVariable("organisationId") final Long organisationId) {

        model.addAttribute("model", buildFinanceChecksLandingPage(projectId, organisationId));

        return "project/finance-checks";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @GetMapping("/eligibility")
    public String viewExternalEligibilityPage(@PathVariable("projectId") final Long projectId, @PathVariable("organisationId") final Long organisationId, @ModelAttribute(FORM_ATTR_NAME) ApplicationForm form, BindingResult bindingResult, Model model, HttpServletRequest request){
        ProjectResource project = projectService.getById(projectId);
        ApplicationResource application = applicationService.getById(project.getApplication());
        OrganisationResource organisation = organisationService.getOrganisationById(organisationId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        boolean isLeadPartnerOrganisation = leadOrganisation.getId().equals(organisation.getId());
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(application.getCompetition());
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        return doViewEligibility(competition, application, project, allSections, user, isLeadPartnerOrganisation, organisation, model, null, form, bindingResult);
    }

    private String doViewEligibility(CompetitionResource competition, ApplicationResource application, ProjectResource project, List<SectionResource> allSections, UserResource user, boolean isLeadPartnerOrganisation, OrganisationResource organisation, Model model, FinanceChecksEligibilityForm eligibilityForm, ApplicationForm form, BindingResult bindingResult) {

        poulateProjectFinanceDetails(competition, application, project, organisation.getId(), allSections, user, form, bindingResult, model);

        EligibilityResource eligibility = financeService.getEligibility(project.getId(), organisation.getId());

        if (eligibilityForm == null) {
            eligibilityForm = getEligibilityForm(eligibility);
        }

        FinanceCheckEligibilityResource eligibilityOverview = financeCheckService.getFinanceCheckEligibilityDetails(project.getId(), organisation.getId());

        boolean eligibilityApproved = eligibility.getEligibility() == Eligibility.APPROVED;

        model.addAttribute("summaryModel", new FinanceChecksEligibilityViewModel(eligibilityOverview, organisation.getName(), project.getName(),
                application.getFormattedId(), isLeadPartnerOrganisation, project.getId(), organisation.getId(),
                eligibilityApproved, eligibility.getEligibilityRagStatus(), eligibility.getEligibilityApprovalUserFirstName(),
                eligibility.getEligibilityApprovalUserLastName(), eligibility.getEligibilityApprovalDate()));

        model.addAttribute("eligibilityForm", eligibilityForm);
        model.addAttribute("form", form);

        return "project/financecheck/eligibility";
    }

    private ProjectFinanceChecksViewModel buildFinanceChecksLandingPage(final Long projectId, final Long organisationId) {
        ProjectResource projectResource = projectService.getById(projectId);
        OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);
        boolean approved = isApproved(projectId, organisationId);

        return new ProjectFinanceChecksViewModel(projectResource, organisationResource, approved);
    }

    private boolean isApproved(final Long projectId, final Long organisationId) {
        Optional<ProjectPartnerStatusResource> organisationStatus = projectService.getProjectTeamStatus(projectId, Optional.empty()).getPartnerStatusForOrganisation(organisationId);
        return COMPLETE.equals(organisationStatus.get().getFinanceChecksStatus());
    }

    private void poulateProjectFinanceDetails(CompetitionResource competition, ApplicationResource application, ProjectResource project, Long organisationId, List<SectionResource> allSections, UserResource user, ApplicationForm form, BindingResult bindingResult, Model model){

        SectionResource section = simpleFilter(allSections, s -> s.getType().equals(PROJECT_COST_FINANCES)).get(0);

        addApplicationAndSectionsInternalWithOrgDetails(application, competition, user.getId(), Optional.ofNullable(section), Optional.empty(), model, form);

        BaseSectionViewModel openFinanceSectionViewModel = openFinanceSectionModel.populateModel(form, model, application, section, user, bindingResult, allSections, organisationId);

        model.addAttribute("model", openFinanceSectionViewModel);

        model.addAttribute("project", project);
    }

    private void addApplicationAndSectionsInternalWithOrgDetails(final ApplicationResource application, final CompetitionResource competition, final Long userId, Optional<SectionResource> section, Optional<Long> currentQuestionId, final Model model, final ApplicationForm form) {
        applicationModelPopulator.addApplicationAndSections(application, competition, userId, section, currentQuestionId, model, form);
    }

    private FinanceChecksEligibilityForm getEligibilityForm(EligibilityResource eligibility) {

        boolean confirmEligibilityChecked = eligibility.getEligibilityRagStatus() != EligibilityRagStatus.UNSET;

        return new FinanceChecksEligibilityForm(eligibility.getEligibilityRagStatus(), confirmEligibilityChecked);
    }
}
