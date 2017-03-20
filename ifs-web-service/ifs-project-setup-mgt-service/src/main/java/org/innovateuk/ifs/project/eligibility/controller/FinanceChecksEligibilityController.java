package org.innovateuk.ifs.project.eligibility.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.application.finance.view.DefaultProjectFinanceModelManager;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceChangesViewModel;
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
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.innovateuk.ifs.project.finance.resource.Eligibility;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.EligibilityResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRowService;
import org.innovateuk.ifs.project.financecheck.FinanceCheckService;
import org.innovateuk.ifs.project.financecheck.eligibility.form.FinanceChecksEligibilityForm;
import org.innovateuk.ifs.project.financecheck.eligibility.viewmodel.FinanceChecksEligibilityViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.util.FinanceUtil;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.application.resource.SectionType.PROJECT_COST_FINANCES;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

/**
 * This controller serves the Eligibility page where internal users can confirm the eligibility of a partner organisation's
 * financial position on a Project
 */
@Controller
@RequestMapping("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility")
public class FinanceChecksEligibilityController {

    private static final String FORM_ATTR_NAME = "form";
    private static final String ADD_COST = "add_cost";
    private static final String QUESTION_ID = "questionId";

    @Autowired
    private FinanceCheckService financeCheckService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private OpenProjectFinanceSectionModelPopulator openFinanceSectionModel;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationModelPopulator applicationModelPopulator;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @Autowired
    private FinanceHandler financeHandler;

    @Autowired
    private ProjectFinanceRowService financeRowService;

    @Autowired
    private FinanceUtil financeUtil;

    @Autowired
    private FinanceService financeService;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION')")
    @GetMapping
    public String viewEligibility(@PathVariable("projectId") Long projectId,
                                  @PathVariable("organisationId") Long organisationId,
                                  @ModelAttribute(FORM_ATTR_NAME) ApplicationForm form,
                                  BindingResult bindingResult,
                                  Model model,
                                  HttpServletRequest request) {
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

        EligibilityResource eligibility = projectFinanceService.getEligibility(project.getId(), organisation.getId());

        if (eligibilityForm == null) {
            eligibilityForm = getEligibilityForm(eligibility);
        }

        FinanceCheckEligibilityResource eligibilityOverview = financeCheckService.getFinanceCheckEligibilityDetails(project.getId(), organisation.getId());

        boolean eligibilityApproved = eligibility.getEligibility() == Eligibility.APPROVED;

        OrganisationResource organisationResource = organisationService.getOrganisationById(organisation.getId());

        FileDetailsViewModel jesFileDetailsViewModel = null;
        boolean isUsingJesFinances = financeUtil.isUsingJesFinances(organisationResource.getOrganisationType());
        if (!isUsingJesFinances) {
            populateProjectFinanceDetails(competition, application, project, organisation.getId(), allSections, user, form, bindingResult, model);
        } else {
            ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceByApplicationIdAndOrganisationId(application.getId(), organisation.getId());
            if (applicationFinanceResource.getFinanceFileEntry() != null) {
                FileEntryResource jesFileEntryResource = financeService.getFinanceEntry(applicationFinanceResource.getFinanceFileEntry()).getSuccessObject();
                jesFileDetailsViewModel = new FileDetailsViewModel(jesFileEntryResource);
            }
        }
        model.addAttribute("summaryModel", new FinanceChecksEligibilityViewModel(eligibilityOverview, organisation.getName(), project.getName(),
                application.getId(), isLeadPartnerOrganisation, project.getId(), organisation.getId(),
                eligibilityApproved, eligibility.getEligibilityRagStatus(), eligibility.getEligibilityApprovalUserFirstName(),
                eligibility.getEligibilityApprovalUserLastName(), eligibility.getEligibilityApprovalDate(), false, isUsingJesFinances, jesFileDetailsViewModel));

        model.addAttribute("eligibilityForm", eligibilityForm);
        model.addAttribute("form", form);

        return "project/financecheck/eligibility";
    }

