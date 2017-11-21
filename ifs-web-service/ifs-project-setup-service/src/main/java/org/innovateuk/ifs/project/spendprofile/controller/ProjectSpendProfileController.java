package org.innovateuk.ifs.project.spendprofile.controller;

import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.commons.validation.SpendProfileCostValidator;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.model.SpendProfileSummaryModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.spendprofile.form.SpendProfileForm;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileResource;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileTableResource;
import org.innovateuk.ifs.project.spendprofile.service.SpendProfileService;
import org.innovateuk.ifs.project.spendprofile.viewmodel.ProjectSpendProfileProjectSummaryViewModel;
import org.innovateuk.ifs.project.spendprofile.viewmodel.ProjectSpendProfileViewModel;
import org.innovateuk.ifs.project.status.StatusService;
import org.innovateuk.ifs.project.util.FinanceUtil;
import org.innovateuk.ifs.project.util.SpendProfileTableCalculator;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.PrioritySorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.SPEND_PROFILE_CANNOT_MARK_AS_COMPLETE_BECAUSE_SPEND_HIGHER_THAN_ELIGIBLE;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static org.innovateuk.ifs.user.resource.UserRoleType.PARTNER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * This controller will handle all requests that are related to spend profile.
 */
@Controller
@RequestMapping("/" + ProjectSpendProfileController.BASE_DIR + "/{projectId}/partner-organisation/{organisationId}/spend-profile")
public class ProjectSpendProfileController {

    static final String BASE_DIR = "project";
    private static final String REVIEW_TEMPLATE_NAME = "spend-profile-review";
    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private ProjectService projectService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private SpendProfileService spendProfileService;

    @Autowired
    private SpendProfileTableCalculator spendProfileTableCalculator;

    @Autowired
    @Qualifier("spendProfileCostValidator")
    private SpendProfileCostValidator spendProfileCostValidator;

