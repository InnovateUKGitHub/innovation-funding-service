package org.innovateuk.ifs.project.eligibility.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.FinanceViewHandlerProvider;
import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceChangesViewModel;
import org.innovateuk.ifs.application.forms.yourprojectcosts.form.AbstractCostRowForm;
import org.innovateuk.ifs.application.forms.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.application.forms.yourprojectcosts.validator.YourProjectCostsFormValidator;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.ProjectFinanceService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.financecheck.FinanceCheckService;
import org.innovateuk.ifs.financecheck.eligibility.form.FinanceChecksEligibilityForm;
import org.innovateuk.ifs.financecheck.eligibility.viewmodel.FinanceChecksEligibilityViewModel;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.eligibility.populator.FinanceChecksEligibilityProjectCostsFormPopulator;
import org.innovateuk.ifs.project.eligibility.populator.ProjectFinanceChangesViewModelPopulator;
import org.innovateuk.ifs.project.eligibility.saver.FinanceChecksEligibilityProjectCostsSaver;
import org.innovateuk.ifs.project.eligibility.viewmodel.FinanceChecksProjectCostsViewModel;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.EligibilityResource;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.FinanceUtil;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.util.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.function.Supplier;

/**
 * This controller serves the Eligibility page where internal users can confirm the eligibility of a partner organisation's
 * financial position on a Project
 */
