package org.innovateuk.ifs.finance.totals.service;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

/**
 * Service sends cost totals for all submitted {@link Application}s.
 */
@Service
public class AllFinanceTotalsSenderImpl implements AllFinanceTotalsSender{
    private static final Logger LOG = LoggerFactory.getLogger(AllFinanceTotalsSenderImpl.class);

    @Autowired
    private ApplicationFinanceTotalsSender applicationFinanceTotalsSender;

    @Autowired
    private ApplicationService applicationService;

    @Override
    public ServiceResult<Void> sendAllFinanceTotals() {
        LOG.debug("Initiating sendAllFinanceTotals.");

        Stream<Application> applications = applicationService.getApplicationsByState(ApplicationState.submittedAndFinishedStates)
                .getSuccess();
        applications.forEach(app -> applicationFinanceTotalsSender.sendFinanceTotalsForApplication(app.getId()));

        return ServiceResult.serviceSuccess();
    }
}
