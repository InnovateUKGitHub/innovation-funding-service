package org.innovateuk.ifs.project.eligibility.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceChangesViewModel;
import org.innovateuk.ifs.application.forms.academiccosts.form.AcademicCostForm;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.validator.YourProjectCostsFormValidator;
import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.financecheck.FinanceCheckService;
import org.innovateuk.ifs.financecheck.eligibility.form.FinanceChecksEligibilityForm;
import org.innovateuk.ifs.financecheck.eligibility.viewmodel.FinanceChecksEligibilityViewModel;
import org.innovateuk.ifs.grantofferletter.GrantOfferLetterService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.eligibility.form.ResetEligibilityForm;
import org.innovateuk.ifs.project.eligibility.populator.FinanceChecksEligibilityProjectCostsFormPopulator;
import org.innovateuk.ifs.project.eligibility.populator.ProjectAcademicCostFormPopulator;
import org.innovateuk.ifs.project.eligibility.populator.ProjectFinanceChangesViewModelPopulator;
import org.innovateuk.ifs.project.eligibility.saver.FinanceChecksEligibilityProjectCostsSaver;
import org.innovateuk.ifs.project.eligibility.saver.ProjectAcademicCostsSaver;
import org.innovateuk.ifs.project.eligibility.viewmodel.FinanceChecksProjectCostsViewModel;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.EligibilityResource;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.util.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This controller serves the Eligibility page where internal users can confirm the eligibility of a partner organisation's
 * financial position on a Project
 */
@Controller
@RequestMapping("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility")
public class FinanceChecksEligibilityController extends AsyncAdaptor {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private FinanceCheckService financeCheckService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Autowired
    private FinanceCheckRestService financeCheckRestService;

    @Autowired
    private FinanceChecksEligibilityProjectCostsFormPopulator formPopulator;

    @Autowired
    private YourProjectCostsFormValidator yourProjectCostsFormValidator;

    @Autowired
    private FinanceChecksEligibilityProjectCostsSaver yourProjectCostsSaver;

    @Autowired
    private ProjectFinanceChangesViewModelPopulator projectFinanceChangesViewModelPopulator;

    @Autowired
    private ProjectAcademicCostFormPopulator projectAcademicCostFormPopulator;

    @Autowired
    private ProjectAcademicCostsSaver projectAcademicCostsSaver;

    @Autowired
    private GrantOfferLetterService grantOfferLetterService;

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @GetMapping
    @AsyncMethod
    public String viewEligibility(@PathVariable long projectId,
                                  @PathVariable long organisationId,
                                  @RequestParam(value = "editAcademicFinances", required = false) boolean editAcademicFinances,
                                  @RequestParam(value = "editProjectCosts", required = false) boolean editProjectCosts,
                                  Model model,
                                  UserResource user) {
        return doViewEligibility(projectId, organisationId, model, null, new ResetEligibilityForm(), null, null, editAcademicFinances, user, editProjectCosts);
    }

