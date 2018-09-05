package org.innovateuk.ifs.eugrant.funding.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.contact.form.EuContactForm;
import org.innovateuk.ifs.eugrant.funding.form.EuFundingForm;
import org.innovateuk.ifs.eugrant.funding.populator.EuFundingFormPopulator;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.rest.RestFailure.error;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;

/**
 * This Controller handles the funding details activity
 */
@Controller
@RequestMapping("/")
public class EuFundingController {

    @Autowired
    private EuGrantCookieService euGrantCookieService;

    @Autowired
    private EuFundingFormPopulator euFundingFormPopulator;

    @GetMapping("/funding-details")
    public String fundingDetails(@ModelAttribute(value = "form", binding = false) EuFundingForm form) {

        EuGrantResource grantResource = euGrantCookieService.get();

        if (grantResource.getFunding() == null) {
            return "redirect:/funding-details/edit";
        }

        form = euFundingFormPopulator.populate(form);

        return "eugrant/funding-details";
    }

    @GetMapping("/funding-details/edit")
    public String fundingDetailsEdit(@ModelAttribute(value = "form", binding = false) EuFundingForm form,
                                     BindingResult bindingResult) {

        form = euFundingFormPopulator.populate(form);

        return "funding/funding-details-edit";
    }

//    @PostMapping("/contact-details/edit")
//    public String submitContactDetails(@ModelAttribute("form") @Valid EuContactForm contactForm,
//                                       BindingResult bindingResult,
//                                       ValidationHandler validationHandler) {
//
//        Supplier<String> failureView = () -> contactDetailsEdit(contactForm, bindingResult);
//        Supplier<String> successView = () -> "redirect:/contact-details";
//
//        if (bindingResult.hasErrors()) {
//            return failureView.get();
//        }
//
//        return validationHandler.failNowOrSucceedWith(failureView, () -> {
//
//            RestResult<Void> sendResult = euContactSaver.save(contactForm);
//
//            return validationHandler.addAnyErrors(error(removeDuplicates(sendResult.getErrors()))).
//                    failNowOrSucceedWith(failureView, successView);
//        });
//
//    }
}
