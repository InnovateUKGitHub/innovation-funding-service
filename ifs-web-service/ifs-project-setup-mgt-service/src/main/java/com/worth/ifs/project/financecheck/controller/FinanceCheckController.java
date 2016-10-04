package com.worth.ifs.project.financecheck.controller;

import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.ApplicationSummaryService;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.finance.ProjectFinanceService;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.finance.workflow.financechecks.resource.FinanceCheckProcessResource;
import com.worth.ifs.project.financecheck.FinanceCheckService;
import com.worth.ifs.project.financecheck.form.CostFormField;
import com.worth.ifs.project.financecheck.form.FinanceCheckForm;
import com.worth.ifs.project.financecheck.form.FinanceCheckSummaryForm;
import com.worth.ifs.project.financecheck.viewmodel.FinanceCheckViewModel;
import com.worth.ifs.project.financecheck.viewmodel.ProjectFinanceCheckSummaryViewModel;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.OrganisationTypeEnum;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.*;
import static java.math.RoundingMode.HALF_EVEN;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This controller is for allowing internal users to view and update application finances entered by applicants
 */
@Controller
@RequestMapping("/project/{projectId}/finance-check")
public class FinanceCheckController {
    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationSummaryService applicationSummaryService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private FinanceCheckService financeCheckService;

    @Autowired
    OrganisationService organisationService;

    @RequestMapping(value = "/organisation/{organisationId}", method = GET)
    public String view(@PathVariable("projectId") final Long projectId, @PathVariable("organisationId") Long organisationId,
                       @ModelAttribute(FORM_ATTR_NAME) FinanceCheckForm form,
                       @ModelAttribute("loggedInUser") UserResource loggedInUser,
                       Model model){
        FinanceCheckResource financeCheckResource = getFinanceCheckResource(projectId, organisationId);
        populateExitingFinanceCheckDetailsInForm(financeCheckResource, form);
        return doViewFinanceCheckForm(projectId, organisationId, model);
    }

    @RequestMapping(value = "/organisation/{organisationId}", method = POST)
    public String update(@PathVariable("projectId") Long projectId,
                         @PathVariable("organisationId") Long organisationId,
                         @ModelAttribute(FORM_ATTR_NAME) @Valid FinanceCheckForm form,
                         @SuppressWarnings("unused") BindingResult bindingResult,
                         ValidationHandler validationHandler,
                         Model model){
        return validationHandler.failNowOrSucceedWith(
                () -> redirectToFinanceCheckForm(projectId, organisationId),
                () -> updateFinanceCheck(projectId, organisationId, getFinanceCheckResource(projectId, organisationId), form));
    }

    @RequestMapping(method = GET)
    public String viewFinanceCheckSummary(@PathVariable Long projectId, Model model) {
        return doViewFinanceCheckSummary(projectId, model, new FinanceCheckSummaryForm());
    }

    @RequestMapping(value = "/generate", method = POST)
    public String generateSpendProfile(@PathVariable Long projectId, Model model,
                                       @ModelAttribute FinanceCheckSummaryForm form,
                                       @SuppressWarnings("unused") BindingResult bindingResult,
                                       ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> doViewFinanceCheckSummary(projectId, model, form);
        ServiceResult<Void> generateResult = projectFinanceService.generateSpendProfile(projectId);

        return validationHandler.addAnyErrors(generateResult).failNowOrSucceedWith(failureView, () ->
                redirectToViewFinanceCheckSummary(projectId)
        );
    }

    @RequestMapping(value = "/organisation/{organisationId}", params = "approve", method = POST)
    public String approveFinanceCheck(@PathVariable Long projectId, @PathVariable Long organisationId, Model model,
                                       @ModelAttribute FinanceCheckForm form,
                                       @SuppressWarnings("unused") BindingResult bindingResult,
                                       ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> doViewFinanceCheckForm(projectId, organisationId, model);

        ServiceResult<Void> updateResult = doUpdateFinanceCheck(getFinanceCheckResource(projectId, organisationId), form);
        ServiceResult<Void> approveResult = updateResult.andOnSuccess(() -> projectFinanceService.approveFinanceCheck(projectId, organisationId));

        return validationHandler.addAnyErrors(approveResult).failNowOrSucceedWith(failureView, () ->
                redirectToFinanceCheckForm(projectId, organisationId)
        );
    }


    private String redirectToFinanceCheckForm(Long projectId, Long organisationId){
        return "redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId;
    }

    private void populateExitingFinanceCheckDetailsInForm(FinanceCheckResource financeCheckResource, FinanceCheckForm form){
        form.setCosts(simpleMap(financeCheckResource.getCostGroup().getCosts(), c -> {
            CostFormField cf = new CostFormField();
            cf.setId(c.getId());
            cf.setValue(c.getValue());
            return cf;
        }));
    }

    private String doViewFinanceCheckForm(Long projectId, Long organisationId, Model model){

        OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);
        boolean isResearch = OrganisationTypeEnum.isResearch(organisationResource.getOrganisationType());
        Optional<ProjectUserResource> financeContact = getFinanceContact(projectId, organisationId);

        FinanceCheckProcessResource financeCheckStatus = projectFinanceService.getFinanceCheckApprovalStatus(projectId, organisationId);
        String approverName = financeCheckStatus.getInternalParticipant() != null ? financeCheckStatus.getInternalParticipant().getName() : null;
        LocalDate approvalDate = financeCheckStatus.getModifiedDate().toLocalDate();

        FinanceCheckViewModel financeCheckViewModel;

