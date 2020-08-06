package org.innovateuk.ifs.kta.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.registration.form.RegistrationForm;
import org.innovateuk.ifs.registration.form.RegistrationForm.ExternalUserRegistrationValidationGroup;
import org.innovateuk.ifs.registration.viewmodel.RegistrationViewModel.RegistrationViewModelBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.groups.Default;

@Controller
@RequestMapping("/knowledge-transfer-advisor")
@SecuredBySpring(value = "Controller",
        description = "Anyone can register for an account, if they have the invite hash",
        securedType = KtaRegistrationController.class)
@PreAuthorize("permitAll")
public class KtaRegistrationController {

    @GetMapping("/{inviteHash}/register")
    public String createAccount(@PathVariable("inviteHash") String inviteHash, Model model, @Validated({Default.class, ExternalUserRegistrationValidationGroup.class}) @ModelAttribute("form") RegistrationForm form) {
//            StakeholderInviteResource stakeholderInviteResource = competitionSetupStakeholderRestService.getStakeholderInvite(inviteHash).getSuccess();
        form.setEmail("mydummyemail@gmail.com");
        model.addAttribute("model", RegistrationViewModelBuilder.aRegistrationViewModel()
                .withTermsRequired(true)
                .withPhoneRequired(true)
                .withAddressRequired(true)
                .withInvitee(true).build());
        return "registration/register";
    }

//    @PostMapping("/{inviteHash}/register")
//    public String submitYourDetails(Model model,
//                                    @PathVariable("inviteHash") String inviteHash,
//                                    @Valid @ModelAttribute("form") RegistrationForm form,
//                                    BindingResult bindingResult,
//                                    ValidationHandler validationHandler,
//                                    UserResource loggedInUser) {
//
//        Supplier<String> failureView = () -> doViewYourDetails(model, inviteHash, loggedInUser);
//
//        if (loggedInUser != null) {
//            return failureView.get();
//        } else {
//            return validationHandler.failNowOrSucceedWith(failureView, () -> {
//                ServiceResult<Void> result = ServiceResult.serviceSuccess(); // replace with service.
//                result.getErrors().forEach(error -> {
//                    if (StringUtils.hasText(error.getFieldName())) {
//                        bindingResult.rejectValue(error.getFieldName(), "stakeholders." + error.getErrorKey());
//                    } else {
//                        bindingResult.reject("stakeholders." + error.getErrorKey());
//                    }
//                });
//                return validationHandler.
//                        failNowOrSucceedWith(failureView,
//                                () -> format("redirect:/stakeholder/%s/register/account-created", inviteHash)); //TODO what  page here?
//            });
//        }
//    }

//    private String doViewYourDetails(Model model, String inviteHash, UserResource loggedInUser) {
//        if (loggedInUser != null) {
//            return "registration/error";
//        } else {
//            model.addAttribute("model", RegistrationViewModelBuilder.aRegistrationViewModel().withExternalUser(true).withInvitee(true).build());
//            return "registration/register";
//        }
//    }
}
