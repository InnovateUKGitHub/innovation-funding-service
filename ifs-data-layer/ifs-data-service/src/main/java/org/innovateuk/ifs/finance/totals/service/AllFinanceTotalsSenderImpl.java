package org.innovateuk.ifs.finance.totals.service;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.forEachWithIndex;

/**
 * Service sends cost totals for all submitted {@link Application}s.
 */
@Service
public class AllFinanceTotalsSenderImpl implements AllFinanceTotalsSender {
    private static final Logger LOG = LoggerFactory.getLogger(AllFinanceTotalsSenderImpl.class);

    @Autowired
    private ApplicationFinanceTotalsSender applicationFinanceTotalsSender;

    @Autowired
    private ApplicationService applicationService;

    @Override
    public ServiceResult<Void> sendAllFinanceTotals() {
        LOG.debug("Initiating sendAllFinanceTotals.");

        List<Application> applications = applicationService.getApplicationsByState(ApplicationState.submittedStates)
                .getSuccess();

        LOG.debug("Found: {} submitted applications", applications.size());

        forEachWithIndex(applications, (index, application) -> {
            LOG.debug("{}: Sending finance totals for applicationId: {}", index, application.getId());
            applicationFinanceTotalsSender.sendFinanceTotalsForApplication(application.getId());
        });

        LOG.debug("Completed sendAllFinanceTotals");
        return ServiceResult.serviceSuccess();
    }
}
