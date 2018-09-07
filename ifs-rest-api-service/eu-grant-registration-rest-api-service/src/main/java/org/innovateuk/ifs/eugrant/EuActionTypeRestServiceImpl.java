package org.innovateuk.ifs.eugrant;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EuActionTypeRestServiceImpl extends BaseRestService implements EuActionTypeRestService {
    private static final String baseURL = "/eu-grant/action-type";
    @Override
    @Value("${ifs.eu-grant-registration.data.service.baseURL}")
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    @Override
    public RestResult<List<EuActionTypeResource>> findAll() {
        return getWithRestResultAnonymous(baseURL + "/findAll", euActionTypeResourceListType());
    }

    @Override
    public RestResult<EuActionTypeResource> getById(long id) {
        return getWithRestResultAnonymous(baseURL + "/getById/" + id, EuActionTypeResource.class);
    }

    public static ParameterizedTypeReference<List<EuActionTypeResource>> euActionTypeResourceListType() {
        return new ParameterizedTypeReference<List<EuActionTypeResource>>() {};
    }
}