    private FinanceChecksEligibilityForm getEligibilityForm(EligibilityResource eligibility) {

        boolean confirmEligibilityChecked = eligibility.getEligibilityRagStatus() != EligibilityRagStatus.UNSET;

        return new FinanceChecksEligibilityForm(eligibility.getEligibilityRagStatus(), confirmEligibilityChecked);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION')")
    @RequestMapping(value = "/" + ADD_COST + "/{"+QUESTION_ID+"}")  // Note: request type not explicit as it is used by existing ajax calls which do GET
    public String addCostRow(@ModelAttribute(FORM_ATTR_NAME) ApplicationForm form,
                             BindingResult bindingResult,
                             Model model,
                             @PathVariable("projectId") Long projectId,
                             @PathVariable("organisationId") Long organisationId,
                             @PathVariable(QUESTION_ID) final Long questionId,
                             HttpServletRequest request) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        Long organisationType = organisationService.getOrganisationById(organisationId).getOrganisationType();

        FinanceRowItem costItem = addCost(organisationType, organisationId, projectId, questionId);
        FinanceRowType costType = costItem.getCostType();
        financeHandler.getProjectFinanceModelManager(organisationType).addCost(model, costItem, projectId, organisationId, user.getId(), questionId, costType);

        form.setBindingResult(bindingResult);
        return String.format("project/financecheck/fragments/finance:: %s_row", costType.getType());
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION')")
    @RequestMapping(value = "/remove_cost/{costId}") // Note: request type not explicit as it is used by existing ajax calls which do GET
    public @ResponseBody
    String removeCostRow(@PathVariable("costId") final Long costId) throws JsonProcessingException {
        financeRowService.delete(costId);
        AjaxResult ajaxResult = new AjaxResult(HttpStatus.OK, "true");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ajaxResult);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION')")
    @PostMapping(params = "save-eligibility")
    public String projectFinanceFormSubmit(@PathVariable("projectId") final Long projectId,
                                           @PathVariable("organisationId") Long organisationId,
                                           @Valid @ModelAttribute(FORM_ATTR_NAME) ApplicationForm form,
                                           BindingResult bindingResult,
                                           ValidationHandler validationHandler,
                                           Model model,
                                           HttpServletRequest request) {
        ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(projectResource.getApplication());
        OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);
        Long organisationType = organisationResource.getOrganisationType();

        ValidationMessages saveApplicationErrors = saveProjectFinanceSection(applicationResource.getCompetition(), projectId, organisationType, organisationResource.getId(), request);

        if(saveApplicationErrors.hasErrors()){
            OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
            boolean isLeadPartnerOrganisation = leadOrganisation.getId().equals(organisationResource.getId());
            UserResource user = userAuthenticationService.getAuthenticatedUser(request);
            List<SectionResource> allSections = sectionService.getAllByCompetitionId(applicationResource.getCompetition());
            CompetitionResource competition = competitionService.getById(applicationResource.getCompetition());
            validationHandler.addAnyErrors(saveApplicationErrors);
            return doViewEligibility(competition, applicationResource, projectResource, allSections, user, isLeadPartnerOrganisation, organisationResource, model, null, form, bindingResult);
        } else {
            return getRedirectUrlToEligibility(projectId, organisationId);
        }
    }

    private ValidationMessages saveProjectFinanceSection(Long competitionId,
                                                         Long projectId,
                                                         Long organisationType,
                                                         Long organisationId,
                                                         HttpServletRequest request) {
        ValidationMessages errors = new ValidationMessages();

        ValidationMessages saveErrors = financeHandler.getProjectFinanceFormHandler(organisationType).update(request, organisationId, projectId, competitionId);

        removeCapitalUsageExistingErrors(saveErrors);

        errors.addAll(saveErrors);

        return errors;
    }

    /**
     * INFUND-2921, INFUND-4834 - This can be removed once capital usage existing or new field is handled correctly.
     */
    private void removeCapitalUsageExistingErrors(ValidationMessages errors){
        if(errors != null && errors.hasErrors()) {
            List<Error> filtered = errors.getErrors().stream().filter(e -> !e.getFieldName().contains("-existing")).collect(toList());
            errors.setErrors(filtered);
        }
    }

    private String getRedirectUrlToEligibility(Long projectId, Long organisationId){
        return "redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/eligibility";
    }