        if (financeContact.isPresent()) {
            financeCheckViewModel = new FinanceCheckViewModel(projectId, organisationId, financeContact.get().getUserName(), financeContact.get().getEmail(), isResearch,
                    financeCheckStatus.isCanApprove(), approverName, approvalDate);
        } else {
            financeCheckViewModel = new FinanceCheckViewModel(projectId, organisationId, isResearch, financeCheckStatus.isCanApprove(), approverName, approvalDate);
        }

        model.addAttribute("model", financeCheckViewModel);
        return "project/financecheck/partner-project-eligibility";
    }

    private Optional<ProjectUserResource> getFinanceContact(Long projectId, Long organisationId){
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        return simpleFindFirst(projectUsers, pr -> pr.isFinanceContact() && organisationId.equals(pr.getOrganisation()));
    }

    private FinanceCheckResource getFinanceCheckResource(Long projectId, Long organisationId){
        ProjectOrganisationCompositeId key = new ProjectOrganisationCompositeId(projectId, organisationId);
        return financeCheckService.getByProjectAndOrganisation(key);
    }

    private String updateFinanceCheck(Long projectId, Long organisationId, FinanceCheckResource currentFinanceCheckResource, FinanceCheckForm financeCheckForm){
        // TODO map by name once form is dynamic
        doUpdateFinanceCheck(currentFinanceCheckResource, financeCheckForm);

        return redirectToViewFinanceCheckSummary(projectId);
    }

    private ServiceResult<Void> doUpdateFinanceCheck(FinanceCheckResource currentFinanceCheckResource, FinanceCheckForm financeCheckForm) {
        for(int i = 0; i < financeCheckForm.getCosts().size(); i++){
            currentFinanceCheckResource.getCostGroup().getCosts().get(i).setValue(financeCheckForm.getCosts().get(i).getValue());
        }

        // TODO - deal with update failures if they occur
        financeCheckService.update(currentFinanceCheckResource);
        return serviceSuccess();
    }

    private String doViewFinanceCheckSummary(Long projectId, Model model, FinanceCheckSummaryForm form) {

        ProjectFinanceCheckSummaryViewModel viewModel = populateFinanceCheckSummaryViewModel(projectId);

        model.addAttribute("model", viewModel);
        model.addAttribute("form", form);

        return "project/financecheck/summary";
    }

    // TODO DW - a lot of this information will not be available in reality until the Finance Checks story is available,
    // so supporting the page with dummy data until then in order to unblock development on other Spend Profile stories
    private ProjectFinanceCheckSummaryViewModel populateFinanceCheckSummaryViewModel(Long projectId) {

        ProjectResource project = projectService.getById(projectId);
        ApplicationResource application = applicationService.getById(project.getApplication());
        CompetitionSummaryResource competitionSummary = applicationSummaryService.getCompetitionSummaryByCompetitionId(application.getCompetition());
        List<OrganisationResource> partnerOrganisations = projectService.getPartnerOrganisationsForProject(projectId);

        Optional<SpendProfileResource> anySpendProfile = projectFinanceService.getSpendProfile(projectId, partnerOrganisations.get(0).getId());

        List<ApplicationFinanceResource> applicationFinanceResourceList = financeService.getApplicationFinanceTotals(application.getId());

        List<ProjectFinanceCheckSummaryViewModel.FinanceCheckOrganisationRow> organisationRows = mapWithIndex(partnerOrganisations, (i, org) ->

                new ProjectFinanceCheckSummaryViewModel.FinanceCheckOrganisationRow(
                        org.getId(), org.getName(),
                        getEnumForIndex(ProjectFinanceCheckSummaryViewModel.Viability.class, i),
                        getEnumForIndex(ProjectFinanceCheckSummaryViewModel.RagStatus.class, i),
                        getEnumForIndex(ProjectFinanceCheckSummaryViewModel.Eligibility.class, i),
                        getEnumForIndex(ProjectFinanceCheckSummaryViewModel.RagStatus.class, i + 1),
                        getEnumForIndex(ProjectFinanceCheckSummaryViewModel.QueriesRaised.class, i))
        );

        BigDecimal projectTotal = calculateTotalForAllOrganisations(applicationFinanceResourceList,
                applicationFinanceResource -> applicationFinanceResource.getTotal());
        BigDecimal totalFundingSought =  calculateTotalForAllOrganisations(applicationFinanceResourceList,
                applicationFinanceResource -> applicationFinanceResource.getTotalFundingSought());

        return new ProjectFinanceCheckSummaryViewModel(
                projectId, competitionSummary, organisationRows,
                project.getTargetStartDate(), project.getDurationInMonths().intValue(),
                projectTotal,
                totalFundingSought,
                calculateTotalForAllOrganisations(applicationFinanceResourceList,
                        applicationFinanceResource -> applicationFinanceResource.getTotalOtherFunding()),
                calculateGrantPercentage(projectTotal, totalFundingSought),
                anySpendProfile.isPresent());
    }

    private BigDecimal calculateTotalForAllOrganisations(List<ApplicationFinanceResource> applicationFinanceResourceList,
                                                         Function<ApplicationFinanceResource, BigDecimal> keyExtractor) {

        return applicationFinanceResourceList.stream().map(keyExtractor).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateGrantPercentage(BigDecimal projectTotal, BigDecimal totalFundingSought) {

        if (projectTotal.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }

        return totalFundingSought.multiply(BigDecimal.valueOf(100)).divide(projectTotal, 0, HALF_EVEN);
    }

    private <T extends Enum> T getEnumForIndex(Class<T> enums, int index) {
        T[] enumConstants = enums.getEnumConstants();
        return enumConstants[index % enumConstants.length];
    }

    private String redirectToViewFinanceCheckSummary(Long projectId) {
        return "redirect:/project/" + projectId + "/finance-check";
    }
}