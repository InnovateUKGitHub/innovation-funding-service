package org.innovateuk.ifs.project.eligibility.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.DefaultProjectFinanceModelManager;
import org.innovateuk.ifs.application.finance.view.FinanceViewHandlerProvider;
import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceChangesViewModel;
import org.innovateuk.ifs.application.forms.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.populator.OpenProjectFinanceSectionModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.ProjectFinanceService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ProjectFinanceRowRestService;
import org.innovateuk.ifs.financecheck.FinanceCheckService;
import org.innovateuk.ifs.financecheck.eligibility.form.FinanceChecksEligibilityForm;
import org.innovateuk.ifs.financecheck.eligibility.viewmodel.FinanceChecksEligibilityViewModel;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.eligibility.populator.FinanceChecksEligibilityProjectCostsFormPopulator;
import org.innovateuk.ifs.project.eligibility.viewmodel.FinanceChecksProjectCostsViewModel;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.EligibilityResource;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.FinanceUtil;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.util.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

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
    private OrganisationRestService organisationRestService;

    @Autowired
    private OpenProjectFinanceSectionModelPopulator openFinanceSectionModel;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationModelPopulator applicationModelPopulator;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @Autowired
    private FinanceViewHandlerProvider financeViewHandlerProvider;

    @Autowired
    private ProjectFinanceRowRestService projectFinanceRowRestService;

    @Autowired
    private FinanceUtil financeUtil;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private ApplicantRestService applicantRestService;

    @Autowired
    private FinanceChecksEligibilityProjectCostsFormPopulator formPopulator;

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @GetMapping
    public String viewEligibility(@P("projectId")@PathVariable("projectId") Long projectId,
                                  @PathVariable("organisationId") Long organisationId,
                                  @ModelAttribute(name = FORM_ATTR_NAME, binding = false) YourProjectCostsForm form,
                                  @RequestParam(value = "financeType", required = false) FinanceRowType rowType,
                                  BindingResult bindingResult,
                                  Model model,
                                  UserResource user) {
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        boolean isLeadPartnerOrganisation = leadOrganisation.getId().equals(organisation.getId());
        return doViewEligibility(project, isLeadPartnerOrganisation, organisation, model, null, form, rowType);
    }

    private String doViewEligibility(ProjectResource project, boolean isLeadPartnerOrganisation, OrganisationResource organisation, Model model, FinanceChecksEligibilityForm eligibilityForm, YourProjectCostsForm form, FinanceRowType rowType) {

        EligibilityResource eligibility = projectFinanceService.getEligibility(project.getId(), organisation.getId());

        if (eligibilityForm == null) {
            eligibilityForm = getEligibilityForm(eligibility);
        }

        FinanceCheckEligibilityResource eligibilityOverview = financeCheckService.getFinanceCheckEligibilityDetails(project.getId(), organisation.getId());

        boolean eligibilityApproved = eligibility.getEligibility() == EligibilityState.APPROVED;

        FileDetailsViewModel jesFileDetailsViewModel = null;
        boolean isUsingJesFinances = financeUtil.isUsingJesFinances(organisation.getOrganisationType());
        if (!isUsingJesFinances) {
            model.addAttribute("model", new FinanceChecksProjectCostsViewModel(!eligibilityApproved, rowType));
            formPopulator.populateForm(form, project.getId(), organisation.getId());
        } else {
            ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceByApplicationIdAndOrganisationId(project.getApplication(), organisation.getId());
            if (applicationFinanceResource.getFinanceFileEntry() != null) {
                FileEntryResource jesFileEntryResource = financeService.getFinanceEntry(applicationFinanceResource.getFinanceFileEntry()).getSuccess();
                jesFileDetailsViewModel = new FileDetailsViewModel(jesFileEntryResource);
            }
        }
        model.addAttribute("summaryModel", new FinanceChecksEligibilityViewModel(eligibilityOverview, organisation.getName(), project.getName(),
                project.getApplication(), isLeadPartnerOrganisation, project.getId(), organisation.getId(),
                eligibilityApproved, eligibility.getEligibilityRagStatus(), eligibility.getEligibilityApprovalUserFirstName(),
                eligibility.getEligibilityApprovalUserLastName(), eligibility.getEligibilityApprovalDate(), false, isUsingJesFinances, jesFileDetailsViewModel));

        model.addAttribute("eligibilityForm", eligibilityForm);

        return "project/financecheck/eligibility";
    }

    private FinanceChecksEligibilityForm getEligibilityForm(EligibilityResource eligibility) {

        boolean confirmEligibilityChecked = eligibility.getEligibilityRagStatus() != EligibilityRagStatus.UNSET;

        return new FinanceChecksEligibilityForm(eligibility.getEligibilityRagStatus(), confirmEligibilityChecked);
    }


