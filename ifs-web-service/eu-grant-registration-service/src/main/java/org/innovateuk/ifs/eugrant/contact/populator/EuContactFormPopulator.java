package org.innovateuk.ifs.eugrant.contact.populator;

import org.innovateuk.ifs.eugrant.EuContactResource;
import org.innovateuk.ifs.eugrant.contact.form.EuContactForm;
import org.springframework.stereotype.Component;

@Component
public class EuContactFormPopulator {

    public EuContactForm populate(EuContactResource euContactResource) {

        if (euContactResource == null) {
            return new EuContactForm();
        } else {

            EuContactForm contactForm = new EuContactForm();

            contactForm.setName(euContactResource.getName());
            contactForm.setEmail(euContactResource.getEmail());
            contactForm.setJobTitle(euContactResource.getJobTitle());
            contactForm.setTelephone(euContactResource.getTelephone());

            return contactForm;
        }
    }
}
