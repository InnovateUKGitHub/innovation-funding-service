package org.innovateuk.ifs.management.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.service.ApplicationFundingDecisionService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.form.*;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;

/**
 * Handles the Competition Management Funding decision views and submission of funding decision.
 */
@Controller
@RequestMapping("/competition/{competitionId}/funding")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = CompetitionManagementFundingDecisionController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionManagementFundingDecisionController extends CompetitionManagementCookieController<FundingDecisionSelectionCookie> {

    private static final Log log = LogFactory.getLog(CompetitionManagementFundingDecisionController.class);
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

    protected String getCookieName() {
        return "fundingDecisionSelectionForm";
    }

    protected Class<FundingDecisionSelectionCookie> getFormType() {
        return FundingDecisionSelectionCookie.class;
    }

    @GetMapping
    public String applications(Model model,
                               @PathVariable("competitionId") long competitionId,
                               @RequestParam(name = "filterChanged", required = false) boolean filterChanged,
                               @ModelAttribute @Valid FundingDecisionPaginationForm paginationForm,
                               @ModelAttribute FundingDecisionFilterForm filterForm,
                               @ModelAttribute FundingDecisionSelectionForm selectionForm,
                               BindingResult bindingResult,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            return "redirect:/competition/" + competitionId + "/funding";
        }

        FundingDecisionSelectionCookie selectionCookieForm = getSelectionFormFromCookie(request, competitionId).orElse(new FundingDecisionSelectionCookie());

        selectionForm = selectionCookieForm.getFundingDecisionSelectionForm();
        FundingDecisionFilterForm filterCookieForm = selectionCookieForm.getFundingDecisionFilterForm();

        if (!filterForm.anyFilterIsActive()
                && filterCookieForm.anyFilterIsActive()
                && !filterChanged
                && selectionForm.anySelectionIsMade()) {
            filterForm.updateAllFilters(selectionCookieForm.getFundingDecisionFilterForm());
        }

        FundingDecisionSelectionForm trimmedSelectionForm = trimSelectionByFilteredResult(selectionForm, filterForm, competitionId);
        selectionForm.setApplicationIds(trimmedSelectionForm.getApplicationIds());
        selectionForm.setAllSelected(trimmedSelectionForm.isAllSelected());
        selectionCookieForm.setFundingDecisionFilterForm(filterForm);

        saveFormToCookie(response, competitionId, selectionCookieForm);

        return populateSubmittedModel(model, competitionId, paginationForm, filterForm, selectionForm);
    }

    @PostMapping
    public String makeDecision(Model model,
                               @PathVariable("competitionId") long competitionId,
                               @ModelAttribute FundingDecisionPaginationForm paginationForm,
                               @ModelAttribute FundingDecisionSelectionForm fundingDecisionSelectionForm,
                               @ModelAttribute @Valid FundingDecisionChoiceForm fundingDecisionChoiceForm,
                               @ModelAttribute FundingDecisionFilterForm filterForm,
                               BindingResult bindingResult,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "redirect:/competition/" + competitionId + "/funding";
        }

        FundingDecisionSelectionCookie selectionForm = getSelectionFormFromCookie(request, competitionId)
                .orElse(new FundingDecisionSelectionCookie(fundingDecisionSelectionForm));
        return fundersPanelCompetition(model, competitionId, selectionForm, paginationForm, fundingDecisionChoiceForm, filterForm, bindingResult, response);
    }

    @PostMapping(params = {"addAll"})
    public @ResponseBody JsonNode addAllApplicationsToFundingDecisionSelectionList(@PathVariable("competitionId") long competitionId,
                                                                                   @RequestParam("addAll") boolean addAll,
                                                                                   HttpServletRequest request,
                                                                                   HttpServletResponse response) {
        FundingDecisionSelectionCookie selectionCookie;

        try {
            selectionCookie = getSelectionFormFromCookie(request, competitionId).orElse(new FundingDecisionSelectionCookie());
        } catch (Exception e) {
            log.error(e);
            return createFailureResponse();
        }

        if (addAll) {
            List<Long> allApplicationIdsBasedOnFilter = getAllApplicationIdsByFilters(competitionId, selectionCookie.getFundingDecisionFilterForm());
            addAllApplicationsIdsBasedOnFilter(selectionCookie, allApplicationIdsBasedOnFilter);
        } else {
            removeAllApplicationsIds(selectionCookie.getFundingDecisionSelectionForm());
        }

        saveFormToCookie(response, competitionId, selectionCookie);
        return createSuccessfulResponseWithSelectionStatus(selectionCookie.getFundingDecisionSelectionForm().getApplicationIds().size(), selectionCookie.getFundingDecisionSelectionForm().isAllSelected(), false);
    }

    private void addAllApplicationsIdsBasedOnFilter(FundingDecisionSelectionCookie selectionCookie, List<Long> allIds) {
        List<Long> limitedList = limitList(allIds);

        selectionCookie.getFundingDecisionSelectionForm().setApplicationIds(limitedList);
        selectionCookie.getFundingDecisionSelectionForm().setAllSelected(true);
    }

    private void removeAllApplicationsIds(FundingDecisionSelectionForm selectionForm) {
        selectionForm.getApplicationIds().clear();
        selectionForm.setAllSelected(false);
    }

    @PostMapping(params = {"selectionId", "isSelected"})
    public @ResponseBody JsonNode addSelectedApplicationsToFundingDecisionList(@PathVariable("competitionId") long competitionId,
                                                                               @RequestParam("selectionId") long applicationId,
                                                                               @RequestParam("isSelected") boolean isSelected,
                                                                               HttpServletRequest request,
                                                                               HttpServletResponse response) {
        boolean limitExceeded = false;

        try {
            FundingDecisionSelectionCookie cookieForm = getSelectionFormFromCookie(request, competitionId).orElse(new FundingDecisionSelectionCookie());
            FundingDecisionSelectionForm selectionForm = cookieForm.getFundingDecisionSelectionForm();
            if (isSelected) {
                List<Long> applicationIds = selectionForm.getApplicationIds();
                int predictedSize = selectionForm.getApplicationIds().size() + 1;

                if(!applicationIds.contains(applicationId)) {

                    if(limitIsExceeded(predictedSize)){
                        limitExceeded = true;
                    } else {
                        selectionForm.getApplicationIds().add(applicationId);
                        List<Long> filteredApplicationList = getAllApplicationIdsByFilters(competitionId, cookieForm.getFundingDecisionFilterForm());

                        if (selectionForm.containsAll(filteredApplicationList)) {
                            selectionForm.setAllSelected(true);
                        }
                    }
                }
            } else {
                selectionForm.getApplicationIds().remove(applicationId);
                selectionForm.setAllSelected(false);
            }

            cookieForm.setFundingDecisionSelectionForm(selectionForm);
            saveFormToCookie(response, competitionId, cookieForm);
            return createSuccessfulResponseWithSelectionStatus(selectionForm.getApplicationIds().size(), selectionForm.isAllSelected(), limitExceeded);
        } catch (Exception e) {
            log.error(e);
            return createFailureResponse();
        }
    }

    private List<Long> getAllApplicationIdsByFilters(long competitionId, FundingDecisionFilterForm filterForm) {
        return applicationSummaryRestService.getAllSubmittedApplicationIds(competitionId, filterForm.getStringFilter(), filterForm.getFundingFilter()).getOrElse(emptyList());
    }

    private FundingDecisionSelectionForm trimSelectionByFilteredResult(FundingDecisionSelectionForm selectionForm,
                                                                       FundingDecisionFilterForm filterForm,
                                                                       long competitionId) {
        List<Long> filteredApplicationIds = getAllApplicationIdsByFilters(competitionId, filterForm);
        FundingDecisionSelectionForm updatedSelectionForm = new FundingDecisionSelectionForm();

        selectionForm.getApplicationIds().retainAll(filteredApplicationIds);
        updatedSelectionForm.setApplicationIds(selectionForm.getApplicationIds());

        if (updatedSelectionForm.getApplicationIds().equals(filteredApplicationIds)  && !updatedSelectionForm.getApplicationIds().isEmpty()) {
            updatedSelectionForm.setAllSelected(true);
        } else {
            updatedSelectionForm.setAllSelected(false);
        }

        return updatedSelectionForm;
    }

    private MultiValueMap<String, String> mapFormFilterParametersToMultiValueMap(FundingDecisionFilterForm fundingDecisionFilterForm) {
        MultiValueMap<String, String> filterMap = new LinkedMultiValueMap<>();
        if(fundingDecisionFilterForm.getFundingFilter().isPresent()) {
            filterMap.set("fundingFilter", fundingDecisionFilterForm.getFundingFilter().get().getName());
        }
        if(fundingDecisionFilterForm.getStringFilter().isPresent()) {
            filterMap.set("stringFilter", fundingDecisionFilterForm.getStringFilter().get());
        }

        return filterMap;
    }

    private String fundersPanelCompetition(Model model,
                                           long competitionId,
                                           FundingDecisionSelectionCookie selectionCookie,
                                           FundingDecisionPaginationForm fundingDecisionPaginationForm,
                                           FundingDecisionChoiceForm fundingDecisionChoiceForm,
                                           FundingDecisionFilterForm fundingDecisionFilterForm,
                                           BindingResult bindingResult,
                                           HttpServletResponse response) {
        FundingDecisionSelectionForm selectionForm = selectionCookie.getFundingDecisionSelectionForm();
        if (fundingDecisionChoiceForm.getFundingDecision() != null) {
            validator.validate(selectionForm, bindingResult);
            if (!bindingResult.hasErrors()) {
                Optional<FundingDecision> fundingDecision = applicationFundingDecisionService.getFundingDecisionForString(fundingDecisionChoiceForm.getFundingDecision());
                if (fundingDecision.isPresent()) {
                    applicationFundingDecisionService.saveApplicationFundingDecisionData(competitionId, fundingDecision.get(), selectionForm.getApplicationIds());
                    removeAllApplicationsIds(selectionForm);
                    saveFormToCookie(response, competitionId, selectionCookie);
                }
            }
        }

        return populateSubmittedModel(model, competitionId, fundingDecisionPaginationForm, fundingDecisionFilterForm, selectionForm);
    }

    private ApplicationSummaryPageResource getApplicationsByFilters(long competitionId,
                                                                    FundingDecisionPaginationForm paginationForm,
                                                                    FundingDecisionFilterForm fundingDecisionFilterForm) {
        return applicationSummaryRestService.getSubmittedApplications(
                competitionId,
                "id",
                paginationForm.getPage(),
                PAGE_SIZE,
                fundingDecisionFilterForm.getStringFilter(),
                fundingDecisionFilterForm.getFundingFilter())
                .getSuccessObjectOrThrowException();
    }

    private String populateSubmittedModel(Model model, long competitionId, FundingDecisionPaginationForm paginationForm, FundingDecisionFilterForm fundingDecisionFilterForm, FundingDecisionSelectionForm fundingDecisionSelectionForm) {
        ApplicationSummaryPageResource results = getApplicationsByFilters(competitionId, paginationForm, fundingDecisionFilterForm);
        String originQuery = buildOriginQueryString(CompetitionManagementApplicationServiceImpl.ApplicationOverviewOrigin.FUNDING_APPLICATIONS, mapFormFilterParametersToMultiValueMap(fundingDecisionFilterForm));

        CompetitionSummaryResource competitionSummary = applicationSummaryRestService
                .getCompetitionSummary(competitionId)
                .getSuccessObjectOrThrowException();

        List<Long> submittableApplicationIds = getAllApplicationIdsByFilters(competitionId, fundingDecisionFilterForm);
        boolean selectionLimitWarning = limitIsExceeded(submittableApplicationIds.size());
        boolean selectAllDisabled =  submittableApplicationIds.isEmpty();

        model.addAttribute("pagination", new PaginationViewModel(results, originQuery));
        model.addAttribute("results", results);
        model.addAttribute("selectionForm", fundingDecisionSelectionForm);
        model.addAttribute("competitionSummary", competitionSummary);
        model.addAttribute("originQuery", originQuery);
        model.addAttribute("selectAllDisabled", selectAllDisabled);
        model.addAttribute("selectionLimitWarning", selectionLimitWarning);

        switch (competitionSummary.getCompetitionStatus()) {
            case FUNDERS_PANEL:
            case ASSESSOR_FEEDBACK:
                return "comp-mgt-funders-panel";
            default:
                return "redirect:/login";
        }
    }
}