    private FinanceRowItem addCost(Long orgType, Long organisationId, Long projectId, Long questionId) {
        return financeHandler.getProjectFinanceFormHandler(orgType).addCostWithoutPersisting(projectId, organisationId, questionId);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION')")
    @PostMapping(params = "confirm-eligibility")
    public String confirmEligibility(@PathVariable("projectId") Long projectId,
                                     @PathVariable("organisationId") Long organisationId,
                                     @ModelAttribute(FORM_ATTR_NAME) ApplicationForm form,
                                     @SuppressWarnings("unused") BindingResult bindingResult,
                                     @ModelAttribute("eligibilityForm") FinanceChecksEligibilityForm eligibilityForm,
                                     ValidationHandler validationHandler,
                                     Model model,
                                     HttpServletRequest request) {
        ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(projectResource.getApplication());
        OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        boolean isLeadPartnerOrganisation = leadOrganisation.getId().equals(organisationResource.getId());
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(applicationResource.getCompetition());
        CompetitionResource competition = competitionService.getById(applicationResource.getCompetition());

        Supplier<String> successView = () ->
                "redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/eligibility";

        return doSaveEligibility(competition, applicationResource, projectResource, allSections, user,
                isLeadPartnerOrganisation, organisationResource, Eligibility.APPROVED, eligibilityForm, form,
                validationHandler, successView, bindingResult, model);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION')")
    @PostMapping(params = "save-and-continue")
    public String saveAndContinue(@PathVariable("projectId") Long projectId,
                                  @PathVariable("organisationId") Long organisationId,
                                  @ModelAttribute(FORM_ATTR_NAME) ApplicationForm form,
                                  @ModelAttribute("eligibilityForm") FinanceChecksEligibilityForm eligibilityForm,
                                  @SuppressWarnings("unused") BindingResult bindingResult,
                                  ValidationHandler validationHandler,
                                  Model model,
                                  HttpServletRequest request) {

        ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(projectResource.getApplication());
        OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        boolean isLeadPartnerOrganisation = leadOrganisation.getId().equals(organisationResource.getId());
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(applicationResource.getCompetition());
        CompetitionResource competition = competitionService.getById(applicationResource.getCompetition());

        Supplier<String> successView = () -> "redirect:/project/" + projectId + "/finance-check";

        return doSaveEligibility(competition, applicationResource, projectResource, allSections, user, isLeadPartnerOrganisation, organisationResource, Eligibility.REVIEW, eligibilityForm, form, validationHandler, successView, bindingResult, model);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION')")
    @GetMapping("/changes")
    public String viewExternalEligibilityChanges(@PathVariable("projectId") final Long projectId, @PathVariable("organisationId") final Long organisationId, Model model, @ModelAttribute("loggedInUser") UserResource loggedInUser){
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource organisation = organisationService.getOrganisationById(organisationId);
        return doViewEligibilityChanges(project, organisation, loggedInUser.getId(), model);
    }

    private String doSaveEligibility(CompetitionResource competition, ApplicationResource application, ProjectResource project, List<SectionResource> allSections, UserResource user, boolean isLeadOrganisation, OrganisationResource organisation, Eligibility eligibility, FinanceChecksEligibilityForm eligibilityForm, ApplicationForm form, ValidationHandler validationHandler, Supplier<String> successView, BindingResult bindingResult, Model model) {

        Supplier<String> failureView = () -> doViewEligibility(competition, application, project, allSections, user, isLeadOrganisation, organisation, model, eligibilityForm, form, bindingResult);

        EligibilityRagStatus statusToSend = getRagStatus(eligibilityForm);

        ServiceResult<Void> saveEligibilityResult = projectFinanceService.saveEligibility(project.getId(), organisation.getId(), eligibility, statusToSend);

        return validationHandler
                .addAnyErrors(saveEligibilityResult)
                .failNowOrSucceedWith(failureView, successView);

    }

    private EligibilityRagStatus getRagStatus(FinanceChecksEligibilityForm form) {
        EligibilityRagStatus statusToSend;

        if (form.isConfirmEligibilityChecked()) {
            statusToSend = form.getEligibilityRagStatus();
        } else {
            statusToSend = EligibilityRagStatus.UNSET;
        }
        return statusToSend;
    }

    private void populateProjectFinanceDetails(CompetitionResource competition, ApplicationResource application, ProjectResource project, Long organisationId, List<SectionResource> allSections, UserResource user, ApplicationForm form, BindingResult bindingResult, Model model){

        SectionResource section = simpleFilter(allSections, s -> s.getType().equals(PROJECT_COST_FINANCES)).get(0);

        addApplicationAndSectionsInternalWithOrgDetails(application, competition, user.getId(), Optional.ofNullable(section), Optional.empty(), model, form);

        BaseSectionViewModel openFinanceSectionViewModel = openFinanceSectionModel.populateModel(form, model, application, section, user, bindingResult, allSections, organisationId);

        model.addAttribute("model", openFinanceSectionViewModel);

        model.addAttribute("project", project);
    }

    private void addApplicationAndSectionsInternalWithOrgDetails(final ApplicationResource application,
                                                                 final CompetitionResource competition, final Long userId,
                                                                 Optional<SectionResource> section, Optional<Long> currentQuestionId,
                                                                 final Model model, final ApplicationForm form) {
        applicationModelPopulator.addApplicationAndSections(application, competition, userId, section, currentQuestionId, model, form);
    }

    private String doViewEligibilityChanges(ProjectResource project, OrganisationResource organisation, Long userId, Model model) {
        ProjectFinanceChangesViewModel projectFinanceChangesViewModel = ((DefaultProjectFinanceModelManager)financeHandler
                .getProjectFinanceModelManager(organisation.getOrganisationType()))
                    .getProjectFinanceChangesViewModel(true, project, organisation, userId);
        model.addAttribute("model", projectFinanceChangesViewModel);
        return "project/financecheck/eligibility-changes";
    }
}