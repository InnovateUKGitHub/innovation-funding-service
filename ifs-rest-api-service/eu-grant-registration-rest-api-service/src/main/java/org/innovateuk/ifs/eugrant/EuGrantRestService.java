package org.innovateuk.ifs.eugrant;

import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.UUID;

public interface EuGrantRestService {

    RestResult<EuGrantResource> create();
    RestResult<EuGrantResource> findById(UUID uuid);
    RestResult<Void> update(EuGrantResource euGrantResource);
    RestResult<EuGrantResource> submit(UUID uuid);

}
