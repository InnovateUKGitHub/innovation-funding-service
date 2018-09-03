package org.innovateuk.ifs.eugrant.contact.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.eugrant.EuContactResource;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.EuGrantRestService;
import org.innovateuk.ifs.eugrant.contact.form.ContactForm;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.innovateuk.ifs.eugrant.contact.populator.ContactFormPopulator;
import org.innovateuk.ifs.eugrant.contact.saver.EuGrantSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    private EuGrantRestService euGrantRestService;

    @Autowired
    private EuGrantSaver euGrantSaver;

    @GetMapping("/contact-details")
    public String contactDetails(Model model) {

        EuContactResource contact = euGrantCookieService.get().getContact();

        if (contact == null) {
            return "redirect:/contact-details/edit";
        }

        model.addAttribute("model", contactFormPopulator.populate(contact));

        return "eugrant/contact-details";
    }

    @GetMapping("/contact-details/edit")
    public String contactDetailsEdit(@ModelAttribute("form") ContactForm contactForm,
                                     BindingResult bindingResult,
                                     Model model) {

        EuContactResource contact = euGrantCookieService.get().getContact();

        EuGrantResource euGrantResource = euGrantCookieService.get();
        model.addAttribute("model", contactFormPopulator.populate(contact));

        return "eugrant/contact-details-edit";
    }

    @PostMapping("/contact-details")
    public String submitContactDetails(@ModelAttribute("form") @Valid ContactForm contactForm,
                                       BindingResult bindingResult,
                                       ValidationHandler validationHandler,
                                       Model model) {

        Supplier<String> failureView = () -> contactDetailsEdit(contactForm, bindingResult, model);
        Supplier<String> successView = () -> "redirect:/contact-details";

        if (bindingResult.hasErrors()) {
            return failureView.get();
        }

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
                contactForm.getJobTitle(),
                contactForm.getEmail(),
                contactForm.getTelephone()
                );
    }

}
