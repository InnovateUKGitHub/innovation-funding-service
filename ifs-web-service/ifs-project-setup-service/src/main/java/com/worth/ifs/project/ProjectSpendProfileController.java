package com.worth.ifs.project;

import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.commons.rest.LocalDateResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.project.finance.ProjectFinanceService;
import com.worth.ifs.project.form.SpendProfileForm;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import com.worth.ifs.project.util.DateUtil;
import com.worth.ifs.project.util.FinancialYearDate;
import com.worth.ifs.project.util.SpendProfileTableCalculator;
import com.worth.ifs.project.validation.SpendProfileCostValidator;
import com.worth.ifs.project.viewmodel.ProjectSpendProfileProjectManagerViewModel;
import com.worth.ifs.project.viewmodel.ProjectSpendProfileViewModel;
import com.worth.ifs.project.viewmodel.SpendProfileSummaryModel;
import com.worth.ifs.project.viewmodel.SpendProfileSummaryYearModel;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.util.CollectionFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.worth.ifs.commons.error.CommonFailureKeys.SPEND_PROFILE_CANNOT_MARK_AS_COMPLETE_BECAUSE_SPEND_HIGHER_THAN_ELIGIBLE;
import static com.worth.ifs.user.resource.UserRoleType.PROJECT_MANAGER;
import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;
import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This controller will handle all requests that are related to spend profile.
 */
@Controller
@RequestMapping("/" + ProjectSpendProfileController.BASE_DIR + "/{projectId}/partner-organisation/{organisationId}/spend-profile")
public class ProjectSpendProfileController {

    private static final String FORM_ATTR_NAME = "form";
    public static final String BASE_DIR = "project";
    public static final String REVIEW_TEMPLATE_NAME = "spend-profile-review";

    @Autowired
    private ProjectService projectService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @Autowired
    private SpendProfileTableCalculator spendProfileTableCalculator;