@Controller
@RequestMapping("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility")
public class FinanceChecksEligibilityController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private FinanceCheckService financeCheckService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @Autowired
    private FinanceViewHandlerProvider financeViewHandlerProvider;

    @Autowired
    private FinanceUtil financeUtil;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private FinanceChecksEligibilityProjectCostsFormPopulator formPopulator;

    @Autowired
    private YourProjectCostsFormValidator yourProjectCostsFormValidator;

    @Autowired
    private FinanceChecksEligibilityProjectCostsSaver yourProjectCostsSaver;

    @Autowired
    private ProjectFinanceChangesViewModelPopulator projectFinanceChangesViewModelPopulator;

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @GetMapping
    public String viewEligibility(@PathVariable long projectId,
                                  @PathVariable Long organisationId,
                                  @ModelAttribute(name = FORM_ATTR_NAME, binding = false) YourProjectCostsForm form,
                                  @RequestParam(value = "financeType", required = false) FinanceRowType rowType,
                                  BindingResult bindingResult,
                                  Model model,
                                  UserResource user) {
        return doViewEligibility(projectId, organisationId, model, null, form, rowType);
    }

    private String doViewEligibility(long projectId, long organisationId, Model model, FinanceChecksEligibilityForm eligibilityForm, YourProjectCostsForm form, FinanceRowType rowType) {
        ProjectResource project = projectService.getById(projectId);
        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        EligibilityResource eligibility = projectFinanceService.getEligibility(projectId, organisationId);
        FinanceCheckEligibilityResource eligibilityOverview = financeCheckService.getFinanceCheckEligibilityDetails(projectId, organisationId);

        if (eligibilityForm == null) {
            eligibilityForm = getEligibilityForm(eligibility);
        }
        boolean eligibilityApproved = eligibility.getEligibility() == EligibilityState.APPROVED;

        FileDetailsViewModel jesFileDetailsViewModel = null;
        boolean isUsingJesFinances = financeUtil.isUsingJesFinances(competition, organisation.getOrganisationType());
        if (!isUsingJesFinances) {
            model.addAttribute("model", new FinanceChecksProjectCostsViewModel(!eligibilityApproved, rowType));
            formPopulator.populateForm(form, projectId, organisationId);
        } else {
            ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceByApplicationIdAndOrganisationId(project.getApplication(), organisation.getId());
            if (applicationFinanceResource.getFinanceFileEntry() != null) {
                FileEntryResource jesFileEntryResource = financeService.getFinanceEntry(applicationFinanceResource.getFinanceFileEntry()).getSuccess();
                jesFileDetailsViewModel = new FileDetailsViewModel(jesFileEntryResource);
            }
        }

        boolean isLeadPartnerOrganisation = leadOrganisation.getId().equals(organisationId);

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

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @PostMapping(params = "save-eligibility")
    public String projectFinanceFormSubmit(@PathVariable final Long projectId,
                                           @PathVariable Long organisationId,
                                           @RequestParam("save-eligibility") FinanceRowType type,
                                           @ModelAttribute(FORM_ATTR_NAME) YourProjectCostsForm form,
                                           BindingResult bindingResult,
                                           ValidationHandler validationHandler,
                                           Model model,
                                           UserResource user) {

        Supplier<String> successView = () -> getRedirectUrlToEligibility(projectId, organisationId);
        Supplier<String> failureView = () -> viewEligibility(projectId, organisationId, form, type, bindingResult, model, user);
        yourProjectCostsFormValidator.validateType(form, type, validationHandler);
        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(yourProjectCostsSaver.saveType(form, type, projectId, organisationId));
            return validationHandler.failNowOrSucceedWith(failureView, successView);
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @PostMapping("remove-row/{rowId}")
    public @ResponseBody
    String ajaxRemoveRow(@PathVariable long projectId,
                         @PathVariable long organisationId,
                         @PathVariable String rowId) throws JsonProcessingException {
        yourProjectCostsSaver.removeFinanceRow(rowId);
        AjaxResult ajaxResult = new AjaxResult(HttpStatus.OK, "true");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ajaxResult);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @PostMapping("add-row/{rowType}")
    public String ajaxAddRow(Model model,
                             @PathVariable long projectId,
                             @PathVariable long organisationId,
                             @PathVariable FinanceRowType rowType) throws InstantiationException, IllegalAccessException {
        YourProjectCostsForm form = new YourProjectCostsForm();
        Map.Entry<String, AbstractCostRowForm> entry = yourProjectCostsSaver.addRowForm(form, rowType);
        model.addAttribute("form", form);
        model.addAttribute("id", entry.getKey());
        model.addAttribute("row", entry.getValue());
        return String.format("application/your-project-costs-fragments :: ajax_%s_row", rowType.name().toLowerCase());
    }

    private String getRedirectUrlToEligibility(Long projectId, Long organisationId){
        return "redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/eligibility";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @PostMapping(params = "confirm-eligibility")
    @AsyncMethod
    public String confirmEligibility(@PathVariable long projectId,
                                     @PathVariable long organisationId,
                                     @ModelAttribute(FORM_ATTR_NAME) YourProjectCostsForm form,
                                     @SuppressWarnings("unused") BindingResult bindingResult,
                                     @ModelAttribute("eligibilityForm") FinanceChecksEligibilityForm eligibilityForm,
                                     ValidationHandler validationHandler,
                                     Model model,
                                     UserResource user) {
        Supplier<String> successView = () ->
                "redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/eligibility";

        return doSaveEligibility(projectId, organisationId, EligibilityState.APPROVED, eligibilityForm, form,
                validationHandler, successView, model);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @PostMapping(params = "save-and-continue")
    @AsyncMethod
    public String saveAndContinue(@PathVariable long projectId,
                                  @PathVariable Long organisationId,
                                  @ModelAttribute(FORM_ATTR_NAME) YourProjectCostsForm form,
                                  @ModelAttribute("eligibilityForm") FinanceChecksEligibilityForm eligibilityForm,
                                  @SuppressWarnings("unused") BindingResult bindingResult,
                                  ValidationHandler validationHandler,
                                  Model model,
                                  UserResource user) {

        Supplier<String> successView = () -> "redirect:/project/" + projectId + "/finance-check";

        return doSaveEligibility(projectId, organisationId, EligibilityState.REVIEW, eligibilityForm, form, validationHandler, successView, model);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @GetMapping("/changes")
    public String viewExternalEligibilityChanges(@PathVariable final Long projectId, @PathVariable final Long organisationId, Model model, UserResource loggedInUser){
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        return doViewEligibilityChanges(project, organisation, loggedInUser.getId(), model);
    }

    private String doSaveEligibility(long projectId, long organisationId, EligibilityState eligibility, FinanceChecksEligibilityForm eligibilityForm, YourProjectCostsForm form, ValidationHandler validationHandler, Supplier<String> successView, Model model) {

        Supplier<String> failureView = () -> doViewEligibility(projectId, organisationId, model, eligibilityForm, form, null);

        EligibilityRagStatus statusToSend = getRagStatus(eligibilityForm);

        ServiceResult<Void> saveEligibilityResult = projectFinanceService.saveEligibility(projectId, organisationId, eligibility, statusToSend);

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

    private String doViewEligibilityChanges(ProjectResource project, OrganisationResource organisation, Long userId, Model model) {
        ProjectFinanceChangesViewModel projectFinanceChangesViewModel = projectFinanceChangesViewModelPopulator
                .getProjectFinanceChangesViewModel(true, project, organisation, userId);
        model.addAttribute("model", projectFinanceChangesViewModel);
        return "project/financecheck/eligibility-changes";
    }
}
