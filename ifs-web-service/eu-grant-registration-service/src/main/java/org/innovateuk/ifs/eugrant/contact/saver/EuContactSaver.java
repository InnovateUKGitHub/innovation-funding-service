package org.innovateuk.ifs.eugrant.contact.saver;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.eugrant.EuContactResource;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.contact.form.EuContactForm;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EuContactSaver {

    @Autowired
    private EuGrantCookieService euGrantCookieService;

    public RestResult<Void> save(EuContactForm contactForm) {

        EuContactResource euContactResource = getEuContactResource(contactForm);

        EuGrantResource euGrantResource = euGrantCookieService.get();
        euGrantResource.setContact(euContactResource);

        euGrantCookieService.save(euGrantResource);
        return RestResult.restSuccess();
    }

    public EuContactForm getEuContactForm(EuContactForm contactForm) {

        EuGrantResource euGrantResource = euGrantCookieService.get();
        EuContactResource euContactResource = euGrantResource.getContact();

        contactForm.setName(euContactResource.getName());
        contactForm.setEmail(euContactResource.getEmail());
        contactForm.setJobTitle(euContactResource.getJobTitle());
        contactForm.setTelephone(euContactResource.getTelephone());

        return contactForm;
    }

    private EuContactResource getEuContactResource(EuContactForm contactForm) {
        return new EuContactResource(
                contactForm.getName(),
                contactForm.getJobTitle(),
                contactForm.getEmail(),
                contactForm.getTelephone()
        );
    }
}
