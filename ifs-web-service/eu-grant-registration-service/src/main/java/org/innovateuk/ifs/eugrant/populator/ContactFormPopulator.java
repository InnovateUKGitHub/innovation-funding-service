package org.innovateuk.ifs.eugrant.populator;

import org.innovateuk.ifs.eugrant.EuContactResource;
import org.innovateuk.ifs.eugrant.form.ContactForm;
import org.springframework.stereotype.Component;

@Component
public class ContactFormPopulator {

    public ContactForm populate(EuContactResource euContactResource) {
        ContactForm contactForm = new ContactForm();

        contactForm.setName(euContactResource.getName());
        contactForm.setEmail(euContactResource.getEmail());
        contactForm.setJobTitle(euContactResource.getJobTitle());
        contactForm.setTelephone(euContactResource.getTelephone());

        return contactForm;
    }
}
