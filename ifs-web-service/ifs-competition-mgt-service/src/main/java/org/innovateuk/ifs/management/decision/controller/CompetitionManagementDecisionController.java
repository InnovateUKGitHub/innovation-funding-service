package org.innovateuk.ifs.management.decision.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.application.resource.Decision;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.cookie.CompetitionManagementCookieController;
import org.innovateuk.ifs.management.decision.form.*;
import org.innovateuk.ifs.management.decision.populator.CompetitionManagementApplicationDecisionModelPopulator;
import org.innovateuk.ifs.management.decision.service.ApplicationDecisionService;
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
public abstract class CompetitionManagementDecisionController extends CompetitionManagementCookieController<DecisionSelectionCookie> {

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Autowired
    private ApplicationDecisionService applicationDecisionService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionManagementApplicationDecisionModelPopulator competitionManagementApplicationDecisionModelPopulator;

    @Value("${ifs.always.open.competition.enabled}")
    private boolean alwaysOpenCompetitionEnabled;

    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;

    protected abstract String getCookieName();

    protected Class<DecisionSelectionCookie> getFormType() {
        return DecisionSelectionCookie.class;
    }

    public String applications(Model model,
                               long competitionId,
                               boolean filterChanged,
                               DecisionPaginationForm paginationForm,
                               DecisionFilterForm filterForm,
                               DecisionSelectionForm selectionForm,
                               UserResource user,
                               BindingResult bindingResult,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        return viewApplications(model, competitionId, filterChanged, paginationForm, filterForm, selectionForm, user, bindingResult, request, response);
    }

    protected String viewApplications(Model model,
                                    long competitionId,
                                    boolean filterChanged,
                                    DecisionPaginationForm paginationForm,
                                    DecisionFilterForm filterForm,
                                    DecisionSelectionForm selectionForm,
                                    UserResource user,
                                    BindingResult bindingResult,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        redirectIfErrorsOrCompNotInCorrectState(competitionId, filterForm, bindingResult);

        DecisionSelectionCookie selectionCookieForm = getSelectionFormFromCookie(request, competitionId).orElse(new DecisionSelectionCookie());

        selectionForm = selectionCookieForm.getDecisionSelectionForm();
        DecisionFilterForm filterCookieForm = selectionCookieForm.getDecisionFilterForm();

        if (!filterForm.anyFilterIsActive()
                && filterCookieForm.anyFilterIsActive()
                && !filterChanged
                && selectionForm.anySelectionIsMade()) {
            filterForm.updateAllFilters(selectionCookieForm.getDecisionFilterForm());
        }

        DecisionSelectionForm trimmedSelectionForm = trimSelectionByFilteredResult(selectionForm, filterForm, competitionId);
        selectionForm.setApplicationIds(trimmedSelectionForm.getApplicationIds());
        selectionForm.setAllSelected(trimmedSelectionForm.isAllSelected());
        selectionCookieForm.setDecisionFilterForm(filterForm);

        saveFormToCookie(response, competitionId, selectionCookieForm);
        model.addAttribute("model", competitionManagementApplicationDecisionModelPopulator.populate(competitionId, paginationForm, filterForm, selectionForm, user));

        return "comp-mgt-funders-panel";
    }

    public String makeDecision(Model model,
                               long competitionId,
                               DecisionPaginationForm paginationForm,
                               DecisionSelectionForm decisionSelectionForm,
                               DecisionChoiceForm decisionChoiceForm,
                               DecisionFilterForm filterForm,
                               UserResource user,
                               BindingResult bindingResult,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        redirectIfErrorsOrCompNotInCorrectState(competitionId, filterForm, bindingResult);

        DecisionSelectionCookie selectionForm = getSelectionFormFromCookie(request, competitionId)
                .orElse(new DecisionSelectionCookie(decisionSelectionForm));
        return fundersPanelCompetition(model, competitionId, selectionForm, paginationForm, decisionChoiceForm, filterForm, user, bindingResult, response);
    }

    public JsonNode addAllApplicationsToDecisionSelectionList(
           long competitionId,
           boolean addAll,
           HttpServletRequest request,
           HttpServletResponse response) {
        DecisionSelectionCookie selectionCookie;

        try {
            selectionCookie = getSelectionFormFromCookie(request, competitionId).orElse(new DecisionSelectionCookie());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return createFailureResponse();
        }

        if (addAll) {
            List<Long> allApplicationIdsBasedOnFilter = getAllApplicationIdsByFilters(competitionId, selectionCookie.getDecisionFilterForm());
            addAllApplicationsIdsBasedOnFilter(selectionCookie, allApplicationIdsBasedOnFilter);
        } else {
            removeAllApplicationsIds(selectionCookie.getDecisionSelectionForm());
        }

        saveFormToCookie(response, competitionId, selectionCookie);
        return createSuccessfulResponseWithSelectionStatus(selectionCookie.getDecisionSelectionForm().getApplicationIds().size(), selectionCookie.getDecisionSelectionForm().isAllSelected(), false);
    }

