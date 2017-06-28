package org.innovateuk.ifs.management.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.application.service.ApplicationFundingDecisionService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.competition.form.FundingNotificationSelectionCookie;
import org.innovateuk.ifs.competition.form.ManageFundingApplicationsQueryForm;
import org.innovateuk.ifs.competition.form.NotificationEmailsForm;
import org.innovateuk.ifs.competition.form.SelectApplicationsForEmailForm;
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
                               @ModelAttribute @Valid ManageFundingApplicationsQueryForm filterForm,
                               @ModelAttribute(name = "selectionForm", binding = false) @Valid SelectApplicationsForEmailForm selectionForm,
                               @RequestParam(value = "clearFilters", defaultValue = "false") boolean clearFilters,
                               BindingResult bindingResult,
                               ValidationHandler validationHandler,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        updateSelectionForm(request, response, competitionId, selectionForm, filterForm, clearFilters);
        List<Long> submittableApplications = getAllApplicationIdsByFilters(competitionId, filterForm);

        return validationHandler.failNowOrSucceedWith(queryFailureView(competitionId), () -> {
                    model.addAttribute("model", manageFundingApplicationsModelPopulator.populate(filterForm, competitionId, buildQueryString(params), submittableApplications.size()));
                    return MANAGE_FUNDING_APPLICATIONS_VIEW; }
        );
    }

    private void updateSelectionForm(HttpServletRequest request,
                                     HttpServletResponse response,
                                     long competitionId,
                                     SelectApplicationsForEmailForm appSelectionForm,
                                     ManageFundingApplicationsQueryForm filterForm,
                                     boolean clearFilters) {
        FundingNotificationSelectionCookie storedSelectionFormCookie = getSelectionFormFromCookie(request, competitionId).orElse(new FundingNotificationSelectionCookie());
        ManageFundingApplicationsQueryForm storedFilterForm = storedSelectionFormCookie.getManageFundingApplicationsQueryForm();
        SelectApplicationsForEmailForm storedAppSelectionForm = storedSelectionFormCookie.getSelectApplicationsForEmailForm();
        clearFilters = !filterForm.anyFilterOptionsActive();

        if (!storedAppSelectionForm.getIds().isEmpty()) {
            appSelectionForm.setAllSelected(storedAppSelectionForm.isAllSelected());
            appSelectionForm.setIds(storedAppSelectionForm.getIds());
        }
        if (storedFilterForm.anyFilterOptionsActive() && !filterForm.anyFilterOptionsActive() && !clearFilters) {
            filterForm.setAllFilterOptions(storedFilterForm.getStringFilter(), storedFilterForm.getSendFilter(), storedFilterForm.getFundingFilter());
        }

        List<Long> filteredIds = getAllApplicationIdsByFilters(competitionId, filterForm);
        if (appSelectionForm.isAllSelected() && !filterForm.anyFilterOptionsActive() && !clearFilters) {
            appSelectionForm.setIds(filteredIds);
            appSelectionForm.setAllSelected(true);
        } else {
            appSelectionForm.getIds().retainAll(filteredIds);
            if (appSelectionForm.getIds().containsAll(filteredIds)) {
                appSelectionForm.setAllSelected(true);
            } else {
                appSelectionForm.setAllSelected(false);
            }
        }
        storedSelectionFormCookie.setManageFundingApplicationsQueryForm(filterForm);
        storedSelectionFormCookie.setSelectApplicationsForEmailForm(appSelectionForm);

        saveFormToCookie(response, competitionId, storedSelectionFormCookie);

    }

    @PostMapping("/manage-funding-applications")
    public String selectApplications(Model model,
                                     @RequestParam MultiValueMap<String, String> params,
                                     @PathVariable("competitionId") Long competitionId,
                                     @ModelAttribute @Valid ManageFundingApplicationsQueryForm query,
                                     ValidationHandler queryFormValidationHandler,
                                     @ModelAttribute("form") @Valid SelectApplicationsForEmailForm selectionForm,
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
                            if (selectionCookie.getSelectApplicationsForEmailForm().getIds().isEmpty()) {
                                idsBindingResult.rejectValue("ids", "validation.manage.funding.applications.no.application.selected");
                            }
                            return idsValidationHandler.failNowOrSucceedWith(idsFailureView(competitionId, query, model, params, request), // Pass or fail custom validation
                                    () -> {
                                        removeCookie(response, competitionId);
                                        return composeEmailRedirect(competitionId, selectionCookie.getSelectApplicationsForEmailForm().getIds());
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
                int predictedSize = selectionCookie.getSelectApplicationsForEmailForm().getIds().size() + 1;
                if(limitIsExceeded(predictedSize)) {
                    limitIsExceeded = true;
                }
                else {
                    handleSelected(selectionCookie, competitionId, applicationId);
                }
            } else {
                selectionCookie.getSelectApplicationsForEmailForm().getIds().remove(applicationId);
                selectionCookie.getSelectApplicationsForEmailForm().setAllSelected(false);
            }
            saveFormToCookie(response, competitionId, selectionCookie);

            return createSuccessfulResponseWithSelectionStatus(selectionCookie.getSelectApplicationsForEmailForm().getIds().size(), selectionCookie.getSelectApplicationsForEmailForm().isAllSelected(), limitIsExceeded);
        } catch (Exception e) {
            return createFailureResponse();
        }
    }

    private void handleSelected(FundingNotificationSelectionCookie selectionCookie, long competitionId, long applicationId) {
        List<Long> applicationIds = selectionCookie.getSelectApplicationsForEmailForm().getIds();

        if (!applicationIds.contains(applicationId)) {
            selectionCookie.getSelectApplicationsForEmailForm().getIds().add(applicationId);
            List<Long> filteredApplicationList = getAllApplicationIdsByFilters(competitionId, selectionCookie.getManageFundingApplicationsQueryForm());
            if (applicationIds.containsAll(filteredApplicationList)) {
                selectionCookie.getSelectApplicationsForEmailForm().setAllSelected(true);
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
            SelectApplicationsForEmailForm applicationsForEmailForm = selectionCookie.getSelectApplicationsForEmailForm();

            if (addAll) {
                applicationsForEmailForm.setIds(getAllApplicationIdsByFilters(competitionId, selectionCookie.getManageFundingApplicationsQueryForm()));
                applicationsForEmailForm.setAllSelected(true);
            } else {
                applicationsForEmailForm.getIds().clear();
                applicationsForEmailForm.setAllSelected(false);
            }

            saveFormToCookie(response, competitionId, selectionCookie);
            return createSuccessfulResponseWithSelectionStatus(selectionCookie.getSelectApplicationsForEmailForm().getIds().size(), selectionCookie.getSelectApplicationsForEmailForm().isAllSelected(), false);
        } catch (Exception e) {
            return createFailureResponse();
        }
    }

    private List<Long> getAllApplicationIdsByFilters(long competitionId, ManageFundingApplicationsQueryForm filterForm) {
        List<ApplicationSummaryResource> resources = applicationSummaryRestService.getWithFundingDecisionApplications(
                competitionId, filterForm.getStringFilter().isEmpty() ? empty() : of(filterForm.getStringFilter()),
                filterForm.getSendFilter(), filterForm.getFundingFilter()).getSuccessObjectOrThrowException();

        return resources.stream().filter(ApplicationSummaryResource::applicationFundingDecisionIsChangeable).map(
                ApplicationSummaryResource::getId).collect(toList());
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

    private Supplier<String> idsFailureView(long competitionId, ManageFundingApplicationsQueryForm query, Model model, MultiValueMap<String, String> params, HttpServletRequest request) {
        FundingNotificationSelectionCookie storedSelectionFormCookie = getSelectionFormFromCookie(request, competitionId).orElse(new FundingNotificationSelectionCookie());
        List<Long> ids = getAllApplicationIdsByFilters(competitionId, storedSelectionFormCookie.getManageFundingApplicationsQueryForm());
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
