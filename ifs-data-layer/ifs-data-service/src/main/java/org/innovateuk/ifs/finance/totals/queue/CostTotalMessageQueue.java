package org.innovateuk.ifs.finance.totals.queue;

import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.totals.FinanceCostTotalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Send {@link FinanceCostTotalResource} to related message handlers in
 * the finance-data-service via a queue mechanism.
 *
 * Under-the-hood, this queue can be implemented via async REST or
 * a message queue e.g. RabbitMQ.
 */
@Component
public class CostTotalMessageQueue {

    private static final Logger LOG = LoggerFactory.getLogger(CostTotalMessageQueue.class);

    private AbstractRestTemplateAdaptor restTemplateAdaptor;

    @Autowired
    public CostTotalMessageQueue(
            @Qualifier("finance_data_service_adaptor") AbstractRestTemplateAdaptor restTemplateAdaptor
    ) {
        this.restTemplateAdaptor = restTemplateAdaptor;
    }

    public ServiceResult<Void> sendCostTotals(List<FinanceCostTotalResource> financeCostTotalResources) {
        restTemplateAdaptor.restPostWithEntityAsync("/cost-totals", financeCostTotalResources, Void.class)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        LOG.error(
                                "Could not send financeCostTotalResources to finance-data-service. Exception: {}",
                                exception.getMessage(),
                                exception
                        );
                    }
                });

        return ServiceResult.serviceSuccess();
    }
}
