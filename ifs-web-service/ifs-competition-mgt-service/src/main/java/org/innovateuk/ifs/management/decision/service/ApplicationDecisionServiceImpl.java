package org.innovateuk.ifs.management.decision.service;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.application.resource.Decision;
import org.innovateuk.ifs.application.service.ApplicationDecisionRestService;
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

@Slf4j
@Service
public class ApplicationDecisionServiceImpl implements ApplicationDecisionService {

    @Autowired
    private ApplicationDecisionRestService applicationDecisionRestService;

    @Override
    public ServiceResult<Void> saveApplicationDecisionData(Long competitionId, Decision decision, List<Long> applicationIds) {

        if (isAllowedDecision(decision)) {
            Map<Long, Decision> applicationIdToDecision = applicationIds.stream().collect(toMap(Function.identity(), id -> decision));
            applicationDecisionRestService.saveApplicationDecisionData(competitionId, applicationIdToDecision).getSuccess();
        } else {
            return serviceFailure(new Error("Disallowed funding decision submitted", HttpStatus.BAD_REQUEST));
        }
        return serviceSuccess();
    }

    private boolean isAllowedDecision(Decision decision) {
        return !decision.equals(Decision.UNDECIDED);
    }

    public Optional<Decision> getDecisionForString(String val) {
        Optional<Decision> decision = Optional.empty();

        try {
            decision = Optional.of(Decision.valueOf(val));
        } catch (IllegalArgumentException e) {
            log.info("Funding decision string disallowed", e);
        }
        return decision;
    }
}
