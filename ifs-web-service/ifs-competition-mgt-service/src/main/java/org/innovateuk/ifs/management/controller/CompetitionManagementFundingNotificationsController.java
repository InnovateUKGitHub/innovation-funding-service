package org.innovateuk.ifs.management.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.application.service.ApplicationFundingDecisionService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.competition.form.*;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.model.ManageFundingApplicationsModelPopulator;
import org.innovateuk.ifs.management.model.SendNotificationsModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;


@Controller
@RequestMapping("/competition/{competitionId}")
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionManagementFundingNotificationsController extends CompetitionManagementCookieController<FundingNotificationSelectionCookie> {

    private static final String MANAGE_FUNDING_APPLICATIONS_VIEW = "comp-mgt-manage-funding-applications";
    private static final String FUNDING_DECISION_NOTIFICATION_VIEW = "comp-mgt-send-notifications";

    @Autowired
    private ManageFundingApplicationsModelPopulator manageFundingApplicationsModelPopulator;

    @Autowired
    private SendNotificationsModelPopulator sendNotificationsModelPopulator;

    @Autowired
    private ApplicationFundingDecisionService applicationFundingService;

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    protected String getCookieName() {
        return "applicationSelectionForm";
    }

    protected Class<FundingNotificationSelectionCookie> getFormType() {
        return FundingNotificationSelectionCookie.class;
    }

    @GetMapping("/funding/send")
    public String sendNotifications(Model model,
                               @PathVariable("competitionId") Long competitionId,
                               @RequestParam("application_ids") List<Long> applicationIds) {

        NotificationEmailsForm form = new NotificationEmailsForm();
        return getFundingDecisionPage(model, form, competitionId, applicationIds);
    }

    @PostMapping("/funding/send")
    public String sendNotificationsSubmit(Model model,
                                    @PathVariable("competitionId") long competitionId,
                                    @ModelAttribute("form") @Valid NotificationEmailsForm form,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler) {

        FundingNotificationResource fundingNotificationResource = new FundingNotificationResource(form.getMessage(), form.getFundingDecisions());

        Supplier<String> failureView = () -> getFundingDecisionPage(model, form, competitionId, form.getApplicationIds());
        Supplier<String> successView = () -> successfulEmailRedirect(competitionId);

        return validationHandler.performActionOrBindErrorsToField("", failureView, successView,
                () -> applicationFundingService.sendFundingNotifications(fundingNotificationResource));
    }

    private String getFundingDecisionPage(Model model, NotificationEmailsForm form, long competitionId, List<Long> applicationIds) {
        model.addAttribute("model", sendNotificationsModelPopulator.populate(competitionId, applicationIds));
        model.addAttribute("form", form);
        return FUNDING_DECISION_NOTIFICATION_VIEW;
    }

    @GetMapping("/manage-funding-applications")
    public String applications(Model model,
                               @RequestParam MultiValueMap<String, String> params,
                               @PathVariable("competitionId") Long competitionId,
                               @ModelAttribute @Valid FundingNotificationFilterForm filterForm,
                               @ModelAttribute(name = "selectionForm") @Valid FundingNotificationSelectionForm selectionForm,
                               @RequestParam(value = "filterChanged", required = false) boolean filterChanged,
                               BindingResult bindingResult,
                               ValidationHandler validationHandler,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        updateSelectionForm(request,
                response,
                competitionId,
                selectionForm,
                filterForm,
                filterChanged);

        List<Long> submittableApplications = getAllApplicationIdsByFilters(competitionId, filterForm);
        return validationHandler.failNowOrSucceedWith(queryFailureView(competitionId), () -> {
                model.addAttribute("model", manageFundingApplicationsModelPopulator.populate(filterForm, competitionId, buildQueryString(params), submittableApplications.size()));
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
        modelFilterForm.setAllFilterOptions(updatedFilterForm.getStringFilter(),  updatedFilterForm.getSendFilter(), updatedFilterForm.getFundingFilter());

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
            modelFilterForm.setAllFilterOptions(storedFilterForm.getStringFilter(), storedFilterForm.getSendFilter(), storedFilterForm.getFundingFilter());
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

    @PostMapping("/manage-funding-applications")
    public String selectApplications(Model model,
                                     @RequestParam MultiValueMap<String, String> params,
                                     @PathVariable("competitionId") Long competitionId,
                                     @ModelAttribute @Valid FundingNotificationFilterForm query,
                                     ValidationHandler queryFormValidationHandler,
                                     @ModelAttribute("form") @Valid FundingNotificationSelectionForm selectionForm,
                                     BindingResult idsBindingResult,
                                     ValidationHandler idsValidationHandler,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {

        FundingNotificationSelectionCookie selectionCookie = getSelectionFormFromCookie(request, competitionId)
                .orElse(new FundingNotificationSelectionCookie(selectionForm));

        return queryFormValidationHandler.failNowOrSucceedWith(queryFailureView(competitionId),  // Pass or fail JSR 303 on the query form
                () -> idsValidationHandler.failNowOrSucceedWith(idsFailureView(competitionId, query, model, params, request), // Pass or fail JSR 303 on the ids
                        () -> {
                            // Custom validation
                            if (selectionCookie.getFundingNotificationSelectionForm().getIds().isEmpty()) {
                                idsBindingResult.rejectValue("ids", "validation.manage.funding.applications.no.application.selected");
                            }
                            return idsValidationHandler.failNowOrSucceedWith(idsFailureView(competitionId, query, model, params, request), // Pass or fail custom validation
                                    () -> {
                                        removeCookie(response, competitionId);
                                        return composeEmailRedirect(competitionId, selectionCookie.getFundingNotificationSelectionForm().getIds());
                                    });
                        }
                )
        );
    }

    @PostMapping(value = "/manage-funding-applications", params = {"selectionId"})
    public @ResponseBody JsonNode selectApplicationForEmailList(
            @PathVariable("competitionId") long competitionId,
            @RequestParam("selectionId") long applicationId,
            @RequestParam("isSelected") boolean isSelected,
            HttpServletRequest request,
            HttpServletResponse response) {

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

    @PostMapping(value = "/manage-funding-applications", params = {"addAll"})
    public @ResponseBody JsonNode addAllApplicationsToEmailList(Model model,
                                           @PathVariable("competitionId") long competitionId,
                                           @RequestParam("addAll") boolean addAll,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
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
            return createFailureResponse();
        }
    }

    private List<Long> getAllApplicationIdsByFilters(long competitionId, FundingNotificationFilterForm filterForm) {
        return applicationSummaryRestService.getWithFundingDecisionIsChangeableApplicationIdsByCompetitionId(
                competitionId, filterForm.getStringFilter().isEmpty() ? empty() : of(filterForm.getStringFilter()),
                filterForm.getSendFilter(), filterForm.getFundingFilter()).getSuccessObjectOrThrowException();

    }

    private String getManageFundingApplicationsPage(long competitionId){
        return "/competition/" + competitionId + "/manage-funding-applications";
    }

    private String successfulEmailRedirect(long competitionId) {
        return "redirect:" + getManageFundingApplicationsPage(competitionId);
    }

    private String composeEmailRedirect(long competitionId, List<Long> ids) {
        String idParameters = ids.stream().map(Object::toString).collect(Collectors.joining(","));
        return "redirect:/competition/" + competitionId + "/funding/send?application_ids=" + idParameters;
    }

    private Supplier<String> queryFailureView(long competitionId) {
        return () -> "redirect:/competition/" + competitionId + "/funding";
    }

    private Supplier<String> idsFailureView(long competitionId, FundingNotificationFilterForm query, Model model, MultiValueMap<String, String> params, HttpServletRequest request) {
        FundingNotificationSelectionCookie storedSelectionFormCookie = getSelectionFormFromCookie(request, competitionId).orElse(new FundingNotificationSelectionCookie());
        List<Long> ids = getAllApplicationIdsByFilters(competitionId, storedSelectionFormCookie.getFundingNotificationFilterForm());
        final long totalSubmittableApplications = ids.size();

        return () -> {
            model.addAttribute("model", manageFundingApplicationsModelPopulator.populate(query, competitionId, buildQueryString(params), totalSubmittableApplications));
            return "comp-mgt-manage-funding-applications";
        };
    }

    private String buildQueryString(MultiValueMap<String, String> params){
        return UriComponentsBuilder.newInstance()
                .queryParams(params)
                .build()
                .encode()
                .toUriString();
    }
}
