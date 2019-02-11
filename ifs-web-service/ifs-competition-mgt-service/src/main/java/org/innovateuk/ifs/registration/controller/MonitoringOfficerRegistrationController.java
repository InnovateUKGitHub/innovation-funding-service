package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.CompetitionSetupMonitoringOfficerRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerInviteResource;
import org.innovateuk.ifs.registration.form.MonitoringOfficerRegistrationForm;
import org.innovateuk.ifs.registration.populator.MonitoringOfficerRegistrationModelPopulator;
import org.innovateuk.ifs.registration.service.MonitoringOfficerService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.NavigationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;

/**
 * Controller to manage registration of monitoring officer users
 */
@Controller
@RequestMapping("/monitoring-officer")
@SecuredBySpring(value = "Controller",
        description = "Anyone can register for an account, if they have the invite hash",
        securedType = MonitoringOfficerRegistrationController.class)
@PreAuthorize("permitAll")
public class MonitoringOfficerRegistrationController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private MonitoringOfficerRegistrationModelPopulator monitoringOfficerRegistrationModelPopulator;

    @Autowired
    private CompetitionSetupMonitoringOfficerRestService competitionSetupMonitoringOfficerRestService;

    @Autowired
    private MonitoringOfficerService monitoringOfficerService;

    @Autowired
    private NavigationUtils navigationUtils;

    public MonitoringOfficerRegistrationController(MonitoringOfficerRegistrationModelPopulator monitoringOfficerRegistrationModelPopulator,
                                                   CompetitionSetupMonitoringOfficerRestService competitionSetupMonitoringOfficerRestService,
                                                   MonitoringOfficerService monitoringOfficerService) {
        this.monitoringOfficerRegistrationModelPopulator = monitoringOfficerRegistrationModelPopulator;
        this.competitionSetupMonitoringOfficerRestService = competitionSetupMonitoringOfficerRestService;
        this.monitoringOfficerService = monitoringOfficerService;
    }


    @GetMapping("/{inviteHash}/register")
    public String openInvite(@PathVariable("inviteHash") String inviteHash,
                             Model model,
                             HttpServletRequest request,
                             @ModelAttribute("form") MonitoringOfficerRegistrationForm monitoringOfficerRegistrationForm) { return competitionSetupMonitoringOfficerRestService.checkExistingUser(inviteHash).andOnSuccessReturn(
                userExists -> {
                    if (userExists) {
                        addMonitoringOfficerRole(inviteHash);
                        return dashboardRedirect(request);
                    } else {
                        MonitoringOfficerInviteResource monitoringOfficerInviteResource = competitionSetupMonitoringOfficerRestService.getMonitoringOfficerInvite(inviteHash).getSuccess();
                        model.addAttribute("model", monitoringOfficerRegistrationModelPopulator.populateModel(monitoringOfficerInviteResource.getEmail()));
                        return "monitoring-officer/create-account";
                    }
                }).getSuccess();
    }

    private void addMonitoringOfficerRole(String inviteHash) {
        competitionSetupMonitoringOfficerRestService.addMonitoringOfficerRole(inviteHash);
    }

    private  String dashboardRedirect(HttpServletRequest request) {
        return navigationUtils.getRedirectToLandingPageUrl(request);
    }

    // existing user and not logged in
    @PostMapping("/{inviteHash}/register")
    public String submitDetails(Model model,
                                    @PathVariable("inviteHash") String inviteHash,
                                    @Valid @ModelAttribute(FORM_ATTR_NAME) MonitoringOfficerRegistrationForm monitoringOfficerRegistrationForm,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler,
                                    UserResource loggedInUser) {

        Supplier<String> failureView = () -> doViewYourDetails(model, inviteHash, loggedInUser);

        if (loggedInUser != null) {
            return failureView.get();
        }
        else {
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                ServiceResult<Void> result = monitoringOfficerService.createMonitoringOfficer(inviteHash, monitoringOfficerRegistrationForm);
                result.getErrors().forEach(error -> {
                    if (StringUtils.hasText(error.getFieldName())) {
                        bindingResult.rejectValue(error.getFieldName(), "monitoring-officer." + error.getErrorKey());
                    } else {
                        bindingResult.reject("monitoring-officer." + error.getErrorKey());
                    }
                });
                return validationHandler.
                        failNowOrSucceedWith(failureView,
                                () -> format("redirect:/monitoring-officer/%s/register/account-created", inviteHash));
            });
        }
    }

    @GetMapping(value = "/{inviteHash}/register/account-created")
    public String accountCreated(@PathVariable("inviteHash") String inviteHash, HttpServletRequest request, UserResource loggedInUser) {
        boolean userIsLoggedIn = loggedInUser != null;

        if (userIsLoggedIn) {
            return dashboardRedirect(request);
        }

        return competitionSetupMonitoringOfficerRestService.getMonitoringOfficerInvite(inviteHash).andOnSuccessReturn(invite -> {
            if (InviteStatus.OPENED != invite.getStatus()) {
                return format("redirect:/monitoring-officer/%s/register", inviteHash);
            } else {
                return "registration/account-created";
            }
        }).getSuccess();
    }

    private String doViewYourDetails(Model model, String inviteHash, UserResource loggedInUser) {
        if (loggedInUser != null) {
            return "registration/error";
        } else {
            MonitoringOfficerInviteResource monitoringOfficerInviteResource = competitionSetupMonitoringOfficerRestService.getMonitoringOfficerInvite(inviteHash).getSuccess();
            model.addAttribute("model", monitoringOfficerRegistrationModelPopulator.populateModel(monitoringOfficerInviteResource.getEmail()));
            return "monitoring-officer/create-account";
        }
    }
}