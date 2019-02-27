package org.innovateuk.ifs.eu.grant;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.eugrant.EuActionTypeResource;

import java.util.List;

public interface EuActionTypeRestService {

    RestResult<List<EuActionTypeResource>> findAll();
    RestResult<EuActionTypeResource> getById(long id);
}
