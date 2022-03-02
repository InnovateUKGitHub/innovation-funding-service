package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.address.validation.ValidAddressForm;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.organisation.form.ConfirmResearchOrganisationEligibilityForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.function.Supplier;

@Controller
@RequestMapping("/organisation/confirm-eligibility")
public class ConfirmResearchOrganisationEligibilityController {

    private static final String FORM_NAME = "form";

    @Autowired
    private UserService userService;


    @GetMapping
    public String view(Model model,
                       UserResource user) {

        model.addAttribute(FORM_NAME, new ConfirmResearchOrganisationEligibilityForm());

        return "registration/organisation/confirm-research-organisation-eligibility";
    }

    @PostMapping
    public String post(@Valid @ModelAttribute(FORM_NAME) ConfirmResearchOrganisationEligibilityForm form,
            BindingResult bindingResult,
                       ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> "create-application/confirm-new-application";
        Supplier<String> successView = () -> "redirect:/";

        return validationHandler.failNowOrSucceedWith(failureView, successView);
    }

}
