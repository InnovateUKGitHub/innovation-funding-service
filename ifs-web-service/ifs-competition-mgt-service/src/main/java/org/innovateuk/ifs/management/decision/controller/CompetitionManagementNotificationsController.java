package org.innovateuk.ifs.management.decision.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.application.service.ApplicationDecisionRestService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.commons.exception.IncorrectStateForPageException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.cookie.CompetitionManagementCookieController;
import org.innovateuk.ifs.management.decision.form.FundingNotificationFilterForm;
import org.innovateuk.ifs.management.decision.form.FundingNotificationSelectionCookie;
import org.innovateuk.ifs.management.decision.form.FundingNotificationSelectionForm;
import org.innovateuk.ifs.management.decision.form.NotificationEmailsForm;
import org.innovateuk.ifs.management.decision.populator.ManageApplicationDecisionsModelPopulator;
import org.innovateuk.ifs.management.notification.populator.SendNotificationsModelPopulator;
import org.innovateuk.ifs.management.notification.viewmodel.SendNotificationsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@Slf4j
@Component
public abstract class CompetitionManagementNotificationsController extends CompetitionManagementCookieController<FundingNotificationSelectionCookie> {

    private static final String MANAGE_FUNDING_APPLICATIONS_VIEW = "comp-mgt-manage-funding-applications";
    private static final String FUNDING_DECISION_NOTIFICATION_VIEW = "comp-mgt-send-notifications";

    @Autowired
    private ManageApplicationDecisionsModelPopulator manageApplicationDecisionsModelPopulator;

    @Autowired
    private SendNotificationsModelPopulator sendNotificationsModelPopulator;

    @Autowired
    private ApplicationDecisionRestService applicationDecisionRestService;

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    protected abstract String getCookieName();

    protected abstract Class<FundingNotificationSelectionCookie> getFormType();

    protected String sendNotifications(Model model,
                                       Long competitionId,
                                       List<Long> applicationIds,
                                       boolean eoi) {
        checkCompetitionIsOpen(competitionId);

        NotificationEmailsForm form = new NotificationEmailsForm();
        return getDecisionPage(model, form, competitionId, applicationIds, eoi);
    }

    protected String sendNotificationsSubmit(Model model,
                                             long competitionId,
                                             NotificationEmailsForm form,
                                             boolean eoi,
                                             BindingResult bindingResult,
                                             ValidationHandler validationHandler) {
        checkCompetitionIsOpen(competitionId);

        FundingNotificationResource fundingNotificationResource = new FundingNotificationResource(form.getMessage(), form.getDecisions());

        Supplier<String> failureView = () -> getDecisionPage(model, form, competitionId, form.getApplicationIds(), eoi);
        Supplier<String> successView = () -> successfulEmailRedirect(competitionId);

        return validationHandler.performActionOrBindErrorsToField("", failureView, successView,
                () -> applicationDecisionRestService.sendApplicationDecisions(fundingNotificationResource));
    }

    private String getDecisionPage(Model model, NotificationEmailsForm form, long competitionId, List<Long> applicationIds, boolean eoi) {
        SendNotificationsViewModel viewModel = sendNotificationsModelPopulator.populate(competitionId, applicationIds, form, eoi);
        if (viewModel.getApplications().isEmpty()) {
            return "redirect:" + getManageFundingApplicationsPage(competitionId);
        }
        model.addAttribute("model", viewModel);
        model.addAttribute("form", form);
        return FUNDING_DECISION_NOTIFICATION_VIEW;
    }

    protected String applications(Model model,
                                  MultiValueMap<String, String> params,
                                  Long competitionId,
                                  FundingNotificationFilterForm filterForm,
                                  FundingNotificationSelectionForm selectionForm,
                                  boolean filterChanged,
                                  BindingResult bindingResult,
                                  ValidationHandler validationHandler,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        checkCompetitionIsOpen(competitionId);

        updateSelectionForm(request,
                response,
                competitionId,
                selectionForm,
                filterForm,
                filterChanged);

        List<Long> submittableApplications = getAllApplicationIdsByFilters(competitionId, filterForm);
        return validationHandler.failNowOrSucceedWith(queryFailureView(competitionId), () -> {
                model.addAttribute("model", manageApplicationDecisionsModelPopulator.populate(filterForm, competitionId, submittableApplications.size()));
                return MANAGE_FUNDING_APPLICATIONS_VIEW;
            }
        );
    }

