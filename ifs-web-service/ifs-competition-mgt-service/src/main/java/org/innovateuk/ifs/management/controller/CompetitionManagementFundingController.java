package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.service.ApplicationFundingDecisionService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.competition.form.FundingDecisionPaginationForm;
import org.innovateuk.ifs.competition.form.FundingDecisionFilterForm;
import org.innovateuk.ifs.competition.form.FundingDecisionForm;
import org.innovateuk.ifs.competition.service.ApplicationSummarySortFieldService;
import org.innovateuk.ifs.management.service.CompetitionManagementApplicationServiceImpl;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;

/**
 * Handles the Competition Management Funding decision views and submission of funding decision.
 */
@Controller
@RequestMapping("/competition/{competitionId}/funding")
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionManagementFundingController {

    public static final Collection<String> FILTERED_PARAMS = asList(
            "applicationIds",
            "fundingDecision",
            "_csrf");

    private static final int PAGE_SIZE = 20;

    @Autowired
    private ApplicationSummarySortFieldService applicationSummarySortFieldService;

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Autowired
    private ApplicationFundingDecisionService applicationFundingDecisionService;

    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;

    @GetMapping
    public String applications(Model model,
                               @PathVariable("competitionId") long competitionId,
                               @ModelAttribute @Valid FundingDecisionPaginationForm paginationForm,
                               @ModelAttribute(binding = false) FundingDecisionForm fundingDecisionForm,
                               @ModelAttribute FundingDecisionFilterForm filterForm,
                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "redirect:/competition/" + competitionId + "/funding";
        }

        CompetitionSummaryResource competitionSummary = applicationSummaryRestService
                .getCompetitionSummary(competitionId)
                .getSuccessObjectOrThrowException();

        model.addAttribute("competitionSummary", competitionSummary);
        String originQuery = buildOriginQueryString(CompetitionManagementApplicationServiceImpl.ApplicationOverviewOrigin.FUNDING_APPLICATIONS, mapFormFilterParametersToMultiValueMap(filterForm));
        model.addAttribute("originQuery", originQuery);

        switch (competitionSummary.getCompetitionStatus()) {
            case FUNDERS_PANEL:
            case ASSESSOR_FEEDBACK:
                return populateSubmittedModel(model, competitionSummary, paginationForm, filterForm, originQuery);
            default:
                return "redirect:/login";
        }
    }

    @PostMapping
    public String makeDecision(Model model,
                               @PathVariable("competitionId") long competitionId,
                               @ModelAttribute @Valid FundingDecisionPaginationForm paginationForm,
                               @ModelAttribute @Valid FundingDecisionForm fundingDecisionForm,
                               @ModelAttribute FundingDecisionFilterForm filterForm,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/competition/" + competitionId + "/funding";
        }
        CompetitionSummaryResource competitionSummary = applicationSummaryRestService
                .getCompetitionSummary(competitionId)
                .getSuccessObjectOrThrowException();

        model.addAttribute("competitionSummary", competitionSummary);
        String originQuery = buildOriginQueryString(CompetitionManagementApplicationServiceImpl.ApplicationOverviewOrigin.FUNDING_APPLICATIONS, mapFormFilterParametersToMultiValueMap(filterForm));
        model.addAttribute("originQuery", originQuery);

        switch (competitionSummary.getCompetitionStatus()) {
            case FUNDERS_PANEL:
            case ASSESSOR_FEEDBACK:
                return fundersPanelCompetition(model, competitionId, competitionSummary, fundingDecisionForm, paginationForm, filterForm, originQuery, bindingResult);
            default:
                return "redirect:/login";
        }
    }

    MultiValueMap<String, String> mapFormFilterParametersToMultiValueMap(FundingDecisionFilterForm fundingDecisionFilterForm) {
        MultiValueMap<String, String> filterMap = new LinkedMultiValueMap<String, String>();
        if(fundingDecisionFilterForm.getFundingFilter().isPresent()) {
            filterMap.put("fundingFilter", Arrays.asList(fundingDecisionFilterForm.getFundingFilter().get().getName()));
        }
        if(fundingDecisionFilterForm.getStringFilter().isPresent()) {
            filterMap.put("stringFilter",Arrays.asList(fundingDecisionFilterForm.getStringFilter().get()));
        }

        return filterMap;
    }

    private String fundersPanelCompetition(Model model,
                                           Long competitionId,
                                           CompetitionSummaryResource competitionSummary,
                                           FundingDecisionForm fundingDecisionForm,
                                           FundingDecisionPaginationForm fundingDecisionPaginationForm,
                                           FundingDecisionFilterForm fundingDecisionFilterForm,
                                           String originQuery,
                                           BindingResult bindingResult) {
        if (fundingDecisionForm.getFundingDecision() != null) {
            validator.validate(fundingDecisionForm, bindingResult);
            if (!bindingResult.hasErrors()) {
                Optional<FundingDecision> fundingDecision = applicationFundingDecisionService.getFundingDecisionForString(fundingDecisionForm.getFundingDecision());
                if (fundingDecision.isPresent()) {
                    applicationFundingDecisionService.saveApplicationFundingDecisionData(competitionId, fundingDecision.get(), fundingDecisionForm.getApplicationIds());
                }
            }
        }

        return populateSubmittedModel(model, competitionSummary, fundingDecisionPaginationForm, fundingDecisionFilterForm, originQuery);
    }

    private String populateSubmittedModel(Model model, CompetitionSummaryResource competitionSummary, FundingDecisionPaginationForm paginationForm, FundingDecisionFilterForm fundingDecisionFilterForm, String originQuery) {
        ApplicationSummaryPageResource results = applicationSummaryRestService.getSubmittedApplications(
                competitionSummary.getCompetitionId(),
                "id",
                paginationForm.getPage(),
                PAGE_SIZE,
                fundingDecisionFilterForm.getStringFilter(),
                fundingDecisionFilterForm.getFundingFilter())
                .getSuccessObjectOrThrowException();

        model.addAttribute("pagination", new PaginationViewModel(results, originQuery));
        model.addAttribute("results", results);
        model.addAttribute("activeSortField", "id");

        return "comp-mgt-funders-panel";
    }
}
