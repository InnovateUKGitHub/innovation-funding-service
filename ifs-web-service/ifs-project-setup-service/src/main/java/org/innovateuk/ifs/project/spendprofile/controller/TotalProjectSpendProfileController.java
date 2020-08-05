package org.innovateuk.ifs.project.spendprofile.controller;

import org.innovateuk.ifs.commons.rest.LocalDateResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.spendprofile.SpendProfileSummaryModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.spendprofile.form.TotalSpendProfileForm;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileTableResource;
import org.innovateuk.ifs.project.spendprofile.SpendProfileTableCalculator;
import org.innovateuk.ifs.project.spendprofile.viewmodel.TotalProjectSpendProfileTableViewModel;
import org.innovateuk.ifs.project.spendprofile.viewmodel.TotalSpendProfileViewModel;
import org.innovateuk.ifs.spendprofile.SpendProfileService;
import org.innovateuk.ifs.util.PrioritySorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.LOAN;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * This controller will handle all requests that are related to the reviewing and sending of total project spend profiles.
 */
@Controller
@RequestMapping("/" + TotalProjectSpendProfileController.BASE_DIR + "/{projectId}/spend-profile/total")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = TotalProjectSpendProfileController .class)
@PreAuthorize("hasAnyAuthority('project_finance', 'applicant', 'comp_admin')")
public class TotalProjectSpendProfileController {

    public static final String BASE_DIR = "project";
    private static final String FORM_ATTR_NAME = "form";
    private static final String SPEND_PROFILE_TOTALS_TEMPLATE = BASE_DIR + "/spend-profile-totals";

    @Autowired
    private ProjectService projectService;
    @Autowired
    private SpendProfileService spendProfileService;
    @Autowired
    private SpendProfileTableCalculator spendProfileTableCalculator;
    @Autowired
    private CompetitionRestService competitionRestService;

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_TOTAL_SPEND_PROFILE_SECTION')")
    @GetMapping
    public String totals(Model model, @PathVariable("projectId") final Long projectId) {
        model.addAttribute("model", buildTotalViewModel(projectId));
        model.addAttribute(FORM_ATTR_NAME, new TotalSpendProfileForm());
        return SPEND_PROFILE_TOTALS_TEMPLATE;
    }

    @GetMapping("confirmation")
    public String confirmation(@PathVariable("projectId") final Long projectId) {
        return BASE_DIR + "/spend-profile-total-confirmation";
    }

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'SUBMIT_SPEND_PROFILE_SECTION')")
    @PostMapping
    public String sendForReview(@PathVariable("projectId") final Long projectId,
                                @ModelAttribute(FORM_ATTR_NAME) TotalSpendProfileForm form,
                                @SuppressWarnings("unused") BindingResult bindingResult,
                                ValidationHandler validationHandler,
                                Model model) {
        return validationHandler.performActionOrBindErrorsToField("",
                () -> {
                    model.addAttribute("model", buildTotalViewModel(projectId));
                    model.addAttribute(FORM_ATTR_NAME, form);
                    return SPEND_PROFILE_TOTALS_TEMPLATE;
                },
                () -> "redirect:/project/" + projectId,
                () -> spendProfileService.completeSpendProfilesReview(projectId));

    }

    private TotalSpendProfileViewModel buildTotalViewModel(final Long projectId) {
        ProjectResource projectResource = projectService.getById(projectId);
        TotalProjectSpendProfileTableViewModel tableView = buildTableViewModel(projectId);

        CompetitionResource competition = competitionRestService.getCompetitionById(projectResource.getCompetition()).getSuccess();
        SpendProfileSummaryModel summary = null;
        if (!competition.getFundingType().equals(LOAN)) {
            summary = spendProfileTableCalculator.createSpendProfileSummary(projectResource, tableView.getMonthlyCostsPerOrganisationMap(), tableView.getMonths());
        }
        return new TotalSpendProfileViewModel(projectResource, tableView, summary);
    }

    private TotalProjectSpendProfileTableViewModel buildTableViewModel(final Long projectId) {
        final OrganisationResource leadOrganisation = projectService.getLeadOrganisation(projectId);
        List<OrganisationResource> organisations = new PrioritySorting<>(projectService.getPartnerOrganisationsForProject(projectId),
                leadOrganisation, OrganisationResource::getName).unwrap();

        Map<Long, SpendProfileTableResource> organisationSpendProfiles = simpleToLinkedMap(organisations, OrganisationResource::getId,
                organisation -> spendProfileService.getSpendProfileTable(projectId, organisation.getId()));

        Map<Long, List<BigDecimal>> monthlyCostsPerOrganisationMap = simpleLinkedMapValue(organisationSpendProfiles, spendTableResource ->
                spendProfileTableCalculator.calculateMonthlyTotals(spendTableResource.getMonthlyCostsPerCategoryMap(), spendTableResource.getMonths().size()));

        Map<Long, BigDecimal> eligibleCostPerOrganisationMap = simpleLinkedMapValue(organisationSpendProfiles, tableResource ->
                spendProfileTableCalculator.calculateTotalOfAllEligibleTotals(tableResource.getEligibleCostPerCategoryMap()));

        List<LocalDateResource> months = organisationSpendProfiles.values().iterator().next().getMonths();
        Map<Long, BigDecimal> organisationToActualTotal = spendProfileTableCalculator.calculateRowTotal(monthlyCostsPerOrganisationMap);
        List<BigDecimal> totalForEachMonth = spendProfileTableCalculator.calculateMonthlyTotals(monthlyCostsPerOrganisationMap, months.size());
        BigDecimal totalOfAllActualTotals = spendProfileTableCalculator.calculateTotalOfAllActualTotals(monthlyCostsPerOrganisationMap);
        BigDecimal totalOfAllEligibleTotals = spendProfileTableCalculator.calculateTotalOfAllEligibleTotals(eligibleCostPerOrganisationMap);

        return new TotalProjectSpendProfileTableViewModel(months, monthlyCostsPerOrganisationMap, eligibleCostPerOrganisationMap,
                organisationToActualTotal, totalForEachMonth, totalOfAllActualTotals, totalOfAllEligibleTotals,
                simpleToMap(organisations, OrganisationResource::getId, OrganisationResource::getName), leadOrganisation);

    }
}