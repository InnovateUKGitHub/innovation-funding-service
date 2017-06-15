package org.innovateuk.ifs.management.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.service.ApplicationFundingDecisionService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.competition.form.FundingDecisionFilterForm;
import org.innovateuk.ifs.competition.form.FundingDecisionForm;
import org.innovateuk.ifs.competition.form.FundingDecisionPaginationForm;
import org.innovateuk.ifs.competition.form.FundingDecisionSelectionCookie;
import org.innovateuk.ifs.competition.service.ApplicationSummarySortFieldService;
import org.innovateuk.ifs.management.service.CompetitionManagementApplicationServiceImpl;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.innovateuk.ifs.util.CookieUtil;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;
import static org.innovateuk.ifs.util.JsonUtil.getObjectFromJson;
import static org.innovateuk.ifs.util.JsonUtil.getSerializedObject;

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

    private static final String SELECTION_FORM = "selectionForm";

    @Autowired
    private ApplicationSummarySortFieldService applicationSummarySortFieldService;

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Autowired
    private ApplicationFundingDecisionService applicationFundingDecisionService;

    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;

    @Autowired
    private CookieUtil cookieUtil;

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

        //TODO: Save / overwrite filter settings to cookie (FundingDecisionSelectionCookie)

        switch (competitionSummary.getCompetitionStatus()) {
            case FUNDERS_PANEL:
            case ASSESSOR_FEEDBACK:
                return populateSubmittedModel(model, competitionId, paginationForm, filterForm, originQuery);
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

        //TODO: Extract applicationIds from cookie to save with posted FundingDecision choice

        CompetitionSummaryResource competitionSummary = applicationSummaryRestService
                .getCompetitionSummary(competitionId)
                .getSuccessObjectOrThrowException();

        model.addAttribute("competitionSummary", competitionSummary);
        String originQuery = buildOriginQueryString(CompetitionManagementApplicationServiceImpl.ApplicationOverviewOrigin.FUNDING_APPLICATIONS, mapFormFilterParametersToMultiValueMap(filterForm));
        model.addAttribute("originQuery", originQuery);

        switch (competitionSummary.getCompetitionStatus()) {
            case FUNDERS_PANEL:
            case ASSESSOR_FEEDBACK:
                return fundersPanelCompetition(model, competitionId, fundingDecisionForm, paginationForm, filterForm, originQuery, bindingResult);
            default:
                return "redirect:/login";
        }
    }

    @PostMapping(value = "/find", params = {"addAll"})
    public @ResponseBody
    JsonNode addAllApplicationsToFundingDecisionList(@PathVariable("competitionId") long competitionId,
                                                     @RequestParam("addAll") boolean addAll,
                                                     HttpServletRequest request,
                                                     HttpServletResponse response) {
        try {
            FundingDecisionSelectionCookie selectionForm = getApplicationSelectionFormFromCookie(request, competitionId).orElse(new FundingDecisionSelectionCookie());

            if (addAll) {
                selectionForm.getFundingDecisionForm().setApplicationIds(getAllApplicationIdsByFilters(competitionId, selectionForm.getFundingDecisionFilterForm()));
                selectionForm.getFundingDecisionForm().setAllSelected(true);
            } else {
                selectionForm.getFundingDecisionForm().setApplicationIds(Arrays.asList());
                selectionForm.getFundingDecisionForm().setAllSelected(false);
            }

            cookieUtil.saveToCookie(response, format("%s_comp%s", SELECTION_FORM, competitionId), getSerializedObject(selectionForm));
            return createJsonObjectNode(selectionForm.getFundingDecisionForm().getApplicationIds().size());
        } catch (Exception e) {
            return createJsonObjectNode(-1);
        }
    }

    private Optional<FundingDecisionSelectionCookie> getApplicationSelectionFormFromCookie(HttpServletRequest request, long competitionId) {
        String assessorFormJson = cookieUtil.getCookieValue(request, format("%s_comp%s", SELECTION_FORM, competitionId));
        if (isNotBlank(assessorFormJson)) {
            return Optional.ofNullable(getObjectFromJson(assessorFormJson, FundingDecisionSelectionCookie.class));
        } else {
            return Optional.empty();
        }
    }

    private List<Long> getAllApplicationIdsByFilters(Long competitionId, FundingDecisionFilterForm filterForm) {
        //TODO: Use REST service here that can find all submitted applications by competitionId and filters (still needs to be created)

        return new ArrayList<Long>();
    }

    /*@PostMapping(value = "/find/addSelected")
    public String addSelectedApplicationsToFundingDecisionList(@PathVariable("competitionId") long competitionId,
                                                               @ModelAttribute("fundingSelection") boolean addAll) {

         //TODO: Implement endpoint based on sister endpoint in CompetitionManagementInviteAssessorsController                                                      HttpServletResponse response) {
    }*/

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

        return populateSubmittedModel(model, competitionId, fundingDecisionPaginationForm, fundingDecisionFilterForm, originQuery);
    }

    private ApplicationSummaryPageResource getApplicationsByFilters(Long competitionId, FundingDecisionPaginationForm paginationForm, FundingDecisionFilterForm fundingDecisionFilterForm) {
        return applicationSummaryRestService.getSubmittedApplications(
                competitionId,
                "id",
                paginationForm.getPage(),
                PAGE_SIZE,
                fundingDecisionFilterForm.getStringFilter(),
                fundingDecisionFilterForm.getFundingFilter())
                .getSuccessObjectOrThrowException();
    }

    private String populateSubmittedModel(Model model, Long competitionId, FundingDecisionPaginationForm paginationForm, FundingDecisionFilterForm fundingDecisionFilterForm, String originQuery) {
        ApplicationSummaryPageResource results = getApplicationsByFilters(competitionId, paginationForm, fundingDecisionFilterForm);

        model.addAttribute("pagination", new PaginationViewModel(results, originQuery));
        model.addAttribute("results", results);
        model.addAttribute("activeSortField", "id");

        return "comp-mgt-funders-panel";
    }

    private ObjectNode createJsonObjectNode(int selectionCount) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("selectionCount", selectionCount);

        return node;
    }
}
