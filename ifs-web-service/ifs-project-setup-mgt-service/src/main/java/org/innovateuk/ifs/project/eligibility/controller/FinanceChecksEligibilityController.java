package org.innovateuk.ifs.project.eligibility.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.view.DefaultProjectFinanceModelManager;
import org.innovateuk.ifs.application.finance.view.FinanceViewHandlerProvider;
import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceChangesViewModel;
import org.innovateuk.ifs.application.forms.yourprojectcosts.form.AbstractCostRowForm;
import org.innovateuk.ifs.application.forms.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.application.forms.yourprojectcosts.validator.YourProjectCostsFormValidator;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.populator.OpenProjectFinanceSectionModelPopulator;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.service.ServiceResult;
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
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.eligibility.populator.FinanceChecksEligibilityProjectCostsFormPopulator;
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

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * This controller serves the Eligibility page where internal users can confirm the eligibility of a partner organisation's
 * financial position on a Project
 */
@Controller
@RequestMapping("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility")
public class FinanceChecksEligibilityController extends AsyncAdaptor {

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

    @Autowired
    private YourProjectCostsFormValidator yourProjectCostsFormValidator;

    @Autowired
    private FinanceChecksEligibilityProjectCostsSaver yourProjectCostsSaver;

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @GetMapping
    public String viewEligibility(@P("projectId")@PathVariable("projectId") Long projectId,
                                  @PathVariable("organisationId") Long organisationId,
                                  @ModelAttribute(name = FORM_ATTR_NAME, binding = false) YourProjectCostsForm form,
                                  @RequestParam(value = "financeType", required = false) FinanceRowType rowType,
                                  BindingResult bindingResult,
                                  Model model,
                                  UserResource user) {
        return doViewEligibility(projectId, organisationId, model, null, form, rowType);
    }

    private String doViewEligibility(long projectId, long organisationId, Model model, FinanceChecksEligibilityForm eligibilityForm, YourProjectCostsForm form, FinanceRowType rowType) {
        Future<ProjectResource> project = async(() -> projectService.getById(projectId));
        Future<OrganisationResource> organisation = async(() -> organisationRestService.getOrganisationById(organisationId).getSuccess());
        Future<OrganisationResource> leadOrganisation = async(() -> projectService.getLeadOrganisation(projectId));
        Future<EligibilityResource> eligibility = async(() -> projectFinanceService.getEligibility(projectId, organisationId));
        Future<FinanceCheckEligibilityResource> eligibilityOverview = async(() -> financeCheckService.getFinanceCheckEligibilityDetails(projectId, organisationId));

        try {
            if (eligibilityForm == null) {
                eligibilityForm = getEligibilityForm(eligibility.get());
            }
            boolean eligibilityApproved = eligibility.get().getEligibility() == EligibilityState.APPROVED;

            FileDetailsViewModel jesFileDetailsViewModel = null;
            boolean isUsingJesFinances = financeUtil.isUsingJesFinances(organisation.get().getOrganisationType());
            if (!isUsingJesFinances) {
                model.addAttribute("model", new FinanceChecksProjectCostsViewModel(!eligibilityApproved, rowType));
                formPopulator.populateForm(form, projectId, organisationId);
            } else {
                ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceByApplicationIdAndOrganisationId(project.get().getApplication(), organisation.get().getId());
                if (applicationFinanceResource.getFinanceFileEntry() != null) {
                    FileEntryResource jesFileEntryResource = financeService.getFinanceEntry(applicationFinanceResource.getFinanceFileEntry()).getSuccess();
                    jesFileDetailsViewModel = new FileDetailsViewModel(jesFileEntryResource);
                }
            }

            boolean isLeadPartnerOrganisation = leadOrganisation.get().getId().equals(organisationId);

            model.addAttribute("summaryModel", new FinanceChecksEligibilityViewModel(eligibilityOverview.get(), organisation.get().getName(), project.get().getName(),
                    project.get().getApplication(), isLeadPartnerOrganisation, project.get().getId(), organisation.get().getId(),
                    eligibilityApproved, eligibility.get().getEligibilityRagStatus(), eligibility.get().getEligibilityApprovalUserFirstName(),
                    eligibility.get().getEligibilityApprovalUserLastName(), eligibility.get().getEligibilityApprovalDate(), false, isUsingJesFinances, jesFileDetailsViewModel));

            model.addAttribute("eligibilityForm", eligibilityForm);

            return "project/financecheck/eligibility";
        } catch (InterruptedException | ExecutionException e) {
            throw new IFSRuntimeException(e, Collections.emptyList());
        }
    }

    private FinanceChecksEligibilityForm getEligibilityForm(EligibilityResource eligibility) {

        boolean confirmEligibilityChecked = eligibility.getEligibilityRagStatus() != EligibilityRagStatus.UNSET;

        return new FinanceChecksEligibilityForm(eligibility.getEligibilityRagStatus(), confirmEligibilityChecked);
    }



    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @PostMapping(params = "save-eligibility")
    public String projectFinanceFormSubmit(@P("projectId")@PathVariable("projectId") final Long projectId,
                                           @PathVariable("organisationId") Long organisationId,
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

    @PostMapping("remove-row/{rowId}")
    public @ResponseBody
    String ajaxRemoveRow(UserResource user,
                         @PathVariable String rowId) throws JsonProcessingException {
        yourProjectCostsSaver.removeFinanceRow(rowId);
        AjaxResult ajaxResult = new AjaxResult(HttpStatus.OK, "true");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ajaxResult);
    }

    @PostMapping("add-row/{rowType}")
    public String ajaxAddRow(Model model,
                             UserResource user,
                             @PathVariable long projectId,
                             @PathVariable long organisationId,
                             @PathVariable FinanceRowType rowType) throws InstantiationException, IllegalAccessException {
        YourProjectCostsForm form = new YourProjectCostsForm();
        AbstractCostRowForm row = yourProjectCostsSaver.addRowForm(form, rowType, projectId, organisationId);
        model.addAttribute("form", form);
        model.addAttribute("id", row.getCostId());
        model.addAttribute("row", row);
        return String.format("application/your-project-costs-fragments :: ajax_%s_row", rowType.name().toLowerCase());
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
    public String saveAndContinue(@P("projectId")@PathVariable("projectId") Long projectId,
                                  @PathVariable("organisationId") Long organisationId,
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
    public String viewExternalEligibilityChanges(@P("projectId")@PathVariable("projectId") final Long projectId, @PathVariable("organisationId") final Long organisationId, Model model, UserResource loggedInUser){
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
        ProjectFinanceChangesViewModel projectFinanceChangesViewModel = ((DefaultProjectFinanceModelManager) financeViewHandlerProvider
                .getProjectFinanceModelManager(organisation.getOrganisationType()))
                    .getProjectFinanceChangesViewModel(true, project, organisation, userId);
        model.addAttribute("model", projectFinanceChangesViewModel);
        return "project/financecheck/eligibility-changes";
    }
}
