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
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.ProjectFinanceService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.financecheck.FinanceCheckService;
import org.innovateuk.ifs.financecheck.eligibility.form.FinanceChecksEligibilityForm;
import org.innovateuk.ifs.financecheck.eligibility.viewmodel.FinanceChecksEligibilityViewModel;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
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
import org.innovateuk.ifs.project.resource.ProjectResource;
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

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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

    @Autowired
    private FinanceCheckService financeCheckService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ProjectFinanceService projectFinanceService;

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

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @GetMapping
    @AsyncMethod
    public String viewEligibility(@PathVariable long projectId,
                                  @PathVariable long organisationId,
                                  @RequestParam(value = "financeType", required = false) FinanceRowType rowType,
                                  @RequestParam(value = "editAcademicFinances", required = false) boolean editAcademicFinances,
                                  Model model,
                                  UserResource user) {
        return doViewEligibility(projectId, organisationId, model, null, null, null, rowType, editAcademicFinances);
    }

    private String doViewEligibility(long projectId, long organisationId, Model model, FinanceChecksEligibilityForm eligibilityForm, YourProjectCostsForm form, AcademicCostForm academicCostForm, FinanceRowType rowType, boolean editAcademicFinances) {
        ProjectResource project = projectService.getById(projectId);
        Future<CompetitionResource> competition = async(() -> competitionRestService.getCompetitionById(project.getCompetition()).getSuccess());
        Future<OrganisationResource> organisation = async(() -> organisationRestService.getOrganisationById(organisationId).getSuccess());
        Future<OrganisationResource> leadOrganisation = async(() -> projectService.getLeadOrganisation(projectId));
        Future<EligibilityResource> eligibility = async(() -> projectFinanceService.getEligibility(projectId, organisationId));
        Future<FinanceCheckEligibilityResource> eligibilityOverview = async(() -> financeCheckService.getFinanceCheckEligibilityDetails(projectId, organisationId));

        try {
            Future<Model> future = CompletableFuture.completedFuture(model);
            if (eligibilityForm == null) {
                eligibilityForm = getEligibilityForm(eligibility.get());
            }
            boolean eligibilityApproved = eligibility.get().getEligibility() == EligibilityState.APPROVED;

            boolean isUsingJesFinances = competition.get().applicantShouldUseJesFinances(organisation.get().getOrganisationTypeEnum());
            if (!isUsingJesFinances) {
                model.addAttribute("model", new FinanceChecksProjectCostsViewModel(!eligibilityApproved, rowType, competition.get().getFinanceRowTypes()));
                if (form == null) {
                    future = async(() -> model.addAttribute("form", formPopulator.populateForm(projectId, organisationId)));
                }
            } else {
                if (academicCostForm == null) {
                    future = async(() -> model.addAttribute("academicCostForm", projectAcademicCostFormPopulator.populate(new AcademicCostForm(), projectId, organisationId)));
                }
            }

            List<ProjectFinanceResource> projectFinances = projectFinanceService.getProjectFinances(projectId);
            boolean isLeadPartnerOrganisation = leadOrganisation.get().getId().equals(organisationId);

            model.addAttribute("summaryModel", new FinanceChecksEligibilityViewModel(project, competition.get(), eligibilityOverview.get(),
                    organisation.get().getName(),
                    isLeadPartnerOrganisation,
                    organisation.get().getId(),
                    eligibilityApproved,
                    eligibility.get().getEligibilityRagStatus(),
                    eligibility.get().getEligibilityApprovalUserFirstName(),
                    eligibility.get().getEligibilityApprovalUserLastName(),
                    eligibility.get().getEligibilityApprovalDate(),
                    false,
                    isUsingJesFinances,
                    editAcademicFinances,
                    projectFinances
            ));

            model.addAttribute("eligibilityForm", eligibilityForm);
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
        Supplier<String> failureView = () -> doViewEligibility(projectId, organisationId, model, null, null, form, null, true);

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
                                           @RequestParam("save-eligibility") FinanceRowType type,
                                           @ModelAttribute("form") YourProjectCostsForm form,
                                           BindingResult bindingResult,
                                           ValidationHandler validationHandler,
                                           Model model,
                                           UserResource user) {

        Supplier<String> successView = () -> getRedirectUrlToEligibility(projectId, organisationId);
        Supplier<String> failureView = () -> doViewEligibility(projectId, organisationId, model, null, form, null, null, false);
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

        return doSaveEligibility(projectId, organisationId, EligibilityState.APPROVED, eligibilityForm, form,
                validationHandler, successView, model);
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

        return doSaveEligibility(projectId, organisationId, EligibilityState.REVIEW, eligibilityForm, form, validationHandler, successView, model);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_FINANCE_CHECKS_SECTION')")
    @GetMapping("/changes")
    public String viewExternalEligibilityChanges(@PathVariable long projectId, @PathVariable final Long organisationId, Model model, UserResource loggedInUser) {
        ProjectResource project = projectService.getById(projectId);
        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();
        return doViewEligibilityChanges(project, organisation, loggedInUser.getId(), model);
    }

    private String doSaveEligibility(long projectId, long organisationId, EligibilityState eligibility, FinanceChecksEligibilityForm eligibilityForm, YourProjectCostsForm form, ValidationHandler validationHandler, Supplier<String> successView, Model model) {

        Supplier<String> failureView = () -> doViewEligibility(projectId, organisationId, model, eligibilityForm, form, null, null, false);

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