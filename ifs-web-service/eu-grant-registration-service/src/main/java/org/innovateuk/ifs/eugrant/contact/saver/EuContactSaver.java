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

    private EuContactResource getEuContactResource(EuContactForm contactForm) {
        return new EuContactResource(
                contactForm.getName(),
                contactForm.getJobTitle(),
                contactForm.getEmail(),
                contactForm.getTelephone()
        );
    }
}
