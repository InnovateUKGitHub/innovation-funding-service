package org.innovateuk.ifs.project.eligibility.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.populator.OpenProjectFinanceSectionModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.service.*;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.profiling.ProfileExecution;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.eligibility.form.FinanceChecksEligibilityForm;
import org.innovateuk.ifs.project.eligibility.viewmodel.FinanceChecksEligibilityViewModel;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.innovateuk.ifs.project.finance.resource.Eligibility;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.EligibilityResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRowService;
import org.innovateuk.ifs.project.financecheck.FinanceCheckService;
import org.innovateuk.ifs.project.resource.ProjectResource;
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
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.resource.SectionType.PROJECT_COST_FINANCES;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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
    private ProjectFinanceService financeService;

    @Autowired
    private FinanceHandler financeHandler;

    @Autowired
    private ProjectFinanceRowService financeRowService;

    @Autowired
    private QuestionService questionService;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION')")
    @RequestMapping(method = GET)
    public String viewEligibility(@PathVariable("projectId") Long projectId,
                                  @PathVariable("organisationId") Long organisationId,
                                  @ModelAttribute(FORM_ATTR_NAME) ApplicationForm form,
                                  BindingResult bindingResult,
                                  Model model,
                                  HttpServletRequest request) {
        return doViewEligibility(projectId, organisationId, model, null, form, bindingResult, request);
    }

    private String doViewEligibility(Long projectId, Long organisationId, Model model, FinanceChecksEligibilityForm eligibilityForm, ApplicationForm form, BindingResult bindingResult, HttpServletRequest request) {

        poulateProjectFinanceDetails(projectId, organisationId, form, bindingResult, model, request);

        EligibilityResource eligibility = financeService.getEligibility(projectId, organisationId);

        if (eligibilityForm == null) {
            eligibilityForm = getEligibilityForm(eligibility);
        }

        model.addAttribute("model", getViewModel(projectId, organisationId, eligibility));

        model.addAttribute("eligibilityForm", eligibilityForm);
        model.addAttribute("form", form);

        return "project/financecheck/eligibility";
    }

    private FinanceChecksEligibilityForm getEligibilityForm(EligibilityResource eligibility) {

        boolean confirmEligibilityChecked = eligibility.getEligibilityRagStatus() != EligibilityRagStatus.UNSET;

        return new FinanceChecksEligibilityForm(eligibility.getEligibilityRagStatus(), confirmEligibilityChecked);
    }

    private FinanceChecksEligibilityViewModel getViewModel(Long projectId, Long organisationId, EligibilityResource eligibility) {
        ProjectResource project = projectService.getById(projectId);
        ApplicationResource application = applicationService.getById(project.getApplication());

        OrganisationResource organisation = organisationService.getOrganisationById(organisationId);
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        boolean leadPartnerOrganisation = leadOrganisation.getId().equals(organisation.getId());

        FinanceCheckEligibilityResource eligibilityOverview = financeCheckService.getFinanceCheckEligibilityDetails(projectId, organisationId);

        boolean eligibilityApproved = eligibility.getEligibility() == Eligibility.APPROVED;

        return new FinanceChecksEligibilityViewModel(eligibilityOverview, organisation.getName(), project.getName(),
                application.getFormattedId(), leadPartnerOrganisation, project.getId(),
                eligibilityApproved, eligibility.getEligibilityRagStatus(), eligibility.getEligibilityApprovalUserFirstName(),
                eligibility.getEligibilityApprovalUserLastName(), eligibility.getEligibilityApprovalDate());
    }

    @RequestMapping(value = "/" + ADD_COST + "/{"+QUESTION_ID+"}")
    public String addCostRow(@ModelAttribute(FORM_ATTR_NAME) ApplicationForm form,
                             BindingResult bindingResult,
                             Model model,
                             @PathVariable("projectId") Long projectId,
                             @PathVariable("organisationId") Long organisationId,
                             @PathVariable(QUESTION_ID) final Long questionId,
                             HttpServletRequest request) {
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        String organisationType = organisationService.getOrganisationById(organisationId).getOrganisationTypeName();

        FinanceRowItem costItem = addCost(organisationType, organisationId, projectId, questionId);
        FinanceRowType costType = costItem.getCostType();
        financeHandler.getProjectFinanceModelManager(organisationType).addCost(model, costItem, projectId, organisationId, user.getId(), questionId, costType);

        form.setBindingResult(bindingResult);
        return String.format("finance/finance :: %s_row", costType.getType());
    }

    @RequestMapping(value = "/remove_cost/{costId}")
    public @ResponseBody
    String removeCostRow(@PathVariable("costId") final Long costId) throws JsonProcessingException {
        financeRowService.delete(costId);
        AjaxResult ajaxResult = new AjaxResult(HttpStatus.OK, "true");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ajaxResult);
    }

    /**
     * This method is for the post request when the users clicks the input[type=submit] button.
     * This is also used when the user clicks the 'mark-as-complete' button or reassigns a question to another user.
     */
    @ProfileExecution
    @RequestMapping(method = RequestMethod.POST, params = "save-eligibility")
    public String projectFinanceFormSubmit(@PathVariable("projectId") final Long projectId,
                                           @PathVariable("organisationId") Long organisationId,
                                           @Valid @ModelAttribute(FORM_ATTR_NAME) ApplicationForm form,
                                           BindingResult bindingResult,
                                           ValidationHandler validationHandler,
                                           Model model,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(projectResource.getApplication());
        OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);
        String organisationType = organisationResource.getOrganisationTypeName();

        ValidationMessages saveApplicationErrors = saveProjectFinanceSection(applicationResource.getCompetition(), projectId, organisationType, organisationResource.getId(), request);

        if(saveApplicationErrors.hasErrors()){
            validationHandler.addAnyErrors(saveApplicationErrors);
            return doViewEligibility(projectId, organisationId, model, null, form, bindingResult, request);
        } else {
            return getRedirectUrlToEligibility(projectId, organisationId);
        }
    }

    private ValidationMessages saveProjectFinanceSection(Long competitionId,
                                                         Long projectId,
                                                         String organisationType,
                                                         Long organisationId,
                                                         HttpServletRequest request) {
        ValidationMessages errors = new ValidationMessages();

        ValidationMessages saveErrors = financeHandler.getProjectFinanceFormHandler(organisationType).update(request, organisationId, projectId, competitionId);

        errors.addAll(saveErrors);

        return errors;
    }

    private String getRedirectUrlToEligibility(Long projectId, Long organisationId){
        return "redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/eligibility";
    }

    private FinanceRowItem addCost(String orgType, Long organisationId, Long projectId, Long questionId) {
        return financeHandler.getProjectFinanceFormHandler(orgType).addCostWithoutPersisting(projectId, organisationId, questionId);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION')")
    @RequestMapping(method = POST, params = "confirm-eligibility")
    public String confirmEligibility(@PathVariable("projectId") Long projectId,
                                     @PathVariable("organisationId") Long organisationId,
                                     @ModelAttribute(FORM_ATTR_NAME) ApplicationForm form,
                                     @SuppressWarnings("unused") BindingResult bindingResult,
                                     @ModelAttribute("eligibilityForm") FinanceChecksEligibilityForm eligibilityForm,
                                     ValidationHandler validationHandler,
                                     Model model,
                                     HttpServletRequest request) {

        Supplier<String> successView = () ->
                "redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/eligibility";

        return doSaveEligibility(projectId, organisationId, Eligibility.APPROVED, eligibilityForm, form, validationHandler, successView, bindingResult, request, model);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION')")
    @RequestMapping(method = POST, params = "save-and-continue")
    public String saveAndContinue(@PathVariable("projectId") Long projectId,
                                  @PathVariable("organisationId") Long organisationId,
                                  @ModelAttribute(FORM_ATTR_NAME) ApplicationForm form,
                                  @ModelAttribute("eligibilityForm") FinanceChecksEligibilityForm eligibilityForm,
                                  @SuppressWarnings("unused") BindingResult bindingResult,
                                  ValidationHandler validationHandler,
                                  Model model,
                                  HttpServletRequest request) {

        Supplier<String> successView = () -> "redirect:/project/" + projectId + "/finance-check";

        return doSaveEligibility(projectId, organisationId, Eligibility.REVIEW, eligibilityForm, form, validationHandler, successView, bindingResult, request, model);
    }

    private String doSaveEligibility(Long projectId, Long organisationId, Eligibility eligibility, FinanceChecksEligibilityForm eligibilityForm, ApplicationForm form,
                                     ValidationHandler validationHandler, Supplier<String> successView, BindingResult bindingResult, HttpServletRequest request, Model model) {

        Supplier<String> failureView = () -> doViewEligibility(projectId, organisationId, model, eligibilityForm, form, bindingResult, request);

        EligibilityRagStatus statusToSend = getRagStatus(eligibilityForm);

        ServiceResult<Void> saveEligibilityResult = financeService.saveEligibility(projectId, organisationId, eligibility, statusToSend);

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

    private void poulateProjectFinanceDetails(Long projectId, Long organisationId, ApplicationForm form, BindingResult bindingResult, Model model, HttpServletRequest request){
        ProjectResource project = projectService.getById(projectId);
        UserResource user = userAuthenticationService.getAuthenticatedUser(request);
        ApplicationResource application = applicationService.getById(project.getApplication());
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(application.getCompetition());
        CompetitionResource competition = competitionService.getById(application.getCompetition());

        SectionResource section = simpleFilter(allSections, s -> s.getType().equals(PROJECT_COST_FINANCES)).get(0);

        addApplicationAndSectionsInternalWithOrgDetails(application, competition, user.getId(), Optional.ofNullable(section), Optional.empty(), model, form);

        openFinanceSectionModel.populateModel(form, model, application, section, user, bindingResult, allSections, organisationId);

        model.addAttribute("project", project);
    }

    private void addApplicationAndSectionsInternalWithOrgDetails(final ApplicationResource application, final CompetitionResource competition, final Long userId, Optional<SectionResource> section, Optional<Long> currentQuestionId, final Model model, final ApplicationForm form) {
        applicationModelPopulator.addApplicationAndSections(application, competition, userId, section, currentQuestionId, model, form);
    }
}