    private void addAllApplicationsIdsBasedOnFilter(DecisionSelectionCookie selectionCookie, List<Long> allIds) {
        List<Long> limitedList = limitList(allIds);

        selectionCookie.getDecisionSelectionForm().setApplicationIds(limitedList);
        selectionCookie.getDecisionSelectionForm().setAllSelected(true);
    }

    private void removeAllApplicationsIds(DecisionSelectionForm selectionForm) {
        selectionForm.getApplicationIds().clear();
        selectionForm.setAllSelected(false);
    }

    public JsonNode addSelectedApplicationsToDecisionList(
            long competitionId,
            long applicationId,
            boolean isSelected,
            HttpServletRequest request,
            HttpServletResponse response) {
        boolean limitExceeded = false;

        try {
            DecisionSelectionCookie cookieForm = getSelectionFormFromCookie(request, competitionId).orElse(new DecisionSelectionCookie());
            DecisionSelectionForm selectionForm = cookieForm.getDecisionSelectionForm();
            if (isSelected) {
                List<Long> applicationIds = selectionForm.getApplicationIds();
                int predictedSize = selectionForm.getApplicationIds().size() + 1;

                if(!applicationIds.contains(applicationId)) {

                    if(limitIsExceeded(predictedSize)){
                        limitExceeded = true;
                    } else {
                        selectionForm.getApplicationIds().add(applicationId);
                        List<Long> filteredApplicationList = getAllApplicationIdsByFilters(competitionId, cookieForm.getDecisionFilterForm());

                        if (selectionForm.containsAll(filteredApplicationList)) {
                            selectionForm.setAllSelected(true);
                        }
                    }
                }
            } else {
                selectionForm.getApplicationIds().remove(applicationId);
                selectionForm.setAllSelected(false);
            }

            cookieForm.setDecisionSelectionForm(selectionForm);
            saveFormToCookie(response, competitionId, cookieForm);
            return createSuccessfulResponseWithSelectionStatus(selectionForm.getApplicationIds().size(), selectionForm.isAllSelected(), limitExceeded);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return createFailureResponse();
        }
    }

    private List<Long> getAllApplicationIdsByFilters(long competitionId, DecisionFilterForm filterForm) {
        if(alwaysOpenCompetitionEnabled) {
            CompetitionResource competition = getCompetitionIfExist(competitionId);
            if (competition.isAlwaysOpen() && competition.isHasAssessmentStage()) {
                return applicationSummaryRestService.getAllAssessedApplicationIds(competitionId, filterForm.getStringFilter(), filterForm.getFundingFilter()).getOrElse(emptyList());
            }
        }

        return  competitionManagementApplicationDecisionModelPopulator.getAllSubmittedApplicationIds(competitionId, filterForm);
    }

    private DecisionSelectionForm trimSelectionByFilteredResult(DecisionSelectionForm selectionForm,
                                                                       DecisionFilterForm filterForm,
                                                                       long competitionId) {
        List<Long> filteredApplicationIds = getAllApplicationIdsByFilters(competitionId, filterForm);
        DecisionSelectionForm updatedSelectionForm = new DecisionSelectionForm();

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
                                           DecisionSelectionCookie selectionCookie,
                                           DecisionPaginationForm decisionPaginationForm,
                                           DecisionChoiceForm decisionChoiceForm,
                                           DecisionFilterForm decisionFilterForm,
                                           UserResource user,
                                           BindingResult bindingResult,
                                           HttpServletResponse response) {
        DecisionSelectionForm selectionForm = selectionCookie.getDecisionSelectionForm();
        if (decisionChoiceForm.getDecision() != null) {
            validator.validate(selectionForm, bindingResult);
            if (!bindingResult.hasErrors()) {
                Optional<Decision> decision = applicationDecisionService.getDecisionForString(decisionChoiceForm.getDecision());
                if (decision.isPresent()) {
                    applicationDecisionService.saveApplicationDecisionData(competitionId, decision.get(), selectionForm.getApplicationIds());
                    removeAllApplicationsIds(selectionForm);
                    saveFormToCookie(response, competitionId, selectionCookie);
                }
            }
        }

        model.addAttribute("model", competitionManagementApplicationDecisionModelPopulator.populate(competitionId, decisionPaginationForm, decisionFilterForm, selectionForm, user));

        return "comp-mgt-funders-panel";
    }

    private CompetitionResource getCompetitionIfExist(long competitionId) {
        return competitionRestService.getCompetitionById(competitionId).getSuccess();
    }

    protected abstract String getDefaultView(long competitionId);

    private String redirectIfErrorsOrCompNotInCorrectState(long competitionId, DecisionFilterForm filterForm, BindingResult bindingResult) {

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