package org.innovateuk.ifs.eugrant.contact.populator;

import org.innovateuk.ifs.eugrant.EuContactResource;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.contact.form.EuContactForm;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EuContactFormPopulator {

    @Autowired
    private EuGrantCookieService euGrantCookieService;

    public EuContactForm populate(EuContactForm contactForm) {

        EuGrantResource euGrantResource = euGrantCookieService.get();
        EuContactResource euContactResource = euGrantResource.getContact();

        if (euContactResource != null) {
            contactForm.setName(euContactResource.getName());
            contactForm.setEmail(euContactResource.getEmail());
            contactForm.setJobTitle(euContactResource.getJobTitle());
            contactForm.setTelephone(euContactResource.getTelephone());
        }

        return contactForm;

    }
}
