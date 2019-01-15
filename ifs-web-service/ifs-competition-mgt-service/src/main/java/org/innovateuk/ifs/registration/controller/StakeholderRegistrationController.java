package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.CompetitionSetupStakeholderRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.StakeholderInviteResource;
import org.innovateuk.ifs.invite.service.InviteUserRestService;
import org.innovateuk.ifs.registration.form.StakeholderRegistrationForm;
import org.innovateuk.ifs.registration.populator.StakeholderRegistrationModelPopulator;
import org.innovateuk.ifs.registration.service.StakeholderService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;

/**
 * Controller to manage registration of stakeholder users
 */
@Controller
@RequestMapping("/stakeholder")
@SecuredBySpring(value = "Controller",
        description = "Anyone can register for an account, if they have the invite hash",
        securedType = StakeholderRegistrationController.class)
@PreAuthorize("permitAll")
public class StakeholderRegistrationController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private StakeholderRegistrationModelPopulator stakeholderRegistrationModelPopulator;

    @Autowired
    private CompetitionSetupStakeholderRestService competitionSetupStakeholderRestService;

    @Autowired
    private StakeholderService stakeholderService;

    @Autowired
    private InviteUserRestService inviteUserRestService;


    @GetMapping("/{inviteHash}/register")
    public String createAccount(@PathVariable("inviteHash") String inviteHash, Model model, @ModelAttribute("form") StakeholderRegistrationForm stakeholderRegistrationForm) {
        StakeholderInviteResource stakeholderInviteResource = competitionSetupStakeholderRestService.getStakeholderInvite(inviteHash).getSuccess();
        model.addAttribute("model", stakeholderRegistrationModelPopulator.populateModel(stakeholderInviteResource.getEmail()));
        return "stakeholders/create-account";
    }

    @PostMapping("/{inviteHash}/register")
    public String submitYourDetails(Model model,
                                    @PathVariable("inviteHash") String inviteHash,
                                    @Valid @ModelAttribute(FORM_ATTR_NAME) StakeholderRegistrationForm stakeholderRegistrationForm,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler,
                                    UserResource loggedInUser) {

        Supplier<String> failureView = () -> doViewYourDetails(model, inviteHash, loggedInUser);

        if(loggedInUser != null) {
            return failureView.get();
        } else {
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                ServiceResult<Void> result = stakeholderService.createStakeholder(inviteHash, stakeholderRegistrationForm);
                result.getErrors().forEach(error -> {
                    if (StringUtils.hasText(error.getFieldName())) {
                        bindingResult.rejectValue(error.getFieldName(), "stakeholders." + error.getErrorKey());
                    } else {
                        bindingResult.reject("stakeholders." + error.getErrorKey());
                    }
                });
                return validationHandler.
                        failNowOrSucceedWith(failureView,
                                () -> format("redirect:/stakeholder/%s/register/account-created", inviteHash));
            });
        }
    }


    @GetMapping(value = "/{inviteHash}/register/account-created")
    public String accountCreated(@PathVariable("inviteHash") String inviteHash, UserResource loggedInUser) {
        boolean userIsLoggedIn = loggedInUser != null;

        // the user is already logged in, take them back to the dashboard
        if (userIsLoggedIn) {
            return "redirect:/";
        }

        return competitionSetupStakeholderRestService.getStakeholderInvite(inviteHash).andOnSuccessReturn(invite -> {
            if (InviteStatus.OPENED != invite.getStatus()) {
                return format("redirect:/stakeholder/%s/register", inviteHash);
            } else {
                return "registration/account-created";
            }
        }).getSuccess();
    }

    private String doViewYourDetails(Model model, String inviteHash, UserResource loggedInUser) {
        if(loggedInUser != null) {
            return "registration/error";
        } else {
            StakeholderInviteResource stakeholderInviteResource = competitionSetupStakeholderRestService.getStakeholderInvite(inviteHash).getSuccess();
            model.addAttribute("model", stakeholderRegistrationModelPopulator.populateModel(stakeholderInviteResource.getEmail()));
            return "stakeholders/create-account";
        }
    }

}
