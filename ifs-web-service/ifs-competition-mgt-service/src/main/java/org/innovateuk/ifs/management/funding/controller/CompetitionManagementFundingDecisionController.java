package org.innovateuk.ifs.management.funding.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.management.funding.service.ApplicationFundingDecisionService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.form.*;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.management.cookie.CompetitionManagementCookieController;
import org.innovateuk.ifs.management.funding.populator.CompetitionManagementFundingDecisionModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

/**
 * Handles the Competition Management Funding decision views and submission of funding decision.
 */
@Controller
@RequestMapping("/competition/{competitionId}/funding")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = CompetitionManagementFundingDecisionController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionManagementFundingDecisionController extends CompetitionManagementCookieController<FundingDecisionSelectionCookie> {

    private static final Log log = LogFactory.getLog(CompetitionManagementFundingDecisionController.class);

    private ApplicationSummaryRestService applicationSummaryRestService;
    private ApplicationFundingDecisionService applicationFundingDecisionService;
    private CompetitionService competitionService;
    private CompetitionManagementFundingDecisionModelPopulator competitionManagementFundingDecisionModelPopulator;

    @Autowired
    public CompetitionManagementFundingDecisionController(ApplicationSummaryRestService applicationSummaryRestService,
                                                          ApplicationFundingDecisionService applicationFundingDecisionService,
                                                          CompetitionService competitionService,
                                                          CompetitionManagementFundingDecisionModelPopulator competitionManagementFundingDecisionModelPopulator) {
        this.applicationSummaryRestService = applicationSummaryRestService;
        this.applicationFundingDecisionService = applicationFundingDecisionService;
        this.competitionService = competitionService;
        this.competitionManagementFundingDecisionModelPopulator = competitionManagementFundingDecisionModelPopulator;
    }

    public CompetitionManagementFundingDecisionController() {
    }

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

        redirectIfErrorsOrCompNotInCorrectState(competitionId, bindingResult);

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
        model.addAttribute("model", competitionManagementFundingDecisionModelPopulator.populate(competitionId, paginationForm, filterForm, selectionForm));

        return "comp-mgt-funders-panel";
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

        redirectIfErrorsOrCompNotInCorrectState(competitionId, bindingResult);

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

        model.addAttribute("model", competitionManagementFundingDecisionModelPopulator.populate(competitionId, fundingDecisionPaginationForm, fundingDecisionFilterForm, selectionForm));

        return "comp-mgt-funders-panel";
    }

    private CompetitionResource getCompetitionIfExist(long competitionId) {
        return competitionService.getById(competitionId);
    }

    private String redirectIfErrorsOrCompNotInCorrectState(long competitionId, BindingResult bindingResult) {

        CompetitionResource competition = getCompetitionIfExist(competitionId);
        List<CompetitionStatus> acceptedCompStates = Arrays.asList(CompetitionStatus.ASSESSOR_FEEDBACK, CompetitionStatus.FUNDERS_PANEL);

        if (!acceptedCompStates.contains(competition.getCompetitionStatus())) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            return "redirect:/competition/" + competition.getId() + "/funding";
        }

        return null;
    }
}