package org.innovateuk.ifs.granttransfer.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.granttransfer.resource.EuActionTypeResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Action type rest service.
 */
@Service
public class ActionTypeRestServiceImpl extends BaseRestService implements ActionTypeRestService {
    private static final String BASE_URL = "/action-type";

    @Override
    public RestResult<List<EuActionTypeResource>> findAll() {
        return getWithRestResult(BASE_URL + "/find-all", euActionTypeResourceListType());
    }

    @Override
    public RestResult<EuActionTypeResource> getById(long id) {
        return getWithRestResult(BASE_URL + "/get-by-id/" + id, EuActionTypeResource.class);
    }

    public static ParameterizedTypeReference<List<EuActionTypeResource>> euActionTypeResourceListType() {
        return new ParameterizedTypeReference<List<EuActionTypeResource>>() {};
    }
}
