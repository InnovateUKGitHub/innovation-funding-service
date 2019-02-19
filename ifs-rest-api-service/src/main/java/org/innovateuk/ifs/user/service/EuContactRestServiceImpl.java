package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.EuContactPageResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static java.lang.String.format;

/**
 * EuContactRestServiceImpl is a utility for CRUD operations on {@link EuContactPageResource}.
 *
 * This class connects to the eu-grant-registration microservice through REST calls in order to send invites
 * onto our main system. Necessary to increase coupling in this instance as these endpoints expose confidential data
 * and therefore must be available to our internal users only
 */

@Service
public class EuContactRestServiceImpl extends BaseRestService implements EuContactRestService {

    private static final String baseUrl = "/eu-contacts";

    @Override
    @Value("http://eu-grant-registration-data-service:8080")
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    @Override
    public RestResult<EuContactPageResource> getEuContactsByNotified(boolean notified, Integer pageIndex, Integer pageSize) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        String uriWithParams = buildPaginationUri(format("%s/notified/%s", baseUrl, notified), pageIndex, pageSize, null, params);
        return getWithRestResultAnonymous(uriWithParams, EuContactPageResource.class);
    }
}
