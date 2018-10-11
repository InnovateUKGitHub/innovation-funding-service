package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.CompetitionSetupStakeholderRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.StakeholderInviteResource;
import org.innovateuk.ifs.registration.form.StakeholderRegistrationForm;
import org.innovateuk.ifs.registration.populator.StakeholderRegistrationModelPopulator;
import org.innovateuk.ifs.registration.service.StakeholderService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

import static java.lang.String.format;


@Controller
@RequestMapping("/stakeholder")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = StakeholderRegistrationController.class)
@PreAuthorize("permitAll")
public class StakeholderRegistrationController {

    private static final String FORM_ATTR_NAME = "form";

    private final StakeholderRegistrationModelPopulator stakeholderRegistrationModelPopulator;

    private final CompetitionSetupStakeholderRestService competitionSetupStakeholderRestService;

    private final StakeholderService stakeholderService;

    @Autowired
    public StakeholderRegistrationController(StakeholderRegistrationModelPopulator stakeholderRegistrationModelPopulator, CompetitionSetupStakeholderRestService competitionSetupStakeholderRestService, StakeholderService stakeholderService) {
        this.stakeholderRegistrationModelPopulator = stakeholderRegistrationModelPopulator;
        this.competitionSetupStakeholderRestService = competitionSetupStakeholderRestService;
        this.stakeholderService = stakeholderService;
    }

    @GetMapping("/{inviteHash}/register")
    public String createAccount(@PathVariable("inviteHash") String inviteHash, Model model, @ModelAttribute("form") StakeholderRegistrationForm stakeholderRegistrationForm) {
        StakeholderInviteResource stakeholderInviteResource = competitionSetupStakeholderRestService.getInvite(inviteHash).getSuccess();
        model.addAttribute("model", stakeholderRegistrationModelPopulator.populateModel(stakeholderInviteResource.getEmail()));
        return "registration/register";
    }

    @PostMapping("/{inviteHash}/register")
    public String submitYourDetails(Model model,
                                    @PathVariable("inviteHash") String inviteHash,
                                    @Valid @ModelAttribute(FORM_ATTR_NAME) StakeholderRegistrationForm stakeholderRegistrationForm,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler,
                                    UserResource loggedInUser) {

        Supplier<String> failureView = () -> doViewYourDetails(model, inviteHash, loggedInUser);

        if(loggedInUser != null){
            return failureView.get();
        } else {
            return validationHandler.failNowOrSucceedWith(failureView, () -> {
                ServiceResult<Void> result = stakeholderService.createStakeholder(inviteHash, stakeholderRegistrationForm);
                result.getErrors().forEach(error -> {
                    if (StringUtils.hasText(error.getFieldName())) {
                        bindingResult.rejectValue(error.getFieldName(), "registration." + error.getErrorKey());
                    } else {
                        bindingResult.reject("registration." + error.getErrorKey());
                    }
                });
                return validationHandler.
                        failNowOrSucceedWith(failureView,
                                () -> format("redirect:/registration/%s/register/account-created", inviteHash));
            });
        }
    }

    private String doViewYourDetails(Model model, String inviteHash, UserResource loggedInUser) {
        if(loggedInUser != null) {
            return "registration/error";
        } else {
            model.addAttribute("model", stakeholderRegistrationModelPopulator.populateModel(inviteHash));
            return "registration/register";
        }
    }

}
