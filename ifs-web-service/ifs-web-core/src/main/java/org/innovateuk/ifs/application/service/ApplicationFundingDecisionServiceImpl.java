package org.innovateuk.ifs.application.service;

import org.apache.catalina.util.ParameterMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Service
public class ApplicationFundingDecisionServiceImpl implements ApplicationFundingDecisionService {

    private static final Log LOG = LogFactory.getLog(ApplicationFundingDecisionServiceImpl.class);

    @Autowired
    private ApplicationFundingDecisionRestService applicationFundingDecisionRestService;

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Override
    public ServiceResult<Void> sendFundingNotifications(FundingNotificationResource fundingNotificationResource) {
        return applicationFundingDecisionRestService.sendApplicationFundingDecisions(fundingNotificationResource).toServiceResult();
    }

    @Override
    public ServiceResult<Void> saveApplicationFundingDecisionData(Long competitionId, FundingDecision fundingDecision, List<Long> applicationIds) {

        if (isAllowedFundingDecision(fundingDecision)) {
            Map<Long, FundingDecision> applicationIdToFundingDecision = createSubmittedApplicationFundingDecisionMap(applicationIds, competitionId, fundingDecision);
            applicationFundingDecisionRestService.saveApplicationFundingDecisionData(competitionId, applicationIdToFundingDecision).getSuccess();
        } else {
            return serviceFailure(new Error("Disallowed funding decision submitted", HttpStatus.BAD_REQUEST));
        }
        return serviceSuccess();
    }

    private boolean isAllowedFundingDecision(FundingDecision fundingDecision) {
        return !fundingDecision.equals(FundingDecision.UNDECIDED);
    }

    public Optional<FundingDecision> getFundingDecisionForString(String val) {
        Optional<FundingDecision> fundingDecision = Optional.empty();

        try {
            fundingDecision = Optional.of(FundingDecision.valueOf(val));
        } catch (IllegalArgumentException e) {
            LOG.info("Funding decision string disallowed", e);
        }
        return fundingDecision;
    }

    private List<Long> submittedApplicationIdsForCompetition(Long competitionId) {

        ApplicationSummaryPageResource results = applicationSummaryRestService.getSubmittedApplications(competitionId, null, 0, Integer.MAX_VALUE, Optional.empty(), Optional.empty())
                .getSuccess();
        return simpleMap(results.getContent(), ApplicationSummaryResource::getId);
    }

    private Map<Long, FundingDecision> createSubmittedApplicationFundingDecisionMap(List<Long> applicationIds, Long competitionId, FundingDecision fundingDecision) {

        return filteredListOfFundingDecisions(applicationIds, competitionId, fundingDecision);
    }

    private Map<Long, FundingDecision> filteredListOfFundingDecisions(List<Long> applicationIds, Long competitionId, FundingDecision fundingDecision) {
        Map<Long, FundingDecision> applicationIdToFundingDecision = new ParameterMap<>();

        List<Long> ids = submittedApplicationIdsForCompetition(competitionId);
        simpleFilter(applicationIds, ids::contains).forEach(id -> applicationIdToFundingDecision.put(id, fundingDecision));

        return applicationIdToFundingDecision;
    }
}
