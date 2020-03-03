package org.innovateuk.ifs.granttransfer.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.granttransfer.resource.EuActionTypeResource;

import java.util.List;

public interface ActionTypeRestService {

    RestResult<List<EuActionTypeResource>> findAll();
    RestResult<EuActionTypeResource> getById(long id);
}
