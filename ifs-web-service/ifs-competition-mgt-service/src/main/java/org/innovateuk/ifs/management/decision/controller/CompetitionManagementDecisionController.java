package org.innovateuk.ifs.management.decision.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.cookie.CompetitionManagementCookieController;
import org.innovateuk.ifs.management.decision.form.*;
import org.innovateuk.ifs.management.decision.populator.CompetitionManagementFundingDecisionModelPopulator;
import org.innovateuk.ifs.management.decision.service.ApplicationFundingDecisionService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

/**
 * Handles the Competition Management decision views and submission of decision.
 */
@Slf4j
@Component
public abstract class CompetitionManagementDecisionController extends CompetitionManagementCookieController<FundingDecisionSelectionCookie> {

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Autowired
    private ApplicationFundingDecisionService applicationFundingDecisionService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionManagementFundingDecisionModelPopulator competitionManagementFundingDecisionModelPopulator;

    @Value("${ifs.always.open.competition.enabled}")
    private boolean alwaysOpenCompetitionEnabled;

    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;

    protected abstract String getCookieName();

    protected Class<FundingDecisionSelectionCookie> getFormType() {
        return FundingDecisionSelectionCookie.class;
    }

    public String applications(Model model,
                               long competitionId,
                               boolean filterChanged,
                               FundingDecisionPaginationForm paginationForm,
                               FundingDecisionFilterForm filterForm,
                               FundingDecisionSelectionForm selectionForm,
                               UserResource user,
                               BindingResult bindingResult,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        return viewApplications(model, competitionId, filterChanged, paginationForm, filterForm, selectionForm, user, bindingResult, request, response);
    }

    protected String viewApplications(Model model,
                                    long competitionId,
                                    boolean filterChanged,
                                    FundingDecisionPaginationForm paginationForm,
                                    FundingDecisionFilterForm filterForm,
                                    FundingDecisionSelectionForm selectionForm,
                                    UserResource user,
                                    BindingResult bindingResult,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        redirectIfErrorsOrCompNotInCorrectState(competitionId, filterForm, bindingResult);

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
        model.addAttribute("model", competitionManagementFundingDecisionModelPopulator.populate(competitionId, paginationForm, filterForm, selectionForm, user));

        return "comp-mgt-funders-panel";
    }

    public String makeDecision(Model model,
                               long competitionId,
                               FundingDecisionPaginationForm paginationForm,
                               FundingDecisionSelectionForm fundingDecisionSelectionForm,
                               FundingDecisionChoiceForm fundingDecisionChoiceForm,
                               FundingDecisionFilterForm filterForm,
                               UserResource user,
                               BindingResult bindingResult,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        redirectIfErrorsOrCompNotInCorrectState(competitionId, filterForm, bindingResult);

        FundingDecisionSelectionCookie selectionForm = getSelectionFormFromCookie(request, competitionId)
                .orElse(new FundingDecisionSelectionCookie(fundingDecisionSelectionForm));
        return fundersPanelCompetition(model, competitionId, selectionForm, paginationForm, fundingDecisionChoiceForm, filterForm, user, bindingResult, response);
    }

    public JsonNode addAllApplicationsToFundingDecisionSelectionList(
           long competitionId,
           boolean addAll,
           HttpServletRequest request,
           HttpServletResponse response) {
        FundingDecisionSelectionCookie selectionCookie;

        try {
            selectionCookie = getSelectionFormFromCookie(request, competitionId).orElse(new FundingDecisionSelectionCookie());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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

    public JsonNode addSelectedApplicationsToFundingDecisionList(
            long competitionId,
            long applicationId,
            boolean isSelected,
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
            log.error(e.getMessage(), e);
            return createFailureResponse();
        }
    }

    private List<Long> getAllApplicationIdsByFilters(long competitionId, FundingDecisionFilterForm filterForm) {
        if(alwaysOpenCompetitionEnabled) {
            CompetitionResource competition = getCompetitionIfExist(competitionId);
            if (competition.isAlwaysOpen() && competition.isHasAssessmentStage()) {
                return applicationSummaryRestService.getAllAssessedApplicationIds(competitionId, filterForm.getStringFilter(), filterForm.getFundingFilter()).getOrElse(emptyList());
            }
        }

        return  competitionManagementFundingDecisionModelPopulator.getAllSubmittedApplicationIds(competitionId, filterForm);
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
                                           UserResource user,
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

        model.addAttribute("model", competitionManagementFundingDecisionModelPopulator.populate(competitionId, fundingDecisionPaginationForm, fundingDecisionFilterForm, selectionForm, user));

        return "comp-mgt-funders-panel";
    }

    private CompetitionResource getCompetitionIfExist(long competitionId) {
        return competitionRestService.getCompetitionById(competitionId).getSuccess();
    }

    protected abstract String getDefaultView(long competitionId);

    private String redirectIfErrorsOrCompNotInCorrectState(long competitionId, FundingDecisionFilterForm filterForm, BindingResult bindingResult) {

        CompetitionResource competition = getCompetitionIfExist(competitionId);
        List<CompetitionStatus> acceptedCompStates = Arrays.asList(CompetitionStatus.ASSESSOR_FEEDBACK, CompetitionStatus.FUNDERS_PANEL);

        if (!acceptedCompStates.contains(competition.getCompetitionStatus())) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            return "redirect:/" + getDefaultView(competitionId);
        }

        return null;
    }
}