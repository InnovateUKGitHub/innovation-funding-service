package org.innovateuk.ifs.eugrant.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.eugrant.EuContactResource;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.EuGrantRestService;
import org.innovateuk.ifs.eugrant.form.ContactForm;
import org.innovateuk.ifs.eugrant.service.EuGrantCookieService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.rest.RestFailure.error;
import static org.innovateuk.ifs.util.CollectionFunctions.removeDuplicates;
import static org.innovateuk.ifs.util.RedirectUtils.buildRedirect;

@Controller
@RequestMapping("/")
public class ContactDetailsController {

    @Autowired
    EuGrantCookieService euGrantCookieService;

    @Autowired
    EuGrantRestService euGrantRestService;

    @GetMapping("/contact-details")
    public String contactDetails() {

        return "eugrant/contact-details";
    }

    @GetMapping("/contact-details")
    public String contactDetails(HttpServletRequest request,
                                 HttpServletResponse response,
                                 @ModelAttribute("form") @Valid ContactForm contactForm,
                                 BindingResult bindingResult,
                                 ValidationHandler validationHandler) {


        Supplier<String> failureView = () -> contactDetails();
        Supplier<String> successView = () -> contactDetails();

        EuContactResource euContactResource = getEuContactResource(contactForm);

        EuGrantResource euGrantResource = euGrantCookieService.get();
        euGrantResource.setContact(euContactResource);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {

            RestResult<Void> sendResult = euGrantRestService.update(euGrantResource);

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
