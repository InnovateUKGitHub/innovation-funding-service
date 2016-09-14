package com.worth.ifs.finance.spendprofile.summary.controller;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.service.ApplicationFinanceService;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.ApplicationSummaryService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.spendprofile.summary.form.ProjectSpendProfileForm;
import com.worth.ifs.finance.spendprofile.summary.viewmodel.ProjectSpendProfileSummaryViewModel;
import com.worth.ifs.project.ProjectService;
import com.worth.ifs.project.finance.ProjectFinanceService;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.user.resource.OrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.worth.ifs.util.CollectionFunctions.mapWithIndex;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This Controller handles Spend Profile activity for the Internal Finance Team users
 */
@Controller
@RequestMapping("/project/{projectId}/spend-profile")
public class ProjectSpendProfileSummaryController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationSummaryService applicationSummaryService;

    @Autowired
    private ApplicationFinanceService applicationFinanceService;

    @RequestMapping(value = "/summary", method = GET)
    public String viewSpendProfileSummary(@PathVariable Long projectId, Model model) {
        return doViewSpendProfileSummary(projectId, model, new ProjectSpendProfileForm());
    }

    @RequestMapping(value = "/generate", method = POST)
    public String generateSpendProfile(@PathVariable Long projectId, Model model,
                                       @ModelAttribute ProjectSpendProfileForm form,
                                       @SuppressWarnings("unused") BindingResult bindingResult,
                                       ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> doViewSpendProfileSummary(projectId, model, form);
        ServiceResult<Void> generateResult = projectFinanceService.generateSpendProfile(projectId);

        return validationHandler.addAnyErrors(generateResult).failNowOrSucceedWith(failureView, () ->
                redirectToViewSpendProfile(projectId)
        );
    }

    private String doViewSpendProfileSummary(Long projectId, Model model, ProjectSpendProfileForm form) {

        ProjectSpendProfileSummaryViewModel viewModel = populateSpendProfileViewModel(projectId);

        model.addAttribute("model", viewModel);
        model.addAttribute("form", form);

        return "project/finance/spend-profile/summary";
    }

    // TODO DW - a lot of this information will not be available in reality until the Finance Checks story is available,
    // so supporting the page with dummy data until then in order to unblock development on other Spend Profile stories
    private ProjectSpendProfileSummaryViewModel populateSpendProfileViewModel(Long projectId) {

        ProjectResource project = projectService.getById(projectId);
        ApplicationResource application = applicationService.getById(project.getApplication());
        CompetitionSummaryResource competitionSummary = applicationSummaryService.getCompetitionSummaryByCompetitionId(application.getCompetition());
        List<OrganisationResource> partnerOrganisations = projectService.getPartnerOrganisationsForProject(projectId);

        Optional<SpendProfileResource> anySpendProfile = projectFinanceService.getSpendProfile(projectId, partnerOrganisations.get(0).getId());

        ApplicationFinanceResource applicationFinanceResource =
                applicationFinanceService.getApplicationOrganisationFinances(application.getId(), partnerOrganisations.get(0).getId()).getSuccessObjectOrThrowException();

        List<ProjectSpendProfileSummaryViewModel.SpendProfileOrganisationRow> organisationRows = mapWithIndex(partnerOrganisations, (i, org) ->

                new ProjectSpendProfileSummaryViewModel.SpendProfileOrganisationRow(
                    org.getId(), org.getName(),
                    getEnumForIndex(ProjectSpendProfileSummaryViewModel.Viability.class, i),
                    getEnumForIndex(ProjectSpendProfileSummaryViewModel.RagStatus.class, i),
                    getEnumForIndex(ProjectSpendProfileSummaryViewModel.Eligibility.class, i),
                    getEnumForIndex(ProjectSpendProfileSummaryViewModel.RagStatus.class, i + 1),
                    getEnumForIndex(ProjectSpendProfileSummaryViewModel.QueriesRaised.class, i))
        );


/*        public ProjectSpendProfileSummaryViewModel(
                Long projectId, CompetitionSummaryResource competitionSummary,
                List< ProjectSpendProfileSummaryViewModel.SpendProfileOrganisationRow > partnerOrganisationDetails,
                LocalDate projectStartDate, int durationInMonths, BigDecimal totalProjectCost, BigDecimal grantAppliedFor,
                BigDecimal otherPublicSectorFunding, BigDecimal totalPercentageGrant, boolean spendProfilesGenerated)*/


        return new ProjectSpendProfileSummaryViewModel(
                projectId, competitionSummary, organisationRows,
                project.getTargetStartDate(), project.getDurationInMonths().intValue(),
                applicationFinanceResource.getTotal(), applicationFinanceResource.getTotalFundingSought(),
                applicationFinanceResource.getTotalOtherFunding(),
                new BigDecimal(applicationFinanceResource.getGrantClaimPercentage().toString()),
                anySpendProfile.isPresent());

/*        return new ProjectSpendProfileSummaryViewModel(
                projectId, competitionSummary, organisationRows,
                project.getTargetStartDate(), project.getDurationInMonths().intValue(),
                BigDecimal.valueOf(400000), BigDecimal.valueOf(200000),
                BigDecimal.valueOf(0),
                BigDecimal.valueOf(50),
                anySpendProfile.isPresent());*/
    }

    private <T extends Enum> T getEnumForIndex(Class<T> enums, int index) {
        T[] enumConstants = enums.getEnumConstants();
        return enumConstants[index % enumConstants.length];
    }

    private String redirectToViewSpendProfile(Long projectId) {
        return "redirect:/project/" + projectId + "/spend-profile/summary";
    }
}