    private void updateSelectionForm(HttpServletRequest request,
                                     HttpServletResponse response,
                                     long competitionId,
                                     FundingNotificationSelectionForm modelSelectionForm,
                                     FundingNotificationFilterForm modelFilterForm,
                                     boolean filterChanged) {
        FundingNotificationSelectionCookie storedSelectionFormCookie = getSelectionFormFromCookie(request, competitionId).orElse(new FundingNotificationSelectionCookie());

        FundingNotificationSelectionForm trimmedSelectionForm = trimSelectionByFilteredResult(storedSelectionFormCookie.getFundingNotificationSelectionForm(), modelFilterForm, competitionId);
        FundingNotificationFilterForm updatedFilterForm = updateFilterWithCookieFilterValues(storedSelectionFormCookie.getFundingNotificationFilterForm(), modelFilterForm, filterChanged, trimmedSelectionForm.anySelectionIsMade());

        modelSelectionForm.setIds(trimmedSelectionForm.getIds());
        modelSelectionForm.setAllSelected(trimmedSelectionForm.isAllSelected());
        modelFilterForm.setAllFilterOptions(updatedFilterForm.getStringFilter(),  updatedFilterForm.getSendFilter(), updatedFilterForm.getFundingFilter(), updatedFilterForm.isEoi());

        FundingNotificationSelectionCookie updatedSelectionFormCookie = new FundingNotificationSelectionCookie();
        updatedSelectionFormCookie.setFundingNotificationFilterForm(modelFilterForm);
        updatedSelectionFormCookie.setFundingNotificationSelectionForm(modelSelectionForm);

        saveFormToCookie(response, competitionId, updatedSelectionFormCookie);
    }

    private FundingNotificationFilterForm updateFilterWithCookieFilterValues(FundingNotificationFilterForm storedFilterForm, FundingNotificationFilterForm modelFilterForm, boolean filterChanged, boolean anySelectionMade) {
        if (storedFilterForm.anyFilterIsActive()
                && !modelFilterForm.anyFilterIsActive()
                && !filterChanged
                && anySelectionMade) {
            modelFilterForm.setAllFilterOptions(storedFilterForm.getStringFilter(), storedFilterForm.getSendFilter(), storedFilterForm.getFundingFilter(), storedFilterForm.isEoi());
        }

        return modelFilterForm;
    }

    private FundingNotificationSelectionForm trimSelectionByFilteredResult(FundingNotificationSelectionForm selectionForm,
                                                                           FundingNotificationFilterForm filterForm,
                                                                           Long competitionId) {
        List<Long> filteredApplicationIds = getAllApplicationIdsByFilters(competitionId, filterForm);
        FundingNotificationSelectionForm updatedSelectionForm = new FundingNotificationSelectionForm();

        selectionForm.getIds().retainAll(filteredApplicationIds);
        updatedSelectionForm.setIds(selectionForm.getIds());

        if (updatedSelectionForm.getIds().equals(filteredApplicationIds) && !updatedSelectionForm.getIds().isEmpty()) {
            updatedSelectionForm.setAllSelected(true);
        } else {
            updatedSelectionForm.setAllSelected(false);
        }

        return updatedSelectionForm;
    }

    protected String selectApplications(Model model,
                                        Long competitionId,
                                        FundingNotificationFilterForm query,
                                        ValidationHandler queryFormValidationHandler,
                                        FundingNotificationSelectionForm selectionForm,
                                        BindingResult idsBindingResult,
                                        ValidationHandler idsValidationHandler,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        checkCompetitionIsOpen(competitionId);

        FundingNotificationSelectionCookie selectionCookie = getSelectionFormFromCookie(request, competitionId)
                .orElse(new FundingNotificationSelectionCookie(selectionForm));

        return queryFormValidationHandler.failNowOrSucceedWith(queryFailureView(competitionId),  // Pass or fail JSR 303 on the query form
                () -> idsValidationHandler.failNowOrSucceedWith(idsFailureView(competitionId, query, model, request), // Pass or fail JSR 303 on the ids
                        () -> {
                            // Custom validation
                            if (selectionCookie.getFundingNotificationSelectionForm().getIds().isEmpty()) {
                                idsBindingResult.rejectValue("ids", "validation.manage.funding.applications.no.application.selected");
                            }
                            return idsValidationHandler.failNowOrSucceedWith(idsFailureView(competitionId, query, model, request), // Pass or fail custom validation
                                    () -> {
                                        removeCookie(response, competitionId);
                                        return composeEmailRedirect(competitionId, selectionCookie.getFundingNotificationSelectionForm().getIds());
                                    });
                        }
                )
        );
    }