    private String doViewEligibility(long projectId, long organisationId, Model model, FinanceChecksEligibilityForm eligibilityForm, ResetEligibilityForm resetEligibilityForm, YourProjectCostsForm form, AcademicCostForm academicCostForm, boolean editAcademicFinances, UserResource user, boolean canEditProjectCosts) {
        ProjectResource project = projectService.getById(projectId);
        Future<CompetitionResource> competition = async(() -> competitionRestService.getCompetitionById(project.getCompetition()).getSuccess());
        Future<OrganisationResource> organisation = async(() -> organisationRestService.getOrganisationById(organisationId).getSuccess());
        Future<OrganisationResource> leadOrganisation = async(() -> projectService.getLeadOrganisation(projectId));
        Future<EligibilityResource> eligibility = async(() -> financeCheckRestService.getEligibility(projectId, organisationId).getSuccess());
        Future<FinanceCheckEligibilityResource> eligibilityOverview = async(() -> financeCheckService.getFinanceCheckEligibilityDetails(projectId, organisationId));

        try {
            Future<Model> future = CompletableFuture.completedFuture(model);
            if (eligibilityForm == null) {
                eligibilityForm = getEligibilityForm(eligibility.get());
            }
            EligibilityState eligibilityState = eligibility.get().getEligibility();

            List<ProjectFinanceResource> projectFinances = projectFinanceRestService.getProjectFinances(projectId).getSuccess();

            boolean isUsingJesFinances = competition.get().applicantShouldUseJesFinances(organisation.get().getOrganisationTypeEnum());
            if (!isUsingJesFinances) {
                boolean open = EligibilityState.APPROVED != eligibilityState && project.getProjectState().isActive();
                Optional<ProjectFinanceResource> organisationProjectFinance = projectFinances.stream()
                        .filter(projectFinance -> projectFinance.getOrganisation().longValue() == organisationId)
                        .findFirst();
                model.addAttribute("model", new FinanceChecksProjectCostsViewModel(project.getApplication(), competition.get().getName(), open, competition.get().getFinanceRowTypesByFinance(organisationProjectFinance), competition.get().isOverheadsAlwaysTwenty(), competition.get().getFundingType() == FundingType.KTP, canEditProjectCosts));
                if (form == null) {
                    future = async(() -> model.addAttribute("form", formPopulator.populateForm(projectId, organisation.get().getId())));
                }
                else {
                    form.recalculateTotals();
                    form.orderAssociateCosts();
                }

            } else {
                if (academicCostForm == null) {
                    future = async(() -> model.addAttribute("academicCostForm", projectAcademicCostFormPopulator.populate(new AcademicCostForm(), projectId, organisationId)));
                }
            }

            boolean isLeadPartnerOrganisation = leadOrganisation.get().getId().equals(organisationId);

            boolean resetableGolState = false;
            if (user.isInternalUser()) {
                GrantOfferLetterStateResource golState = grantOfferLetterService.getGrantOfferLetterState(projectId).getSuccess();
                resetableGolState = golState.getState() != GrantOfferLetterState.APPROVED;
            }

            boolean showChangesLink = projectFinanceChangesViewModelPopulator.getProjectFinanceChangesViewModel(true, project, organisation.get()).showChanges();

            model.addAttribute("summaryModel", new FinanceChecksEligibilityViewModel(project, competition.get(), eligibilityOverview.get(),
                    organisation.get().getName(),
                    isLeadPartnerOrganisation,
                    organisation.get().getId(),
                    eligibilityState,
                    eligibility.get().getEligibilityRagStatus(),
                    eligibility.get().getEligibilityApprovalUserFirstName(),
                    eligibility.get().getEligibilityApprovalUserLastName(),
                    eligibility.get().getEligibilityApprovalDate(),
                    eligibility.get().getEligibilityResetUserFirstName(),
                    eligibility.get().getEligibilityResetUserLastName(),
                    eligibility.get().getEligibilityResetDate(),
                    false,
                    isUsingJesFinances,
                    editAcademicFinances,
                    projectFinances,
                    resetableGolState,
                    showChangesLink,
                    canEditProjectCosts
            ));

            model.addAttribute("eligibilityForm", eligibilityForm);
            model.addAttribute("resetForm", resetEligibilityForm);
            future.get();

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
    @PostMapping(params = "save-academic-costs")
    @AsyncMethod
    public String saveAcademicCosts(@PathVariable long projectId,
                                    @PathVariable long organisationId,
                                    @Valid @ModelAttribute("academicCostForm") AcademicCostForm form,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler,
                                    Model model,
                                    UserResource user) {

        Supplier<String> successView = () -> getRedirectUrlToEligibility(projectId, organisationId);
        Supplier<String> failureView = () -> doViewEligibility(projectId, organisationId, model, null, new ResetEligibilityForm(), null, form, true, user, false);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(projectAcademicCostsSaver.save(form, projectId, organisationId));
            return validationHandler.failNowOrSucceedWith(failureView, successView);
        });
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @PostMapping(params = "save-eligibility")
    @AsyncMethod
    public String projectFinanceFormSubmit(@PathVariable long projectId,
                                           @PathVariable long organisationId,
                                           @Valid @ModelAttribute("form") YourProjectCostsForm form,
                                           BindingResult bindingResult,
                                           ValidationHandler validationHandler,
                                           Model model,
                                           UserResource user) {

        Supplier<String> successView = () -> getRedirectUrlToEligibility(projectId, organisationId);
        Supplier<String> failureView = () -> doViewEligibility(projectId, organisationId, model, null, new ResetEligibilityForm(), form, null, false, user, true);

        ProjectFinanceResource projectFinance = projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionForProject(projectId).getSuccess();

        OrganisationResource organisationResource = organisationRestService.getOrganisationById(organisationId).getSuccess();

        List<FinanceRowType> financeRowTypes = competition.getFinanceRowTypesByFinance(Optional.of(projectFinance))
                .stream()
                .filter(FinanceRowType::isAppearsInProjectCostsAccordion)
                .collect(Collectors.toList());

        financeRowTypes.forEach(type -> yourProjectCostsFormValidator.validateType(form, type, validationHandler));

        ProjectResource project = projectService.getById(projectId);
        boolean ktp = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess().isKtp();

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            validationHandler.addAnyErrors(yourProjectCostsSaver.save(form, projectId, organisationResource, new ValidationMessages()));
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
                             @PathVariable FinanceRowType rowType) throws InstantiationException, IllegalAccessException {
        YourProjectCostsForm form = new YourProjectCostsForm();
        Map.Entry<String, AbstractCostRowForm> entry = yourProjectCostsSaver.addRowForm(form, rowType);
        model.addAttribute("form", form);
        model.addAttribute("id", entry.getKey());
        model.addAttribute("row", entry.getValue());
        return String.format("application/your-project-costs-fragments :: ajax_%s_row", rowType.name().toLowerCase());
    }

    private String getRedirectUrlToEligibility(Long projectId, Long organisationId) {
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

        return doSaveEligibility(projectId, organisationId, EligibilityState.APPROVED, eligibilityForm, form, validationHandler, successView, model, user);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @PostMapping(params = "reset-eligibility")
    @AsyncMethod
    public String resetEligibility(@PathVariable long projectId,
                                     @PathVariable long organisationId,
                                     @ModelAttribute("resetForm") ResetEligibilityForm resetEligibilityForm,
                                     @SuppressWarnings("unused") BindingResult bindingResult,
                                     ValidationHandler validationHandler,
                                     Model model,
                                     UserResource user) {
        Supplier<String> successView = () ->
                "redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/eligibility";

        Supplier<String> failureView = () -> doViewEligibility(projectId, organisationId, model, null, resetEligibilityForm, null, null,  false, user, false);

        if (StringUtils.isEmpty(resetEligibilityForm.getRetractionReason())) {
            bindingResult.addError(new FieldError("resetForm", "retractionReason", "Enter a reason for the reset."));
            return failureView.get();
        }

        RestResult<Void> resetEligibilityResult = financeCheckRestService.resetEligibility(projectId, organisationId, resetEligibilityForm.getRetractionReason());
        return validationHandler
                .addAnyErrors(resetEligibilityResult)
                .failNowOrSucceedWith(failureView, successView);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @PostMapping(params = "save-and-continue")
    @AsyncMethod
    public String saveAndContinue(@PathVariable long projectId,
                                  @PathVariable long organisationId,
                                  @ModelAttribute(FORM_ATTR_NAME) YourProjectCostsForm form,
                                  @ModelAttribute("eligibilityForm") FinanceChecksEligibilityForm eligibilityForm,
                                  @SuppressWarnings("unused") BindingResult bindingResult,
                                  ValidationHandler validationHandler,
                                  Model model,
                                  UserResource user) {

        Supplier<String> successView = () -> "redirect:/project/" + projectId + "/finance-check";

        return doSaveEligibility(projectId, organisationId, EligibilityState.REVIEW, eligibilityForm, form, validationHandler, successView, model, user);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @GetMapping("/changes")
    public String viewExternalEligibilityChanges(@PathVariable long projectId, @PathVariable final Long organisationId, Model model, UserResource loggedInUser) {
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        return doViewEligibilityChanges(project, organisation, model);
    }

    private String doSaveEligibility(long projectId, long organisationId, EligibilityState eligibility, FinanceChecksEligibilityForm eligibilityForm, YourProjectCostsForm form, ValidationHandler validationHandler, Supplier<String> successView, Model model, UserResource user) {

        Supplier<String> failureView = () -> doViewEligibility(projectId, organisationId, model, eligibilityForm, new ResetEligibilityForm(), form, null, false, user, false);

        EligibilityRagStatus statusToSend = getRagStatus(eligibilityForm);

        RestResult<Void> saveEligibilityResult = financeCheckRestService.saveEligibility(projectId, organisationId, eligibility, statusToSend);
        return validationHandler
                .addAnyErrors(saveEligibilityResult)
                .failNowOrSucceedWith(failureView, successView);

    }

    private EligibilityRagStatus getRagStatus(FinanceChecksEligibilityForm form) {
        if (form != null && form.isConfirmEligibilityChecked()) {
           return form.getEligibilityRagStatus();
        }
        return EligibilityRagStatus.UNSET;
    }

    private String doViewEligibilityChanges(ProjectResource project, OrganisationResource organisation, Model model) {
        ProjectFinanceChangesViewModel projectFinanceChangesViewModel = projectFinanceChangesViewModelPopulator
                .getProjectFinanceChangesViewModel(true, project, organisation);
        model.addAttribute("model", projectFinanceChangesViewModel);

        return "project/financecheck/eligibility-changes";
    }
}