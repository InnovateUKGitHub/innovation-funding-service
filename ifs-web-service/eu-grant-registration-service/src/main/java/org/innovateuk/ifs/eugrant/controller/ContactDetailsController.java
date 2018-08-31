package org.innovateuk.ifs.eugrant.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.eugrant.EuContactResource;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.form.ContactForm;
import org.innovateuk.ifs.eugrant.populator.ContactFormPopulator;
import org.innovateuk.ifs.eugrant.saver.EuGrantSaver;
import org.innovateuk.ifs.eugrant.service.EuGrantCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.rest.RestFailure.error;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;

@Controller
@RequestMapping("/")
public class ContactDetailsController {

    @Autowired
    private EuGrantCookieService euGrantCookieService;

    @Autowired
    private ContactFormPopulator contactFormPopulator;

    @Autowired
    private EuGrantSaver euGrantSaver;

    @GetMapping("/contact-details")
    public String contactDetails(@ModelAttribute("form") ContactForm contactForm,
                                 BindingResult bindingResult) {

        EuGrantResource euGrantResource = euGrantCookieService.get();

        if (euGrantResource.getId() != null) {
            contactForm = contactFormPopulator.populate(euGrantResource.getContact());
        }

        return "eugrant/contact-details";
    }

    @PostMapping("/contact-details")
    public String submitContactDetails(@ModelAttribute("form") @Valid ContactForm contactForm,
                                       BindingResult bindingResult,
                                       ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> contactDetails(contactForm, bindingResult);
        Supplier<String> successView = () -> contactDetails(contactForm, bindingResult);

        EuContactResource euContactResource = getEuContactResource(contactForm);

        EuGrantResource euGrantResource = euGrantCookieService.get();
        euGrantResource.setContact(euContactResource);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            RestResult<Void> sendResult = euGrantSaver.save(euGrantResource);

            return validationHandler.addAnyErrors(error(removeDuplicates(sendResult.getErrors()))).
                    failNowOrSucceedWith(failureView, successView);
        });

    }

    private EuContactResource getEuContactResource(ContactForm contactForm) {
        return new EuContactResource(
                contactForm.getName(),
                contactForm.getEmail(),
                contactForm.getJobTitle(),
                contactForm.getTelephone()
                );
    }

}
