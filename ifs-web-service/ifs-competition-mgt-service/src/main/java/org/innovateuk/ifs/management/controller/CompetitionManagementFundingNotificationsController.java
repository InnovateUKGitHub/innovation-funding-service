package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.application.service.ApplicationFundingDecisionService;
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

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@Controller
@RequestMapping("/competition/{competitionId}")
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionManagementFundingNotificationsController {


    private static final String MANAGE_FUNDING_APPLICATIONS_VIEW = "comp-mgt-manage-funding-applications";
    private static final String FUNDING_DECISION_NOTIFICATION_VIEW = "comp-mgt-send-notifications";


    @Autowired
    private ManageFundingApplicationsModelPopulator manageFundingApplicationsModelPopulator;

    @Autowired
    private SendNotificationsModelPopulator sendNotificationsModelPopulator;

    @Autowired
    private ApplicationFundingDecisionService applicationFundingService;

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
                               BindingResult bindingResult,
                               ValidationHandler validationHandler) {
        return validationHandler.failNowOrSucceedWith(queryFailureView(competitionId), () -> {
                    model.addAttribute("model", manageFundingApplicationsModelPopulator.populate(query, competitionId, buildQueryString(params)));
                    model.addAttribute("form", new SelectApplicationsForEmailForm());
                    return MANAGE_FUNDING_APPLICATIONS_VIEW;
                }
        );

    }

    @PostMapping("/manage-funding-applications")
    public String selectApplications(Model model,
                                     @RequestParam MultiValueMap<String, String> params,
                                     @PathVariable("competitionId") Long competitionId,
                                     @ModelAttribute @Valid ManageFundingApplicationsQueryForm query,
                                     BindingResult queryFormBindingResult,
                                     ValidationHandler queryFormValidationHandler,
                                     @ModelAttribute("form") @Valid SelectApplicationsForEmailForm ids,
                                     BindingResult idsBindingResult,
                                     ValidationHandler idsValidationHandler) {
        return queryFormValidationHandler.failNowOrSucceedWith(queryFailureView(competitionId),  // Pass or fail JSR 303 on the query form
                () -> idsValidationHandler.failNowOrSucceedWith(idsFailureView(competitionId, query, model, params), // Pass or fail JSR 303 on the ids
                        () -> {
                            // Custom validation
                            List<Long> applicationIds = ids.getIds().stream().map(this::toLongOrNull).filter(Objects::nonNull).collect(toList());
                            if (applicationIds.isEmpty()) {
                                idsBindingResult.rejectValue("ids", "validation.manage.funding.applications.no.application.selected");
                            }
                            return idsValidationHandler.failNowOrSucceedWith(idsFailureView(competitionId, query, model, params), // Pass or fail custom validation
                                    () -> composeEmailRedirect(competitionId, applicationIds));
                        }
                )
        );
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


}
