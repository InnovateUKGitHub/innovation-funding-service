package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.Decision;
import org.innovateuk.ifs.application.resource.ApplicationDecisionToSendApplicationResource;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementing class for {@link ApplicationDecisionRestService}, for the action on deciding what applications should be funded for a given competition.
 */
@Service
public class ApplicationDecisionRestServiceImpl extends BaseRestService implements ApplicationDecisionRestService {

    private String applicationDecisionRestURL = "/applicationfunding";

    @Override
    public RestResult<Void> saveApplicationDecisionData(Long competitionId, Map<Long, Decision> applicationIdToDecision) {
        return postWithRestResult(applicationDecisionRestURL + "/" + competitionId, applicationIdToDecision, Void.class);
    }

    protected void setApplicationDecisionRestURL(String applicationDecisionRestURL) {
        this.applicationDecisionRestURL = applicationDecisionRestURL;
    }

    public RestResult<Void> sendApplicationDecisions(FundingNotificationResource fundingNotificationResource) {
        return postWithRestResult(applicationDecisionRestURL + "/send-notifications", fundingNotificationResource, Void.class);
    }

    @Override
    public RestResult<List<ApplicationDecisionToSendApplicationResource>> getNotificationResourceForApplications(List<Long> applicationIds) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("applicationIds", applicationIds.stream().map(String::valueOf).collect(Collectors.toList()));
        String url =  UriComponentsBuilder.fromPath(applicationDecisionRestURL + "/notifications-to-send").queryParams(params).toUriString();
        return getWithRestResult(url, new ParameterizedTypeReference<List<ApplicationDecisionToSendApplicationResource>>() {});
    }
}
