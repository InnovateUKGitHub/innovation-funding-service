package org.innovateuk.ifs.finance.totals.service;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static org.innovateuk.ifs.application.resource.ApplicationState.submittedStates;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Service sends cost totals for all submitted {@link Application}s.
 */
@Service
public class AllFinanceTotalsSenderImpl implements AllFinanceTotalsSender {
    private static final Logger LOG = LoggerFactory.getLogger(AllFinanceTotalsSenderImpl.class);

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationFinanceTotalsSender applicationFinanceTotalsSender;

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<Void> sendAllFinanceTotals() {
        LOG.debug("Initiating sendAllFinanceTotals.");

        try (Stream<Application> applications = submittedApplicationsStream()) {
            applications.forEach(application ->
                    applicationFinanceTotalsSender.sendFinanceTotalsForApplication(application.getId()));
        }

        LOG.debug("Completed sendAllFinanceTotals");
        return ServiceResult.serviceSuccess();
    }

    private Stream<Application> submittedApplicationsStream() {
        return applicationRepository.findByApplicationProcessActivityStateStateIn(
                simpleMap(submittedStates, ApplicationState::getBackingState));
    }
}
