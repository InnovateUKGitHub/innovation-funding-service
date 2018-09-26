package org.innovateuk.ifs.eugrant;

import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;

public interface EuActionTypeRestService {

    RestResult<List<EuActionTypeResource>> findAll();
    RestResult<EuActionTypeResource> getById(long id);
}