//
//    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
//    @PostMapping(params = "save-eligibility")
//    public String projectFinanceFormSubmit(@P("projectId")@PathVariable("projectId") final Long projectId,
//                                           @PathVariable("organisationId") Long organisationId,
//                                           @Valid @ModelAttribute(FORM_ATTR_NAME) ApplicationForm form,
//                                           BindingResult bindingResult,
//                                           ValidationHandler validationHandler,
//                                           Model model,
//                                           UserResource user,
//                                           HttpServletRequest reques) {
//        ProjectResource projectResource = projectService.getById(projectId);
//        ApplicationResource applicationResource = applicationService.getById(projectResource.getApplication());
//        OrganisationResource organisationResource = organisationRestService.getOrganisationById(organisationId).getSuccess();
//        Long organisationType = organisationResource.getOrganisationType();
//
//        ValidationMessages saveApplicationErrors = saveProjectFinanceSection(applicationResource.getCompetition(), projectId, organisationType, organisationResource.getId(), request);
//
//        if(saveApplicationErrors.hasErrors()){
//            OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
//            boolean isLeadPartnerOrganisation = leadOrganisation.getId().equals(organisationResource.getId());
//            List<SectionResource> allSections = sectionService.getAllByCompetitionId(applicationResource.getCompetition());
//            CompetitionResource competition = competitionRestService.getCompetitionById(applicationResource.getCompetition()).getSuccess();
//            validationHandler.addAnyErrors(saveApplicationErrors);
//            return doViewEligibility(competition, applicationResource, projectResource, allSections, user, isLeadPartnerOrganisation, organisationResource, model, null, form, bindingResult, rowType);
//        } else {
//            return getRedirectUrlToEligibility(projectId, organisationId);
//        }
//    }
    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @GetMapping(value = "/" + ADD_COST + "/{"+QUESTION_ID+"}")
    public String addCostRow(@ModelAttribute(name = FORM_ATTR_NAME, binding = false) ApplicationForm form,
                             BindingResult bindingResult,
                             Model model,
                             @P("projectId")@PathVariable("projectId") Long projectId,
                             @PathVariable("organisationId") Long organisationId,
                             @PathVariable(QUESTION_ID) final Long questionId,
                             UserResource user) {
        Long organisationType = organisationRestService.getOrganisationById(organisationId).getSuccess().getOrganisationType();

        FinanceRowItem costItem = addCost(organisationType, organisationId, projectId, questionId);
        FinanceRowType costType = costItem.getCostType();
        financeViewHandlerProvider.getProjectFinanceModelManager(organisationType).addCost(model, costItem, projectId, organisationId, user.getId(), questionId, costType);

        form.setBindingResult(bindingResult);
        return String.format("finance/finance :: %s_row(viewmode='edit')", costType.getType());
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @GetMapping(value = "/remove_cost/{costId}")
    public @ResponseBody
    String removeCostRow(@P("projectId")@PathVariable("projectId") Long projectId,
                         @PathVariable("organisationId") Long organisationId,
                         @PathVariable("costId") final Long costId) throws JsonProcessingException {
        projectFinanceRowRestService.delete(projectId, organisationId, costId).getSuccess();
        AjaxResult ajaxResult = new AjaxResult(HttpStatus.OK, "true");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ajaxResult);
    }
    private ValidationMessages saveProjectFinanceSection(Long competitionId,
                                                         Long projectId,
                                                         Long organisationType,
                                                         Long organisationId,
                                                         HttpServletRequest request) {
        ValidationMessages errors = new ValidationMessages();

        ValidationMessages saveErrors = financeViewHandlerProvider.getProjectFinanceFormHandler(organisationType).update(request, organisationId, projectId, competitionId);

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
        return financeViewHandlerProvider.getProjectFinanceFormHandler(orgType).addCostWithoutPersisting(projectId, organisationId, questionId);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @PostMapping(params = "confirm-eligibility")
    public String confirmEligibility(@P("projectId")@PathVariable("projectId") Long projectId,
                                     @PathVariable("organisationId") Long organisationId,
                                     @ModelAttribute(FORM_ATTR_NAME) ApplicationForm form,
                                     @SuppressWarnings("unused") BindingResult bindingResult,
                                     @ModelAttribute("eligibilityForm") FinanceChecksEligibilityForm eligibilityForm,
                                     ValidationHandler validationHandler,
                                     Model model,
                                     UserResource user) {
        ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(projectResource.getApplication());
        OrganisationResource organisationResource = organisationRestService.getOrganisationById(organisationId).getSuccess();
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        boolean isLeadPartnerOrganisation = leadOrganisation.getId().equals(organisationResource.getId());
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(applicationResource.getCompetition());
        CompetitionResource competition = competitionRestService.getCompetitionById(applicationResource.getCompetition()).getSuccess();

        Supplier<String> successView = () ->
                "redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/eligibility";

        return "";
//        return doSaveEligibility(competition, applicationResource, projectResource, allSections, user,
//                isLeadPartnerOrganisation, organisationResource, EligibilityState.APPROVED, eligibilityForm, form,
//                validationHandler, successView, bindingResult, model);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @PostMapping(params = "save-and-continue")
    public String saveAndContinue(@P("projectId")@PathVariable("projectId") Long projectId,
                                  @PathVariable("organisationId") Long organisationId,
                                  @ModelAttribute(FORM_ATTR_NAME) ApplicationForm form,
                                  @ModelAttribute("eligibilityForm") FinanceChecksEligibilityForm eligibilityForm,
                                  @SuppressWarnings("unused") BindingResult bindingResult,
                                  ValidationHandler validationHandler,
                                  Model model,
                                  UserResource user) {

        ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(projectResource.getApplication());
        OrganisationResource organisationResource = organisationRestService.getOrganisationById(organisationId).getSuccess();
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        boolean isLeadPartnerOrganisation = leadOrganisation.getId().equals(organisationResource.getId());
        List<SectionResource> allSections = sectionService.getAllByCompetitionId(applicationResource.getCompetition());
        CompetitionResource competition = competitionRestService.getCompetitionById(applicationResource.getCompetition()).getSuccess();

        Supplier<String> successView = () -> "redirect:/project/" + projectId + "/finance-check";

        return "";
        //return doSaveEligibility(competition, applicationResource, projectResource, allSections, user, isLeadPartnerOrganisation, organisationResource, EligibilityState.REVIEW, eligibilityForm, form, validationHandler, successView, bindingResult, model);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @GetMapping("/changes")
    public String viewExternalEligibilityChanges(@P("projectId")@PathVariable("projectId") final Long projectId, @PathVariable("organisationId") final Long organisationId, Model model, UserResource loggedInUser){
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        return doViewEligibilityChanges(project, organisation, loggedInUser.getId(), model);
    }

//    private String doSaveEligibility(CompetitionResource competition, ApplicationResource application, ProjectResource project, List<SectionResource> allSections, UserResource user, boolean isLeadOrganisation, OrganisationResource organisation, EligibilityState eligibility, FinanceChecksEligibilityForm eligibilityForm, ApplicationForm form, ValidationHandler validationHandler, Supplier<String> successView, BindingResult bindingResult, Model model) {
//
//        Supplier<String> failureView = () -> doViewEligibility(competition, application, project, allSections, user, isLeadOrganisation, organisation, model, eligibilityForm, form, bindingResult, rowType);
//
//        EligibilityRagStatus statusToSend = getRagStatus(eligibilityForm);
//
//        ServiceResult<Void> saveEligibilityResult = projectFinanceService.saveEligibility(project.getId(), organisation.getId(), eligibility, statusToSend);
//
//        return validationHandler
//                .addAnyErrors(saveEligibilityResult)
//                .failNowOrSucceedWith(failureView, successView);
//
//    }

    private EligibilityRagStatus getRagStatus(FinanceChecksEligibilityForm form) {
        EligibilityRagStatus statusToSend;

        if (form.isConfirmEligibilityChecked()) {
            statusToSend = form.getEligibilityRagStatus();
        } else {
            statusToSend = EligibilityRagStatus.UNSET;
        }
        return statusToSend;
    }

    private String doViewEligibilityChanges(ProjectResource project, OrganisationResource organisation, Long userId, Model model) {
        ProjectFinanceChangesViewModel projectFinanceChangesViewModel = ((DefaultProjectFinanceModelManager) financeViewHandlerProvider
                .getProjectFinanceModelManager(organisation.getOrganisationType()))
                    .getProjectFinanceChangesViewModel(true, project, organisation, userId);
        model.addAttribute("model", projectFinanceChangesViewModel);
        return "project/financecheck/eligibility-changes";
    }
}