    protected JsonNode selectApplicationForEmailList(long competitionId,
                                                     long applicationId,
                                                     boolean isSelected,
                                                     HttpServletRequest request,
                                                     HttpServletResponse response) {
        checkCompetitionIsOpen(competitionId);

        boolean limitIsExceeded = false;

        try {
            FundingNotificationSelectionCookie selectionCookie = getSelectionFormFromCookie(request, competitionId).orElse(new FundingNotificationSelectionCookie());

            if (isSelected) {
                int predictedSize = selectionCookie.getFundingNotificationSelectionForm().getIds().size() + 1;
                if(limitIsExceeded(predictedSize)) {
                    limitIsExceeded = true;
                }
                else {
                    handleSelected(selectionCookie, competitionId, applicationId);
                }
            } else {
                selectionCookie.getFundingNotificationSelectionForm().getIds().remove(applicationId);
                selectionCookie.getFundingNotificationSelectionForm().setAllSelected(false);
            }
            saveFormToCookie(response, competitionId, selectionCookie);

            return createSuccessfulResponseWithSelectionStatus(selectionCookie.getFundingNotificationSelectionForm().getIds().size(), selectionCookie.getFundingNotificationSelectionForm().isAllSelected(), limitIsExceeded);
        } catch (Exception e) {
            log.error("exception thrown selecting application for email list", e);
            return createFailureResponse();
        }
    }

    private void handleSelected(FundingNotificationSelectionCookie selectionCookie, long competitionId, long applicationId) {
        List<Long> applicationIds = selectionCookie.getFundingNotificationSelectionForm().getIds();

        if (!applicationIds.contains(applicationId)) {
            selectionCookie.getFundingNotificationSelectionForm().getIds().add(applicationId);
            List<Long> filteredApplicationList = getAllApplicationIdsByFilters(competitionId, selectionCookie.getFundingNotificationFilterForm());
            if (applicationIds.containsAll(filteredApplicationList)) {
                selectionCookie.getFundingNotificationSelectionForm().setAllSelected(true);
            }
        }
    }

    protected JsonNode addAllApplicationsToEmailList(Model model,
                                                     long competitionId,
                                                     boolean addAll,
                                                     HttpServletRequest request,
                                                     HttpServletResponse response) {
        checkCompetitionIsOpen(competitionId);
        try {
            FundingNotificationSelectionCookie selectionCookie = getSelectionFormFromCookie(request, competitionId).orElse(new FundingNotificationSelectionCookie());
            FundingNotificationSelectionForm applicationsForEmailForm = selectionCookie.getFundingNotificationSelectionForm();

            if (addAll) {
                applicationsForEmailForm.setIds(getAllApplicationIdsByFilters(competitionId, selectionCookie.getFundingNotificationFilterForm()));
                applicationsForEmailForm.setAllSelected(true);
            } else {
                applicationsForEmailForm.getIds().clear();
                applicationsForEmailForm.setAllSelected(false);
            }

            saveFormToCookie(response, competitionId, selectionCookie);
            return createSuccessfulResponseWithSelectionStatus(selectionCookie.getFundingNotificationSelectionForm().getIds().size(), selectionCookie.getFundingNotificationSelectionForm().isAllSelected(), false);
        } catch (Exception e) {
            log.error("exception thrown adding applications to email list", e);

            return createFailureResponse();
        }
    }

    private List<Long> getAllApplicationIdsByFilters(long competitionId, FundingNotificationFilterForm filterForm) {
        return applicationSummaryRestService.getWithDecisionIsChangeableApplicationIdsByCompetitionId(
                competitionId,
                filterForm.getStringFilter().isEmpty() ? empty() : of(filterForm.getStringFilter()),
                filterForm.getSendFilter(),
                filterForm.getFundingFilter(),
                Optional.of(filterForm.isEoi())).getSuccess();

    }

    protected abstract String getManageFundingApplicationsPage(long competitionId);

    protected abstract String successfulEmailRedirect(long competitionId);

    protected abstract String composeEmailRedirect(long competitionId, List<Long> ids);

    protected abstract Supplier<String> queryFailureView(long competitionId);

    private Supplier<String> idsFailureView(long competitionId, FundingNotificationFilterForm query, Model model, HttpServletRequest request) {
        FundingNotificationSelectionCookie storedSelectionFormCookie = getSelectionFormFromCookie(request, competitionId).orElse(new FundingNotificationSelectionCookie());
        List<Long> ids = getAllApplicationIdsByFilters(competitionId, storedSelectionFormCookie.getFundingNotificationFilterForm());
        final long totalSubmittableApplications = ids.size();

        return () -> {
            model.addAttribute("model", manageApplicationDecisionsModelPopulator.populate(query, competitionId, totalSubmittableApplications));
            return MANAGE_FUNDING_APPLICATIONS_VIEW;
        };
    }

    private void checkCompetitionIsOpen(long competitionId) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        if (!competition.getCompetitionStatus().isLaterThan(CompetitionStatus.READY_TO_OPEN)) {
            throw new IncorrectStateForPageException("Competition is not yet open.");
        }
    }
}
