package org.innovateuk.ifs.management.decision.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.decision.form.FundingNotificationFilterForm;
import org.innovateuk.ifs.management.decision.form.FundingNotificationSelectionCookie;
import org.innovateuk.ifs.management.decision.form.FundingNotificationSelectionForm;
import org.innovateuk.ifs.management.decision.form.NotificationEmailsForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/competition/{competitionId}/eoi/notification")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = CompetitionManagementEOINotificationsController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'support', 'innovation_lead', 'stakeholder')")
public class CompetitionManagementEOINotificationsController extends CompetitionManagementNotificationsController {

    protected String getCookieName() {
        return "eoiSelectionForm";
    }

    protected Class<FundingNotificationSelectionCookie> getFormType() {
        return FundingNotificationSelectionCookie.class;
    }

    protected String getManageFundingApplicationsPage(long competitionId){
        return "/competition/" + competitionId + "/eoi/notification";
    }

    protected String successfulEmailRedirect(long competitionId) {
        return "redirect:" + getManageFundingApplicationsPage(competitionId);
    }

    protected String composeEmailRedirect(long competitionId, List<Long> ids) {
        String idParameters = ids.stream().map(Object::toString).collect(Collectors.joining(","));
        return "redirect:/competition/" + competitionId + "/eoi/notification/send?application_ids=" + idParameters;
    }

    protected Supplier<String> queryFailureView(long competitionId) {
        return () -> "redirect:/competition/" + competitionId + "/applications/eoi";
    }

    @GetMapping("/send")
    public String sendNotifications(Model model,
                               @PathVariable("competitionId") Long competitionId,
                               @RequestParam("application_ids") List<Long> applicationIds) {
        return super.sendNotifications(model,
                competitionId,
                applicationIds,
                true);
    }

    @PostMapping("/send")
    public String sendNotificationsSubmit(Model model,
                                    @PathVariable("competitionId") long competitionId,
                                    @ModelAttribute("form") @Valid NotificationEmailsForm form,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler) {
        return super.sendNotificationsSubmit(model,
                competitionId,
                form,
                bindingResult,
                validationHandler,
                true);
    }

    @GetMapping("")
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
        filterForm.setEoi(true);
        return super.applications(model,
                params,
                competitionId,
                filterForm,
                selectionForm,
                filterChanged,
                bindingResult,
                validationHandler,
                request,
                response);
    }

    @PostMapping("")
    public String selectApplications(Model model,
                                     @PathVariable("competitionId") Long competitionId,
                                     @ModelAttribute @Valid FundingNotificationFilterForm query,
                                     ValidationHandler queryFormValidationHandler,
                                     @ModelAttribute("form") @Valid FundingNotificationSelectionForm selectionForm,
                                     BindingResult idsBindingResult,
                                     ValidationHandler idsValidationHandler,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        return super.selectApplications(model,
                competitionId,
                query,
                queryFormValidationHandler,
                selectionForm,
                idsBindingResult,
                idsValidationHandler,
                request,
                response);
    }

    @PostMapping(value = "", params = {"selectionId"})
    public @ResponseBody JsonNode selectApplicationForEmailList(
            @PathVariable("competitionId") long competitionId,
            @RequestParam("selectionId") long applicationId,
            @RequestParam("isSelected") boolean isSelected,
            HttpServletRequest request,
            HttpServletResponse response) {
        return super.selectApplicationForEmailList(
                competitionId,
                applicationId,
                isSelected,
                request,
                response);
    }

    @PostMapping(value = "", params = {"addAll"})
    public @ResponseBody JsonNode addAllApplicationsToEmailList(Model model,
                                           @PathVariable("competitionId") long competitionId,
                                           @RequestParam("addAll") boolean addAll,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        return super.addAllApplicationsToEmailList(model,
                competitionId,
                addAll,
                request,
                response);
    }

}
