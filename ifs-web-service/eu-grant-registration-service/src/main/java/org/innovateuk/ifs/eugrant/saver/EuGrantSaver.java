package org.innovateuk.ifs.eugrant.saver;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;
import org.innovateuk.ifs.eugrant.EuGrantRestService;
import org.innovateuk.ifs.eugrant.service.EuGrantCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EuGrantSaver {

    @Autowired
    private EuGrantRestService euGrantRestService;

    @Autowired
    private EuGrantCookieService euGrantCookieService;

    public RestResult<Void> save(EuGrantResource euGrantResource) {

        euGrantCookieService.save(euGrantResource);
//        return euGrantRestService.update(euGrantResource);
        return RestResult.restSuccess();
    }
}