    @Autowired
    @Qualifier("spendProfileCostValidator")
    private SpendProfileCostValidator spendProfileCostValidator;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_SPEND_PROFILE_SECTION')")
    @RequestMapping(method = GET)
    public String viewSpendProfile(Model model,
                                   @PathVariable("projectId") final Long projectId,
                                   @PathVariable("organisationId") final Long organisationId,
                                   @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        if (userHasProjectManagerRole(loggedInUser, projectId)) {
            return viewProjectManagerSpendProfile(model, projectId);
        }
        return reviewSpendProfilePage(model, projectId, organisationId, loggedInUser);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_SPEND_PROFILE_SECTION')")
    @RequestMapping(value = "/review", method = GET)
    public String reviewSpendProfilePage(Model model,
                                                 @PathVariable("projectId") final Long projectId,
                                                 @PathVariable("organisationId") final Long organisationId,
                                                 @ModelAttribute("loggedInUser") UserResource loggedInUser) {

            model.addAttribute("model", buildSpendProfileViewModel(projectId, organisationId));
        return BASE_DIR + "/spend-profile";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_SPEND_PROFILE_SECTION')")
    @RequestMapping(value = "/edit", method = GET)
    public String editSpendProfile(Model model,
                                   HttpServletRequest request,
                                   @ModelAttribute(FORM_ATTR_NAME) SpendProfileForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   @PathVariable("projectId") final Long projectId,
                                   @PathVariable("organisationId") final Long organisationId,
                                   @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        ProjectResource projectResource = projectService.getById(projectId);
        SpendProfileTableResource spendProfileTableResource = projectFinanceService.getSpendProfileTable(projectId, organisationId);
        form.setTable(spendProfileTableResource);

        if(spendProfileTableResource.getMarkedAsComplete()) {
            markSpendProfileInComplete(model, projectId, organisationId, "redirect:/" + BASE_DIR + "/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile");
        }
        model.addAttribute("model", buildSpendProfileViewModel(projectResource, organisationId, spendProfileTableResource));

        return BASE_DIR + "/spend-profile";
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_SPEND_PROFILE_SECTION')")
    @RequestMapping(value = "/edit", method = POST)
    public String saveSpendProfile(@ModelAttribute(FORM_ATTR_NAME) SpendProfileForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   ValidationHandler validationHandler,
                                   @PathVariable("projectId") final Long projectId,
                                   @PathVariable("organisationId") final Long organisationId,
                                   @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        String failureView = "redirect:/project/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile/edit";
        String successView = "redirect:/project/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile";

        ValidationHandler customValidationHandler = ValidationHandler.newBindingResultHandler(bindingResult);
        spendProfileCostValidator.validate(form.getTable(), bindingResult);
        if (customValidationHandler.hasErrors()) {
            return failureView;
        }

        SpendProfileTableResource spendProfileTableResource = projectFinanceService.getSpendProfileTable(projectId, organisationId);
        spendProfileTableResource.setMonthlyCostsPerCategoryMap(form.getTable().getMonthlyCostsPerCategoryMap()); // update existing resource with user entered fields

        ServiceResult<Void> result = projectFinanceService.saveSpendProfile(projectId, organisationId, spendProfileTableResource);

        return validationHandler.addAnyErrors(result).failNowOrSucceedWith(() -> failureView, () -> successView);
    }

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_SPEND_PROFILE_SECTION')")
    @RequestMapping(value = "/complete", method = POST)
    public String markAsCompleteSpendProfile(Model model,
                                             @PathVariable("projectId") final Long projectId,
                                             @PathVariable("organisationId") final Long organisationId,
                                             @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        return markSpendProfileComplete(model, projectId, organisationId, "redirect:/project/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile");
    }

    private String viewProjectManagerSpendProfile(Model model, Long projectId) {
        model.addAttribute("model", populateSpendProfileProjectManagerViewModel(projectId));
        return BASE_DIR + "/" + REVIEW_TEMPLATE_NAME;
    }

    private Map<String, Boolean> getPartnersSpendProfileProgress(Long projectId, List<OrganisationResource> partnerOrganisations) {
        HashMap<String, Boolean> partnerProgressMap = new HashMap<>();
        partnerOrganisations.stream().forEach(organisation -> {
            Optional<SpendProfileResource> spendProfile = projectFinanceService.getSpendProfile(projectId, organisation.getId());
            partnerProgressMap.put(organisation.getName(),spendProfile.get().isMarkedAsComplete());
        });
        return partnerProgressMap;
    }

    private String markSpendProfileComplete(Model model,
                                            Long projectId,
                                            Long organisationId,
                                            String successView) {
        return markSpendProfile(model, projectId, organisationId, true, successView);
    }

    private String markSpendProfileInComplete(Model model,
                                              Long projectId,
                                              Long organisationId,
                                              String successView) {
        return markSpendProfile(model, projectId, organisationId, false, successView);
    }

    private String markSpendProfile(Model model,
                                    Long projectId,
                                    Long organisationId,
                                    Boolean complete,
                                    String successView) {
        ServiceResult<Void> result = projectFinanceService.markSpendProfile(projectId, organisationId, complete);
        if(result.isFailure()){
            ProjectSpendProfileViewModel spendProfileViewModel = buildSpendProfileViewModel(projectId, organisationId);
            spendProfileViewModel.setObjectErrors(Collections.singletonList(new ObjectError(SPEND_PROFILE_CANNOT_MARK_AS_COMPLETE_BECAUSE_SPEND_HIGHER_THAN_ELIGIBLE.getErrorKey(), "Cannot mark as complete, because totals more than eligible")));
            model.addAttribute("model", spendProfileViewModel);
            return BASE_DIR + "/spend-profile";
        } else {
            return successView;
        }
    }

    private ProjectSpendProfileViewModel buildSpendProfileViewModel(final ProjectResource projectResource, final Long organisationId, final SpendProfileTableResource spendProfileTableResource) {
        SpendProfileSummaryModel summary = spendProfileTableCalculator.createSpendProfileSummary(projectResource, spendProfileTableResource.getMonthlyCostsPerCategoryMap(), spendProfileTableResource.getMonths());

        OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);

        Map<String, BigDecimal> categoryToActualTotal = spendProfileTableCalculator.calculateRowTotal(spendProfileTableResource.getMonthlyCostsPerCategoryMap());
        List<BigDecimal> totalForEachMonth = spendProfileTableCalculator.calculateMonthlyTotals(spendProfileTableResource.getMonthlyCostsPerCategoryMap(), spendProfileTableResource.getMonths().size());

        BigDecimal totalOfAllActualTotals = spendProfileTableCalculator.calculateTotalOfAllActualTotals(spendProfileTableResource.getMonthlyCostsPerCategoryMap());
        BigDecimal totalOfAllEligibleTotals = spendProfileTableCalculator.calculateTotalOfAllEligibleTotals(spendProfileTableResource.getEligibleCostPerCategoryMap());

        return new ProjectSpendProfileViewModel(projectResource, organisationResource, spendProfileTableResource, summary,
                spendProfileTableResource.getMarkedAsComplete(), categoryToActualTotal, totalForEachMonth,
                totalOfAllActualTotals, totalOfAllEligibleTotals, projectResource.getSpendProfileSubmittedDate() != null);
    }

    private ProjectSpendProfileViewModel buildSpendProfileViewModel(Long projectId, Long organisationId) {
        ProjectResource projectResource = projectService.getById(projectId);
        SpendProfileTableResource spendProfileTableResource = projectFinanceService.getSpendProfileTable(projectId, organisationId);
        return buildSpendProfileViewModel(projectResource, organisationId, spendProfileTableResource);
    }

    private ProjectSpendProfileProjectManagerViewModel populateSpendProfileProjectManagerViewModel(Long projectId) {
        ProjectResource projectResource = projectService.getById(projectId);

        List<OrganisationResource> partnerOrganisations = projectService.getPartnerOrganisationsForProject(projectId);

        Map<String, Boolean> partnersSpendProfileProgress = getPartnersSpendProfileProgress(projectId, partnerOrganisations);

        return new ProjectSpendProfileProjectManagerViewModel(projectId,
                projectResource.getName(),
                partnersSpendProfileProgress,
                partnerOrganisations,
                projectResource.getSpendProfileSubmittedDate() != null);
    }

    private boolean userHasProjectManagerRole(UserResource user, Long projectId) {
        Optional<ProjectUserResource> existingProjectManager = getProjectManager(projectId);
        return existingProjectManager.isPresent() && existingProjectManager.get().getUser().equals(user.getId());
    }

    private Optional<ProjectUserResource> getProjectManager(Long projectId) {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        return simpleFindFirst(projectUsers, pu -> PROJECT_MANAGER.getName().equals(pu.getRoleName()));
    }
}
