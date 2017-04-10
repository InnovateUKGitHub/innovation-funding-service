package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.service.ApplicationFundingDecisionService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.form.ApplicationSummaryQueryForm;
import org.innovateuk.ifs.competition.form.FundingDecisionForm;
import org.innovateuk.ifs.competition.service.ApplicationSummarySortFieldService;
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
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private CompetitionService competitionService;

    @Autowired
    private ApplicationFundingDecisionService applicationFundingDecisionService;

    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String applications(Model model,
                               @PathVariable("competitionId") Long competitionId,
                               @RequestParam Map<String, String> queryParams,
                               @ModelAttribute @Valid ApplicationSummaryQueryForm queryForm,
                               @ModelAttribute FundingDecisionForm fundingDecisionForm,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "redirect:/competition/" + competitionId + "/funding";
        }

        CompetitionSummaryResource competitionSummary = applicationSummaryRestService
                .getCompetitionSummary(competitionId)
                .getSuccessObjectOrThrowException();

        model.addAttribute("competitionSummary", competitionSummary);
        String originQuery = buildOriginQueryString(CompetitionManagementApplicationController.ApplicationOverviewOrigin.FUNDING_APPLICATIONS, getFilteredParameterValues(queryParams));
        model.addAttribute("originQuery", originQuery);

        switch (competitionSummary.getCompetitionStatus()) {
            case FUNDERS_PANEL:
            case ASSESSOR_FEEDBACK:
                return fundersPanelCompetition(model, competitionId, competitionSummary, fundingDecisionForm, originQuery, queryForm, bindingResult);
            default:
                return "redirect:/login";
        }
    }

    MultiValueMap<String, String> getFilteredParameterValues(Map<String, String> queryParams)  {
        Map<String, String> map = queryParams.entrySet().stream()
                .filter(entry -> !FILTERED_PARAMS.contains(entry.getKey()))
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();

        map.entrySet().forEach(entry -> multiValueMap.add(entry.getKey(), entry.getValue()));

        return multiValueMap;
    }

    private String fundersPanelCompetition(Model model,
                                           Long competitionId,
                                           CompetitionSummaryResource competitionSummary,
                                           FundingDecisionForm fundingDecisionForm,
                                           String originQuery,
                                           ApplicationSummaryQueryForm queryForm,
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

        populateSubmittedModel(model, competitionSummary, queryForm, originQuery);

        return "comp-mgt-funders-panel";
    }

    private void populateSubmittedModel(Model model, CompetitionSummaryResource competitionSummary, ApplicationSummaryQueryForm queryForm, String originQuery) {
        String sort = applicationSummarySortFieldService.sortFieldForSubmittedApplications(queryForm.getSort());
        ApplicationSummaryPageResource results = applicationSummaryRestService.getSubmittedApplications(
                competitionSummary.getCompetitionId(),
                sort,
                queryForm.getPage(),
                PAGE_SIZE,
                queryForm.getStringFilter(),
                queryForm.getFundingFilter())
                .getSuccessObjectOrThrowException();

        model.addAttribute("pagination", new PaginationViewModel(results, originQuery));
        model.addAttribute("results", results);
        model.addAttribute("activeTab", "submitted");
        model.addAttribute("activeSortField", sort);
    }
}
