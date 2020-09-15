package org.innovateuk.ifs.management.funding.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.service.ApplicationFundingDecisionRestService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class ApplicationFundingDecisionServiceImpl implements ApplicationFundingDecisionService {

    private static final Log LOG = LogFactory.getLog(ApplicationFundingDecisionServiceImpl.class);

    @Autowired
    private ApplicationFundingDecisionRestService applicationFundingDecisionRestService;

    @Override
    public ServiceResult<Void> saveApplicationFundingDecisionData(Long competitionId, FundingDecision fundingDecision, List<Long> applicationIds) {

        if (isAllowedFundingDecision(fundingDecision)) {
            Map<Long, FundingDecision> applicationIdToFundingDecision = applicationIds.stream().collect(toMap(Function.identity(), id -> fundingDecision));
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
}
