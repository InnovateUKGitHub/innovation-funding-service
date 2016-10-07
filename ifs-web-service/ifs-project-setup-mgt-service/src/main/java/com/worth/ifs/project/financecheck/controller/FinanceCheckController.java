package com.worth.ifs.project.financecheck.controller;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.project.PartnerOrganisationService;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.finance.ProjectFinanceService;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.finance.resource.FinanceCheckSummaryResource;
import com.worth.ifs.project.finance.workflow.financechecks.resource.FinanceCheckProcessResource;
import com.worth.ifs.project.financecheck.FinanceCheckService;
import com.worth.ifs.project.financecheck.form.CostFormField;
import com.worth.ifs.project.financecheck.form.FinanceCheckForm;
import com.worth.ifs.project.financecheck.form.FinanceCheckSummaryForm;
import com.worth.ifs.project.financecheck.viewmodel.FinanceCheckViewModel;
import com.worth.ifs.project.financecheck.viewmodel.ProjectFinanceCheckSummaryViewModel;
import com.worth.ifs.project.resource.PartnerOrganisationResource;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.worth.ifs.application.resource.ApplicationResource.formatter;
import static com.worth.ifs.project.finance.resource.FinanceCheckState.APPROVED;
import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
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
    private FinanceCheckService financeCheckService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private PartnerOrganisationService partnerOrganisationService;

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
                         ValidationHandler validationHandler) {

        return validationHandler.failNowOrSucceedWith(
                () -> redirectToFinanceCheckForm(projectId, organisationId),
                () -> updateFinanceCheck(getFinanceCheckResource(projectId, organisationId), form, validationHandler));
    }

    @RequestMapping(method = GET)
    public String viewFinanceCheckSummary(@PathVariable Long projectId, Model model,
                                          @ModelAttribute FinanceCheckSummaryForm form,
                                          @SuppressWarnings("unused") BindingResult bindingResult,
                                          ValidationHandler validationHandler) {
        return doViewFinanceCheckSummary(projectId, model);
    }

    @RequestMapping(value = "/generate", method = POST)
    public String generateSpendProfile(@PathVariable Long projectId, Model model,
                                       @ModelAttribute FinanceCheckSummaryForm form,
                                       @SuppressWarnings("unused") BindingResult bindingResult,
                                       ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> doViewFinanceCheckSummary(projectId, model);
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
        ServiceResult<Void> approveResult = updateResult.andOnSuccess(() -> financeCheckService.approveFinanceCheck(projectId, organisationId));

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
        populateFinanceCheckModel(projectId, organisationId, model);
        return "project/financecheck/partner-project-eligibility";
    }

    private void populateFinanceCheckModel(Long projectId, Long organisationId, Model model){
        ProjectResource project = projectService.getById(projectId);
        ApplicationResource application = applicationService.getById(project.getApplication());
        String competitionName = application.getCompetitionName();
        String formattedCompId = formatter.format(application.getCompetition());

        OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);
        boolean isResearch = OrganisationTypeEnum.isResearch(organisationResource.getOrganisationType());
        Optional<ProjectUserResource> financeContact = getFinanceContact(projectId, organisationId);

        FinanceCheckProcessResource financeCheckStatus = financeCheckService.getFinanceCheckApprovalStatus(projectId, organisationId);
        boolean financeChecksApproved = APPROVED.equals(financeCheckStatus.getCurrentState());
        String approverName = financeCheckStatus.getInternalParticipant() != null ? financeCheckStatus.getInternalParticipant().getName() : null;
        LocalDate approvalDate = financeCheckStatus.getModifiedDate().toLocalDate();

        boolean isLeadPartner = isLeadPartner(projectId, organisationId);

        FinanceCheckViewModel financeCheckViewModel = new FinanceCheckViewModel(formattedCompId, competitionName, organisationResource.getName(), isLeadPartner, projectId, organisationId, isResearch, financeChecksApproved, approverName, approvalDate);

        if (financeContact.isPresent()) { // Internal users may still view finance contact page without finance contact being set.  They will see a message warning about this on template.
            financeCheckViewModel.setFinanceContactName(financeContact.get().getUserName());
            financeCheckViewModel.setFinanceContactEmail(financeContact.get().getEmail());
        }

        model.addAttribute("model", financeCheckViewModel);
    }

    private Optional<ProjectUserResource> getFinanceContact(Long projectId, Long organisationId){
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        return simpleFindFirst(projectUsers, pr -> pr.isFinanceContact() && organisationId.equals(pr.getOrganisation()));
    }

    private FinanceCheckResource getFinanceCheckResource(Long projectId, Long organisationId){
        ProjectOrganisationCompositeId key = new ProjectOrganisationCompositeId(projectId, organisationId);
        return financeCheckService.getByProjectAndOrganisation(key);
    }

    private String updateFinanceCheck(FinanceCheckResource currentFinanceCheckResource, FinanceCheckForm financeCheckForm, ValidationHandler validationHandler){

        Supplier<String> failureView = () -> redirectToFinanceCheckForm(currentFinanceCheckResource.getProject(), currentFinanceCheckResource.getOrganisation());
        Supplier<String> successView = () -> redirectToViewFinanceCheckSummary(currentFinanceCheckResource.getProject());

        ServiceResult<Void> updateResult = doUpdateFinanceCheck(currentFinanceCheckResource, financeCheckForm);
        return validationHandler.addAnyErrors(updateResult).failNowOrSucceedWith(failureView, successView);
    }

    private ServiceResult<Void> doUpdateFinanceCheck(FinanceCheckResource currentFinanceCheckResource, FinanceCheckForm financeCheckForm) {
        for (int i = 0; i < financeCheckForm.getCosts().size(); i++) {
            currentFinanceCheckResource.getCostGroup().getCosts().get(i).setValue(financeCheckForm.getCosts().get(i).getValue());
        }

        return financeCheckService.update(currentFinanceCheckResource);
    }

    private String doViewFinanceCheckSummary(Long projectId, Model model) {
        FinanceCheckSummaryResource financeCheckSummaryResource = financeCheckService.getFinanceCheckSummary(projectId).getSuccessObjectOrThrowException();
        ProjectFinanceCheckSummaryViewModel projectFinanceCheckSummaryViewModel = new ProjectFinanceCheckSummaryViewModel(financeCheckSummaryResource);
        model.addAttribute("model", projectFinanceCheckSummaryViewModel);
        return "project/financecheck/summary";
    }

    private String redirectToViewFinanceCheckSummary(Long projectId) {
        return "redirect:/project/" + projectId + "/finance-check";
    }

    private boolean isLeadPartner(Long projectId, Long organisationId) {
        ServiceResult<List<PartnerOrganisationResource>> result = partnerOrganisationService.getPartnerOrganisations(projectId);
        if(result.isSuccess()) {
            Optional<PartnerOrganisationResource> partnerOrganisationResource = simpleFindFirst(result.getSuccessObject(), PartnerOrganisationResource::isLeadOrganisation);
            return partnerOrganisationResource.isPresent() && partnerOrganisationResource.get().getOrganisation().equals(organisationId);
        } else {
            return false;
        }
    }
}