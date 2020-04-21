package org.innovateuk.ifs.management.registration.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.CompetitionSetupExternalFinanceUsersRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.CompetitionFinanceInviteResource;
import org.innovateuk.ifs.management.registration.form.CompetitionFinanceRegistrationForm;
import org.innovateuk.ifs.management.registration.populator.ExternalFinanceRegistrationModelPopulator;
import org.innovateuk.ifs.management.registration.service.ExternalFinanceService;
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

@Controller
@RequestMapping("/finance-user")
@SecuredBySpring(value = "Controller",
        description = "Anyone can register for an account, if they have the invite hash",
        securedType = ExternalFinanceRegistrationController.class)
@PreAuthorize("permitAll")
public class ExternalFinanceRegistrationController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private ExternalFinanceRegistrationModelPopulator externalFinanceRegistrationModelPopulator;

    @Autowired
    private CompetitionSetupExternalFinanceUsersRestService competitionSetupExternalFinanceUsersRestService;

    @Autowired
    private ExternalFinanceService externalFinanceService;

    @GetMapping("/{inviteHash}/register")
    public String createAccount(@PathVariable("inviteHash") String inviteHash, Model model, @ModelAttribute("form") CompetitionFinanceRegistrationForm competitionFinanceRegistrationForm) {
        CompetitionFinanceInviteResource competitionFinanceInviteResource = competitionSetupExternalFinanceUsersRestService.getExternalFinanceInvite(inviteHash).getSuccess();
        model.addAttribute("model", externalFinanceRegistrationModelPopulator.populateModel(competitionFinanceInviteResource.getEmail()));
        return "competition-finance/create-account";
    }

    @PostMapping("/{inviteHash}/register")
    public String submitYourDetails(Model model,
                                    @PathVariable("inviteHash") String inviteHash,
                                    @Valid @ModelAttribute(FORM_ATTR_NAME) CompetitionFinanceRegistrationForm competitionFinanceRegistrationForm,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler,
                                    UserResource loggedInUser) {

        Supplier<String> failureView = () -> doViewYourDetails(model, inviteHash, loggedInUser);

        if(loggedInUser != null) {
            return failureView.get();
        } else {
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                ServiceResult<Void> result = externalFinanceService.createExternalFinanceUser(inviteHash, competitionFinanceRegistrationForm);
                //  fix this
                result.getErrors().forEach(error -> {
                    if (StringUtils.hasText(error.getFieldName())) {
                        bindingResult.rejectValue(error.getFieldName(), "compFinance." + error.getErrorKey());
                    } else {
                        bindingResult.reject("compFinance." + error.getErrorKey());
                    }
                });
                return validationHandler.
                        failNowOrSucceedWith(failureView,
                                () -> format("redirect:/finance-user/%s/register/account-created", inviteHash));
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

        return competitionSetupExternalFinanceUsersRestService.getExternalFinanceInvite(inviteHash).andOnSuccessReturn(invite -> {
            if (InviteStatus.OPENED != invite.getStatus()) {
                return format("redirect:/finance-user/%s/register", inviteHash);
            } else {
                return "registration/account-created";
            }
        }).getSuccess();
    }

    private String doViewYourDetails(Model model, String inviteHash, UserResource loggedInUser) {
        if(loggedInUser != null) {
            return "registration/error";
        } else {
            CompetitionFinanceInviteResource competitionFinanceInviteResource = competitionSetupExternalFinanceUsersRestService.getExternalFinanceInvite(inviteHash).getSuccess();
            model.addAttribute("model", externalFinanceRegistrationModelPopulator.populateModel(competitionFinanceInviteResource.getEmail()));
            return "competition-finance/create-account";
        }
    }
}
