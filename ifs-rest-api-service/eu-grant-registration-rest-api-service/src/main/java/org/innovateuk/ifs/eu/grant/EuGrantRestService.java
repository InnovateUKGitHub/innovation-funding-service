package org.innovateuk.ifs.eu.grant;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.eugrant.EuGrantResource;

import java.util.UUID;

public interface EuGrantRestService {

    RestResult<EuGrantResource> create();
    RestResult<EuGrantResource> findById(UUID uuid);
    RestResult<Void> update(EuGrantResource euGrantResource);
    RestResult<EuGrantResource> submit(UUID uuid);

}
