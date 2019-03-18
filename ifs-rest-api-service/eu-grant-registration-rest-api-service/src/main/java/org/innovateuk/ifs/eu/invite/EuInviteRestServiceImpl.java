package org.innovateuk.ifs.eu.invite;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.eugrant.EuGrantPageResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

/**
 * EuInviteRestServiceImpl is a utility for CRUD operations on EuContactPageResource
 *
 * This class connects to the eu-grant-registration microservice through REST calls in order to send invites
 * onto our main system. It is called from the competition management microservice,
 * necessary to increase coupling in this instance as these endpoints expose confidential data
 * and therefore must be available to our internal users only
 */
@Service
public class EuInviteRestServiceImpl extends BaseRestService implements EuInviteRestService {

    private static final String baseUrl = "/eu-grants";

    @Override
    @Value("${ifs.eu-grant-registration.data.service.baseURL}")
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    @Override
    public RestResult<EuGrantPageResource> getEuGrantsByNotified(boolean notified, Integer pageIndex, Integer pageSize) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        String uriWithParams = buildPaginationUri(format("%s/notified/%s", baseUrl, notified), pageIndex, pageSize, null, params);
        return getWithRestResultAnonymous(uriWithParams, EuGrantPageResource.class);
    }

    @Override
    public RestResult<Void> sendInvites(List<UUID> ids) {
        return postWithRestResultAnonymous(baseUrl + "/send-invites", ids, Void.class);
    }

    @Override
    public RestResult<Long> getTotalSubmittedEuGrants() {
        return getWithRestResultAnonymous(baseUrl + "/total-submitted", Long.class);
    }
}