package org.innovateuk.ifs.management.registration.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.MonitoringOfficerRegistrationRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerInviteResource;
import org.innovateuk.ifs.management.registration.service.MonitoringOfficerService;
import org.innovateuk.ifs.registration.form.RegistrationForm;
import org.innovateuk.ifs.registration.form.RegistrationForm.ExternalUserRegistrationValidationGroup;
import org.innovateuk.ifs.registration.viewmodel.RegistrationViewModel.RegistrationViewModelBuilder;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.NavigationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.groups.Default;
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
    private MonitoringOfficerRegistrationRestService competitionSetupMonitoringOfficerRestService;
    @Autowired
    private MonitoringOfficerService monitoringOfficerService;
    @Autowired
    private NavigationUtils navigationUtils;

    @GetMapping("/{inviteHash}/register")
    public String openInvite(@PathVariable("inviteHash") String inviteHash,
                             Model model,
                             HttpServletRequest request,
                             @ModelAttribute("form") RegistrationForm form,
                             UserResource loggedInUser) {

        if (loggedInUser != null) {
            if (!competitionSetupMonitoringOfficerRestService.getMonitoringOfficerInvite(inviteHash).getSuccess().getEmail().equalsIgnoreCase(loggedInUser.getEmail())) {
                return "registration/error";
            }
        }

        MonitoringOfficerInviteResource monitoringOfficerInviteResource = competitionSetupMonitoringOfficerRestService.openMonitoringOfficerInvite(inviteHash).getSuccess();
        form.setEmail(monitoringOfficerInviteResource.getEmail());
        model.addAttribute("model", RegistrationViewModelBuilder.aRegistrationViewModel().withExternalUser(true).withInvitee(true).build());
        return "registration/register";
    }


    private String dashboardRedirect(HttpServletRequest request) {
        return navigationUtils.getRedirectToLandingPageUrl(request);
    }

    @PostMapping("/{inviteHash}/register")
    public String submitDetails(Model model,
                                    @PathVariable("inviteHash") String inviteHash,
                                    @ModelAttribute(FORM_ATTR_NAME) @Validated({Default.class, ExternalUserRegistrationValidationGroup.class}) RegistrationForm monitoringOfficerRegistrationForm,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler,
                                    UserResource loggedInUser) {

        Supplier<String> failureView = () -> doViewYourDetails(model, inviteHash, loggedInUser);

        if (loggedInUser != null) {
            return failureView.get();
        }
        else {
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                ServiceResult<Void> result = monitoringOfficerService.activateAndUpdateMonitoringOfficer(inviteHash,
                                                                                                         monitoringOfficerRegistrationForm);
                result.getErrors().forEach(error -> {
                    if (StringUtils.hasText(error.getFieldName())) {
                        bindingResult.rejectValue(error.getFieldName(), "registration." + error.getErrorKey());
                    } else {
                        bindingResult.reject("registration." + error.getErrorKey());
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
            model.addAttribute("model", RegistrationViewModelBuilder.aRegistrationViewModel().withExternalUser(true).withInvitee(true).build());
            return "registration/register";
        }
    }
}