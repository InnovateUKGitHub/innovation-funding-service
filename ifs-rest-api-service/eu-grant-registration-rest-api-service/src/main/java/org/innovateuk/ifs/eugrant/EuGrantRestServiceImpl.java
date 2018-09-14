package org.innovateuk.ifs.eugrant;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class EuGrantRestServiceImpl extends BaseRestService implements EuGrantRestService {
    private static final String baseURL = "/eu-grant";
    @Override
    @Value("${ifs.eu-grant-registration.data.service.baseURL}")
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }


    @Override
    public RestResult<EuGrantResource> create() {
        return postWithRestResultAnonymous(baseURL, EuGrantResource.class);
    }

    @Override
    public RestResult<EuGrantResource> findById(UUID uuid) {
        return getWithRestResultAnonymous(baseURL + "/" + uuid.toString(), EuGrantResource.class);
    }

    @Override
    public RestResult<Void> update(EuGrantResource euGrantResource) {
        return putWithRestResultAnonymous(baseURL + "/" + euGrantResource.getId().toString(), euGrantResource, Void.class);
    }
}