    @Autowired
    private FinanceUtil financeUtil;

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_SPEND_PROFILE_SECTION')")
    @GetMapping
    public String viewSpendProfile(Model model,
                                   @P("projectId")@PathVariable("projectId") final Long projectId,
                                   @PathVariable("organisationId") final Long organisationId,
                                   UserResource loggedInUser) {

        if (isUserPartOfLeadOrganisation(projectId, loggedInUser)) {
            return viewProjectManagerSpendProfile(model, projectId, loggedInUser);
        }
        return reviewSpendProfilePage(model, projectId, organisationId, loggedInUser);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_SPEND_PROFILE_SECTION')")
    @GetMapping("/review")
    public String reviewSpendProfilePage(Model model,
                                         @P("projectId")@PathVariable("projectId") final Long projectId,
                                         @PathVariable("organisationId") final Long organisationId,
                                         UserResource loggedInUser) {

        model.addAttribute("model", buildSpendProfileViewModel(projectId, organisationId, loggedInUser));

        return BASE_DIR + "/spend-profile";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_SPEND_PROFILE_SECTION')")
    @GetMapping("/edit")
    public String editSpendProfile(Model model,
                                   HttpServletRequest request,
                                   @ModelAttribute(name = FORM_ATTR_NAME, binding = false) SpendProfileForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   ValidationHandler validationHandler,
                                   @P("projectId")@PathVariable("projectId") final Long projectId,
                                   @PathVariable("organisationId") final Long organisationId,
                                   UserResource loggedInUser) {

        String failureView = "redirect:/project/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile";

        ProjectResource projectResource = projectService.getById(projectId);
        SpendProfileTableResource spendProfileTableResource = spendProfileService.getSpendProfileTable(projectId, organisationId);
        form.setTable(spendProfileTableResource);

        if (!spendProfileTableResource.getMarkedAsComplete()) {
            model.addAttribute("model", buildSpendProfileViewModel(projectResource, organisationId, spendProfileTableResource, loggedInUser));
            return validationHandler.failNowOrSucceedWith(() -> BASE_DIR + "/spend-profile", () -> BASE_DIR + "/spend-profile");
        } else {
            ServiceResult<Void> result = markSpendProfileIncomplete(projectId, organisationId);
            return validationHandler.addAnyErrors(result).failNowOrSucceedWith(() -> failureView, () -> {
                model.addAttribute("model", buildSpendProfileViewModel(projectResource, organisationId, spendProfileTableResource, loggedInUser));
                return BASE_DIR + "/spend-profile";
            });
        }
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_SPEND_PROFILE_SECTION')")
    @PostMapping("/edit")
    public String saveSpendProfile(Model model,
                                   @ModelAttribute(FORM_ATTR_NAME) SpendProfileForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   ValidationHandler validationHandler,
                                   @P("projectId")@PathVariable("projectId") final Long projectId,
                                   @PathVariable("organisationId") final Long organisationId,
                                   UserResource loggedInUser) {

        Supplier<String> failureView = () -> {

            SpendProfileTableResource updatedTable = form.getTable();
            SpendProfileTableResource originalTableWithUpdatedCosts = spendProfileService.getSpendProfileTable(projectId, organisationId);
            originalTableWithUpdatedCosts.setMonthlyCostsPerCategoryMap(updatedTable.getMonthlyCostsPerCategoryMap());

            ProjectResource project = projectService.getById(projectId);

            return doEditSpendProfile(model, form, organisationId, loggedInUser, project, originalTableWithUpdatedCosts);
        };


        spendProfileCostValidator.validate(form.getTable(), bindingResult);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            SpendProfileTableResource spendProfileTableResource = spendProfileService.getSpendProfileTable(projectId, organisationId);
            spendProfileTableResource.setMonthlyCostsPerCategoryMap(form.getTable().getMonthlyCostsPerCategoryMap()); // update existing resource with user entered fields
            ServiceResult<Void> result = spendProfileService.saveSpendProfile(projectId, organisationId, spendProfileTableResource);
            return validationHandler.addAnyErrors(result).failNowOrSucceedWith(failureView,
                    () -> saveSpendProfileSuccessView(projectId, organisationId, loggedInUser.getId()));
        });
    }


    private String saveSpendProfileSuccessView(final Long projectId, final Long organisationId, final Long userId) {
        final String urlSuffix = projectService.isUserLeadPartner(projectId, userId) ? "/review" : "";
        return "redirect:/project/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile" + urlSuffix;

    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_SPEND_PROFILE_SECTION') && hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'MARK_SPEND_PROFILE_INCOMPLETE') && hasPermission(#organisationId, 'org.innovateuk.ifs.organisation.resource.OrganisationCompositeId', 'IS_NOT_FROM_OWN_ORGANISATION')")
    @PostMapping("/incomplete")
    public String markAsActionRequiredSpendProfile(Model model,
                                                   @ModelAttribute(FORM_ATTR_NAME) SpendProfileForm form,
                                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                                   ValidationHandler validationHandler,
                                                   @P("projectId")@PathVariable("projectId") final Long projectId,
                                                   @P("organisationId")@PathVariable("organisationId") final Long organisationId,
                                                   UserResource loggedInUser) {

        Supplier<String> failureView = () -> reviewSpendProfilePage(model, projectId, organisationId, loggedInUser);
        String successView = "redirect:/project/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile";

        ServiceResult<Void> result = markSpendProfileIncomplete(projectId, organisationId);

        return validationHandler.addAnyErrors(result).failNowOrSucceedWith(failureView, () -> successView);
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_SPEND_PROFILE_SECTION')")
    @PostMapping("/complete")
    public String markAsCompleteSpendProfile(Model model,
                                             @P("projectId")@PathVariable("projectId") final Long projectId,
                                             @PathVariable("organisationId") final Long organisationId,
                                             UserResource loggedInUser) {
        return markSpendProfileComplete(model, projectId, organisationId, "redirect:/project/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile", loggedInUser);
    }


    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_SPEND_PROFILE_SECTION')")
    @GetMapping("/confirm")
    public String viewConfirmSpendProfilePage(@P("projectId")@PathVariable("projectId") final Long projectId,
                                              @PathVariable("organisationId") final Long organisationId,
                                              Model model,
                                              UserResource loggedInUser) {
        ProjectSpendProfileViewModel viewModel = buildSpendProfileViewModel(projectId, organisationId, loggedInUser);
        model.addAttribute("model", viewModel);
        return "project/spend-profile-confirm";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_SPEND_PROFILE_SECTION') && hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'MARK_SPEND_PROFILE_INCOMPLETE') && hasPermission(#organisationId, 'org.innovateuk.ifs.organisation.resource.OrganisationCompositeId', 'IS_NOT_FROM_OWN_ORGANISATION')")
    @GetMapping("/incomplete")
    public String viewConfirmEditSpendProfilePage(@P("projectId")@PathVariable("projectId") final Long projectId,
                                                  @PathVariable("organisationId") final Long organisationId,
                                                  Model model,
                                                  UserResource loggedInUser) {
        ProjectSpendProfileViewModel viewModel = buildSpendProfileViewModel(projectId, organisationId, loggedInUser);
        model.addAttribute("model", viewModel);
        return "project/spend-profile-confirm-edits";
    }

    private String doEditSpendProfile(Model model,
                                      SpendProfileForm form,
                                      final Long organisationId,
                                      final UserResource loggedInUser,
                                      ProjectResource project,
                                      SpendProfileTableResource spendProfileTableResource) {

        spendProfileTableResource.getMonthlyCostsPerCategoryMap().keySet().forEach(key -> {
            List<BigDecimal> monthlyCostNullsReplacedWithZeros = new ArrayList<>();
            boolean monthlyCostNullsReplaced = false;
            for (BigDecimal mon : spendProfileTableResource.getMonthlyCostsPerCategoryMap().get(key)) {
                if (null == mon) {
                    monthlyCostNullsReplaced = true;
                    monthlyCostNullsReplacedWithZeros.add(BigDecimal.ZERO);
                } else {
                    monthlyCostNullsReplacedWithZeros.add(mon);
                }
            }
            if (monthlyCostNullsReplaced) {
                Map<Long, List<BigDecimal>> updatedCostPerCategoryMap = spendProfileTableResource.getMonthlyCostsPerCategoryMap();
                updatedCostPerCategoryMap.replace(key, monthlyCostNullsReplacedWithZeros);
                spendProfileTableResource.setMonthlyCostsPerCategoryMap(updatedCostPerCategoryMap);
            }
        });

        form.setTable(spendProfileTableResource);

        model.addAttribute("model", buildSpendProfileViewModel(project, organisationId, spendProfileTableResource, loggedInUser));

        return BASE_DIR + "/spend-profile";
    }

    private String viewProjectManagerSpendProfile(Model model, final Long projectId, final UserResource loggedInUser) {
        model.addAttribute("model", populateSpendProfileProjectManagerViewModel(projectId, loggedInUser));
        return BASE_DIR + "/" + REVIEW_TEMPLATE_NAME;
    }

    private String markSpendProfileComplete(Model model,
                                            final Long projectId,
                                            final Long organisationId,
                                            final String successView,
                                            final UserResource loggedInUser) {
        ServiceResult<Void> result = spendProfileService.markSpendProfileComplete(projectId, organisationId);
        if (result.isFailure()) {
            ProjectSpendProfileViewModel spendProfileViewModel = buildSpendProfileViewModel(projectId, organisationId, loggedInUser);
            spendProfileViewModel.setObjectErrors(Collections.singletonList(new ObjectError(SPEND_PROFILE_CANNOT_MARK_AS_COMPLETE_BECAUSE_SPEND_HIGHER_THAN_ELIGIBLE.getErrorKey(), "Cannot mark as complete, because totals more than eligible")));
            model.addAttribute("model", spendProfileViewModel);
            return BASE_DIR + "/spend-profile";
        } else {
            return successView;
        }
    }

    private ServiceResult<Void> markSpendProfileIncomplete(final Long projectId,
                                                           final Long organisationId) {

        return spendProfileService.markSpendProfileIncomplete(projectId, organisationId);
    }

    private ProjectSpendProfileViewModel buildSpendProfileViewModel(final ProjectResource projectResource,
                                                                    final Long organisationId,
                                                                    final SpendProfileTableResource spendProfileTableResource,
                                                                    final UserResource loggedInUser) {
        SpendProfileSummaryModel summary = spendProfileTableCalculator.createSpendProfileSummary(projectResource, spendProfileTableResource.getMonthlyCostsPerCategoryMap(), spendProfileTableResource.getMonths());

        OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);

        boolean isUsingJesFinances = financeUtil.isUsingJesFinances(organisationResource.getOrganisationType());
        Map<Long, BigDecimal> categoryToActualTotal = spendProfileTableCalculator.calculateRowTotal(spendProfileTableResource.getMonthlyCostsPerCategoryMap());
        List<BigDecimal> totalForEachMonth = spendProfileTableCalculator.calculateMonthlyTotals(spendProfileTableResource.getMonthlyCostsPerCategoryMap(), spendProfileTableResource.getMonths().size());

        BigDecimal totalOfAllActualTotals = spendProfileTableCalculator.calculateTotalOfAllActualTotals(spendProfileTableResource.getMonthlyCostsPerCategoryMap());
        BigDecimal totalOfAllEligibleTotals = spendProfileTableCalculator.calculateTotalOfAllEligibleTotals(spendProfileTableResource.getEligibleCostPerCategoryMap());

        boolean isUserPartOfThisOrganisation = isUserPartOfThisOrganisation(projectResource.getId(), organisationId, loggedInUser);

        boolean leadPartner = isUserPartOfLeadOrganisation(projectResource.getId(), loggedInUser);

        return new ProjectSpendProfileViewModel(projectResource, organisationResource, spendProfileTableResource, summary,
                spendProfileTableResource.getMarkedAsComplete(), categoryToActualTotal, totalForEachMonth,
                totalOfAllActualTotals, totalOfAllEligibleTotals, projectResource.getSpendProfileSubmittedDate() != null, spendProfileTableResource.getCostCategoryGroupMap(),
                spendProfileTableResource.getCostCategoryResourceMap(), isUsingJesFinances, isUserPartOfThisOrganisation,
                projectService.isProjectManager(loggedInUser.getId(), projectResource.getId()),
                isApproved(projectResource.getId()), leadPartner);
    }

    private ProjectSpendProfileViewModel buildSpendProfileViewModel(final Long projectId, final Long organisationId, final UserResource loggedInUser) {
        ProjectResource projectResource = projectService.getById(projectId);
        SpendProfileTableResource spendProfileTableResource = spendProfileService.getSpendProfileTable(projectId, organisationId);
        return buildSpendProfileViewModel(projectResource, organisationId, spendProfileTableResource, loggedInUser);
    }

    private ProjectSpendProfileProjectSummaryViewModel populateSpendProfileProjectManagerViewModel(final Long projectId,
                                                                                                   final UserResource loggedInUser) {
        ProjectResource projectResource = projectService.getById(projectId);

        final OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);

        List<OrganisationResource> organisations = new PrioritySorting<>(projectService.getPartnerOrganisationsForProject(projectId),
                leadOrganisation, OrganisationResource::getName).unwrap();


        Map<Long, OrganisationReviewDetails> editablePartners = getOrganisationReviewDetails(projectId, organisations, loggedInUser);

        return new ProjectSpendProfileProjectSummaryViewModel(projectId,
                projectResource.getApplication(), projectResource.getName(),
                organisations,
                leadOrganisation,
                projectResource.getSpendProfileSubmittedDate() != null,
                editablePartners,
                isApproved(projectId));
    }

    private boolean isApproved(final Long projectId) {
        ProjectTeamStatusResource teamStatus = statusService.getProjectTeamStatus(projectId, Optional.empty());
        return COMPLETE.equals(teamStatus.getLeadPartnerStatus().getSpendProfileStatus());
    }

    private Map<Long, OrganisationReviewDetails> getOrganisationReviewDetails(final Long projectId, List<OrganisationResource> partnerOrganisations, final UserResource loggedInUser) {
        return partnerOrganisations.stream().collect(Collectors.toMap(OrganisationResource::getId,
                o -> new OrganisationReviewDetails(o.getName(), spendProfileService.getSpendProfile(projectId, o.getId()).map(SpendProfileResource::isMarkedAsComplete).orElse(false), isUserPartOfThisOrganisation(projectId, o.getId(), loggedInUser), true),
                (v1, v2) -> v1, LinkedHashMap::new));
    }

    private boolean isUserPartOfThisOrganisation(final Long projectId, final Long organisationId, final UserResource loggedInUser) {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        Optional<ProjectUserResource> returnedProjectUser = simpleFindFirst(projectUsers, projectUserResource -> projectUserResource.getUser().equals(loggedInUser.getId())
                && projectUserResource.getOrganisation().equals(organisationId)
                && PARTNER.getName().equals(projectUserResource.getRoleName())
        );

        return returnedProjectUser.isPresent();
    }

    private boolean isUserPartOfLeadOrganisation(final Long projectId, final UserResource loggedInUser) {
        return projectService.getLeadPartners(projectId).stream().anyMatch(pu -> pu.isUser(loggedInUser.getId()));
    }
}