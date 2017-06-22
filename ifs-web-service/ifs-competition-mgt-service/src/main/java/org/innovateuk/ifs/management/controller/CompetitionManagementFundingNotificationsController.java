package org.innovateuk.ifs.management.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.application.service.ApplicationFundingDecisionService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.competition.form.ManageFundingApplicationsQueryForm;
import org.innovateuk.ifs.competition.form.NotificationEmailsForm;
import org.innovateuk.ifs.competition.form.SelectApplicationsForEmailForm;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.model.ManageFundingApplicationsModelPopulator;
import org.innovateuk.ifs.management.model.SendNotificationsModelPopulator;
import org.innovateuk.ifs.util.CookieUtil;
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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.JsonUtil.getObjectFromJson;
import static org.innovateuk.ifs.util.JsonUtil.getSerializedObject;


@Controller
@RequestMapping("/competition/{competitionId}")
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionManagementFundingNotificationsController {

    private static final String MANAGE_FUNDING_APPLICATIONS_VIEW = "comp-mgt-manage-funding-applications";
    private static final String FUNDING_DECISION_NOTIFICATION_VIEW = "comp-mgt-send-notifications";
    private static final String SELECTION_FORM = "applicationSelectionForm";

    @Autowired
    private ManageFundingApplicationsModelPopulator manageFundingApplicationsModelPopulator;

    @Autowired
    private SendNotificationsModelPopulator sendNotificationsModelPopulator;

    @Autowired
    private ApplicationFundingDecisionService applicationFundingService;

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Autowired
    private CookieUtil cookieUtil;


    @GetMapping("/funding/send")
    public String sendNotifications(Model model,
                               @PathVariable("competitionId") Long competitionId,
                               @RequestParam("application_ids") List<Long> applicationIds) {

        NotificationEmailsForm form = new NotificationEmailsForm();
        return getFundingDecisionPage(model, form, competitionId, applicationIds);
    }

    @PostMapping("/funding/send")
    public String sendNotificationsSubmit(Model model,
                                    @PathVariable("competitionId") Long competitionId,
                                    @ModelAttribute("form") @Valid NotificationEmailsForm form,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler) {

        FundingNotificationResource fundingNotificationResource = new FundingNotificationResource(form.getMessage(), form.getFundingDecisions());

        Supplier<String> failureView = () -> getFundingDecisionPage(model, form, competitionId, form.getApplicationIds());
        Supplier<String> successView = () -> successfulEmailRedirect(competitionId);

        return validationHandler.performActionOrBindErrorsToField("", failureView, successView,
                () -> applicationFundingService.sendFundingNotifications(fundingNotificationResource));
    }

    private String getFundingDecisionPage(Model model, NotificationEmailsForm form, Long competitionId, List<Long> applicationIds) {
        model.addAttribute("model", sendNotificationsModelPopulator.populate(competitionId, applicationIds));
        model.addAttribute("form", form);
        return FUNDING_DECISION_NOTIFICATION_VIEW;
    }

    @GetMapping("/manage-funding-applications")
    public String applications(Model model,
                               @RequestParam MultiValueMap<String, String> params,
                               @PathVariable("competitionId") Long competitionId,
                               @ModelAttribute @Valid ManageFundingApplicationsQueryForm query,
                               @ModelAttribute(name = "selectionForm", binding = false) @Valid SelectApplicationsForEmailForm selectionForm,
                               @RequestParam(value = "clearFilters", defaultValue = "false") boolean clearFilters,
                               BindingResult bindingResult,
                               ValidationHandler validationHandler,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        updateSelectionForm(request, response, competitionId, selectionForm, query, clearFilters);
        return validationHandler.failNowOrSucceedWith(queryFailureView(competitionId), () -> {
                    model.addAttribute("model", manageFundingApplicationsModelPopulator.populate(query, competitionId, buildQueryString(params)));
                    return MANAGE_FUNDING_APPLICATIONS_VIEW;
                }
        );
    }

    private void updateSelectionForm(HttpServletRequest request,
                                     HttpServletResponse response,
                                     long competitionId,
                                     SelectApplicationsForEmailForm selectionForm,
                                     ManageFundingApplicationsQueryForm filterForm,
                                     boolean clearFilters) {
        SelectApplicationsForEmailForm storedSelectionForm = getApplicationsSelectionFormFromCookie(request, competitionId).orElse(new SelectApplicationsForEmailForm());
        selectionForm.setAllSelected(storedSelectionForm.isAllSelected());
        selectionForm.setIds(storedSelectionForm.getIds());
        if ((storedSelectionForm.getStringFilter() != null ||
                storedSelectionForm.getSendFilter() != null ||
                storedSelectionForm.getFundingFilter() != null )
                && !clearFilters) {
            filterForm.setStringFilter(storedSelectionForm.getStringFilter());
            filterForm.setSendFilter(of(storedSelectionForm.getSendFilter()));
            filterForm.setFundingFilter(of(storedSelectionForm.getFundingFilter()));
        }

        if (selectionForm.isAllSelected()) {
            selectionForm.setIds(getAllApplicationIds(competitionId, of(filterForm.getStringFilter()), filterForm.getSendFilter(), filterForm.getFundingFilter()));
        } else {
            selectionForm.getIds().retainAll(getAllApplicationIds(competitionId, of(filterForm.getStringFilter()), filterForm.getSendFilter(), filterForm.getFundingFilter()));
        }
        filterForm.getSendFilter().ifPresent(selectionForm::setSendFilter);
        filterForm.getFundingFilter().ifPresent(selectionForm::setFundingFilter);
        if (!filterForm.getStringFilter().isEmpty()) {
            selectionForm.setStringFilter(filterForm.getStringFilter());
        }
        cookieUtil.saveToCookie(response, format("%s_comp%s", SELECTION_FORM, competitionId), getSerializedObject(selectionForm));
    }

    @PostMapping("/manage-funding-applications")
    public String selectApplications(Model model,
                                     @RequestParam MultiValueMap<String, String> params,
                                     @PathVariable("competitionId") Long competitionId,
                                     @ModelAttribute @Valid ManageFundingApplicationsQueryForm query,
                                     BindingResult queryFormBindingResult,
                                     ValidationHandler queryFormValidationHandler,
                                     @ModelAttribute("form") @Valid SelectApplicationsForEmailForm selectionForm,
                                     BindingResult idsBindingResult,
                                     ValidationHandler idsValidationHandler,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {

        SelectApplicationsForEmailForm submittedSelectionForm = getApplicationsSelectionFormFromCookie(request, competitionId).orElse(selectionForm);

        return queryFormValidationHandler.failNowOrSucceedWith(queryFailureView(competitionId),  // Pass or fail JSR 303 on the query form
                () -> idsValidationHandler.failNowOrSucceedWith(idsFailureView(competitionId, query, model, params), // Pass or fail JSR 303 on the ids
                        () -> {
                            // Custom validation
                            List<Long> applicationIds = submittedSelectionForm.getIds().stream().map(this::toLongOrNull).filter(Objects::nonNull).collect(toList());
                            if (applicationIds.isEmpty()) {
                                idsBindingResult.rejectValue("ids", "validation.manage.funding.applications.no.application.selected");
                            }
                            return idsValidationHandler.failNowOrSucceedWith(idsFailureView(competitionId, query, model, params), // Pass or fail custom validation
                                    () -> {
                                        cookieUtil.removeCookie(response, format("%s_comp%s", SELECTION_FORM, competitionId));
                                        return composeEmailRedirect(competitionId, applicationIds);
                                    });
                        }
                )
        );
    }

    @PostMapping(value = "/manage-funding-applications", params = {"selectionId"})
    public @ResponseBody JsonNode selectApplicationForEmailList(
            @PathVariable("competitionId") long competitionId,
            @RequestParam("selectionId") String applicationId,
            @RequestParam("isSelected") boolean isSelected,
            @RequestParam(defaultValue = "0") int page,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            SelectApplicationsForEmailForm selectionForm = getApplicationsSelectionFormFromCookie(request, competitionId).orElse(new SelectApplicationsForEmailForm());
            if (isSelected) {
                selectionForm.getIds().add(applicationId);
            } else {
                selectionForm.getIds().remove(applicationId);
                selectionForm.setAllSelected(false);
            }
            cookieUtil.saveToCookie(response, format("%s_comp%s", SELECTION_FORM, competitionId), getSerializedObject(selectionForm));
            return createJsonObjectNode(selectionForm.getIds().size());
        } catch (Exception e) {
            return createJsonObjectNode(-1);
        }
    }

    @PostMapping(value = "/manage-funding-applications", params = {"addAll"})
    public @ResponseBody JsonNode addAllApplicationsToEmailList(Model model,
                                           @PathVariable("competitionId") long competitionId,
                                           @RequestParam("addAll") boolean addAll,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam Optional<String> stringFilter,
                                           @RequestParam Optional<Boolean> sendFilter,
                                           @RequestParam Optional<FundingDecision> fundingFilter,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        try {
            SelectApplicationsForEmailForm selectionForm = getApplicationsSelectionFormFromCookie(request, competitionId).orElse(new SelectApplicationsForEmailForm());

            if (addAll) {
                selectionForm.setIds(getAllApplicationIds(competitionId, stringFilter, sendFilter, fundingFilter));
                selectionForm.setAllSelected(true);
            } else {
                selectionForm.getIds().clear();
                selectionForm.setAllSelected(false);
            }

            cookieUtil.saveToCookie(response, format("%s_comp%s", SELECTION_FORM, competitionId), getSerializedObject(selectionForm));
            return createJsonObjectNode(selectionForm.getIds().size());
        } catch (Exception e) {
            return createJsonObjectNode(-1);
        }
    }

    private List<String> getAllApplicationIds(long competitionId,  Optional<String> stringFilter, Optional<Boolean> sendFilter, Optional<FundingDecision> fundingFilter) {
        List<ApplicationSummaryResource> resources =
                applicationSummaryRestService.getWithFundingDecisionApplications(competitionId, stringFilter, sendFilter, fundingFilter).getSuccessObjectOrThrowException();
        return resources.stream().filter(resource -> resource.applicationFundingDecisionIsChangeable()).map(
                resource -> resource.getId().toString()).collect(toList());
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

    private Supplier<String> idsFailureView(long competitionId, ManageFundingApplicationsQueryForm query, Model model, MultiValueMap<String, String> params) {
        return () -> {
            model.addAttribute("model", manageFundingApplicationsModelPopulator.populate(query, competitionId, buildQueryString(params)));
            return "comp-mgt-manage-funding-applications";
        };
    }

    private Long toLongOrNull(String value) {
        if (value != null)
        {
            try {
              return Long.parseLong(value);
            } catch (NumberFormatException e){
                return null;
            }
        }
        return null;
    }

    private String buildQueryString(MultiValueMap<String, String> params){
        return UriComponentsBuilder.newInstance()
                .queryParams(params)
                .build()
                .encode()
                .toUriString();
    }

    private ObjectNode createJsonObjectNode(int selectionCount) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("selectionCount", selectionCount);

        return node;
    }

    private Optional<SelectApplicationsForEmailForm> getApplicationsSelectionFormFromCookie(HttpServletRequest request, long competitionId) {
        String applicationFormJson = cookieUtil.getCookieValue(request, format("%s_comp%s", SELECTION_FORM, competitionId));
        if (isNotBlank(applicationFormJson)) {
            return Optional.ofNullable(getObjectFromJson(applicationFormJson, SelectApplicationsForEmailForm.class));
        } else {
            return Optional.empty();
        }
    }
}
