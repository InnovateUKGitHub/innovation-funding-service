package org.innovateuk.ifs.management.decision.service;

import org.innovateuk.ifs.application.resource.Decision;
import org.innovateuk.ifs.commons.service.ServiceResult;

import java.util.List;
import java.util.Optional;

/**
 * Service for making the decision of which applications will receive funding and which will not for a given competition.
 */
public interface ApplicationDecisionService {

    ServiceResult<Void> saveApplicationDecisionData(Long competitionId, Decision decision, List<Long> applicationIds);

    Optional<Decision> getDecisionForString(String decisionName);
}
