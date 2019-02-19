package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.EuContactPageResource;
import org.innovateuk.ifs.invite.resource.EuContactResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static java.lang.String.format;

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

