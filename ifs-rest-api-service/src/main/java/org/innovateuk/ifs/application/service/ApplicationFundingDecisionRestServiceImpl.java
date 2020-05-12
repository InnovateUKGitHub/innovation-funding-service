package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingDecisionToSendApplicationResource;
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
 * Implementing class for {@link ApplicationFundingDecisionRestService}, for the action on deciding what applications should be funded for a given competition.
 */
@Service
public class ApplicationFundingDecisionRestServiceImpl extends BaseRestService implements ApplicationFundingDecisionRestService {

    private String applicationFundingDecisionRestURL = "/applicationfunding";

    @Override
    public RestResult<Void> saveApplicationFundingDecisionData(Long competitionId, Map<Long, FundingDecision> applicationIdToFundingDecision) {
        return postWithRestResult(applicationFundingDecisionRestURL + "/" + competitionId, applicationIdToFundingDecision, Void.class);
    }

    protected void setApplicationFundingDecisionRestURL(String applicationFundingDecisionRestURL) {
        this.applicationFundingDecisionRestURL = applicationFundingDecisionRestURL;
    }

    public RestResult<Void> sendApplicationFundingDecisions(FundingNotificationResource fundingNotificationResource) {
        return postWithRestResult(applicationFundingDecisionRestURL + "/send-notifications", fundingNotificationResource, Void.class);
    }

    @Override
    public RestResult<List<FundingDecisionToSendApplicationResource>> getNotificationResourceForApplications(List<Long> applicationIds) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("applicationIds", applicationIds.stream().map(String::valueOf).collect(Collectors.toList()));
        String url =  UriComponentsBuilder.fromPath(applicationFundingDecisionRestURL + "/notifications-to-send").queryParams(params).toUriString();
        return getWithRestResult(url, new ParameterizedTypeReference<List<FundingDecisionToSendApplicationResource>>() {});
    }
}
