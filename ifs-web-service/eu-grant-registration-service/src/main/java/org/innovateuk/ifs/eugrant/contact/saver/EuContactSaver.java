package org.innovateuk.ifs.eugrant.contact.saver;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.overview.service.EuGrantCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EuContactSaver {

    @Autowired
    private EuGrantCookieService euGrantCookieService;

    public RestResult<Void> save(EuGrantResource euGrantResource) {
        euGrantCookieService.save(euGrantResource);
        return RestResult.restSuccess();
    }
}
