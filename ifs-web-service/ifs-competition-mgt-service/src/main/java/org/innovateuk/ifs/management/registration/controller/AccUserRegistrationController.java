package org.innovateuk.ifs.management.registration.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.AccUserRegistrationRestService;
import org.innovateuk.ifs.competition.service.MonitoringOfficerRegistrationRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.AccMonitoringOfficerInviteResource;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerInviteResource;
import org.innovateuk.ifs.management.registration.form.MonitoringOfficerRegistrationForm;
import org.innovateuk.ifs.management.registration.populator.MonitoringOfficerRegistrationModelPopulator;
import org.innovateuk.ifs.management.registration.service.AccUserService;
import org.innovateuk.ifs.management.registration.service.MonitoringOfficerService;
import org.innovateuk.ifs.management.registration.viewmodel.MonitoringOfficerRegistrationViewModel;
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
@RequestMapping("/acc-user")
@SecuredBySpring(value = "Controller",
        description = "Anyone can register for an account, if they have the invite hash",
        securedType = AccUserRegistrationController.class)
@PreAuthorize("permitAll")
public class AccUserRegistrationController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private NavigationUtils navigationUtils;

    @Autowired
    private AccUserRegistrationRestService accUserRegistrationRestService;

    @Autowired
    private AccUserService accUserService;


    @GetMapping("/{inviteHash}/register")
    public String openInvite(@PathVariable("inviteHash") String inviteHash,
                             Model model,
                             HttpServletRequest request,
                             @ModelAttribute("form") MonitoringOfficerRegistrationForm monitoringOfficerRegistrationForm,
                             UserResource loggedInUser) {
        return doViewYourDetails(model, inviteHash, loggedInUser);
    }


    private String dashboardRedirect(HttpServletRequest request) {
        return navigationUtils.getRedirectToLandingPageUrl(request);
    }

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
                ServiceResult<Void> result = accUserService.activateAndUpdateMonitoringOfficer(inviteHash,
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
                                () -> format("redirect:/acc-user/%s/register/account-created", inviteHash));
            });
        }
    }

    @GetMapping(value = "/{inviteHash}/register/account-created")
    public String accountCreated(@PathVariable("inviteHash") String inviteHash, HttpServletRequest request, UserResource loggedInUser) {
        boolean userIsLoggedIn = loggedInUser != null;

        if (userIsLoggedIn) {
            return dashboardRedirect(request);
        }

        return accUserRegistrationRestService.getAccMonitoringOfficerInvite(inviteHash).andOnSuccessReturn(invite -> {
            if (InviteStatus.OPENED != invite.getStatus()) {
                return format("redirect:/acc-user/%s/register", inviteHash);
            } else {
                return "registration/account-created";
            }
        }).getSuccess();
    }

    private String doViewYourDetails(Model model, String inviteHash, UserResource loggedInUser) {
        if (loggedInUser != null) {
            return "registration/error";
        } else {
            AccMonitoringOfficerInviteResource accMonitoringOfficerInviteResource = accUserRegistrationRestService.openAccMonitoringOfficerInvite(inviteHash).getSuccess();
            model.addAttribute("model", new MonitoringOfficerRegistrationViewModel(accMonitoringOfficerInviteResource.getEmail()));
            return "monitoring-officer/create-account";
        }
    }
}