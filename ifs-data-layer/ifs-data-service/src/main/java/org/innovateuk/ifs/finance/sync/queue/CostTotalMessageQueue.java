package org.innovateuk.ifs.finance.sync.queue;

import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.sync.FinanceCostTotalResource;
import org.innovateuk.ifs.security.HashBasedMacTokenHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.security.InvalidKeyException;
import java.util.List;

import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;

/**
 * Send {@link FinanceCostTotalResource} to related message handlers in
 * the finance-data-service via a queue mechanism.
 * <p>
 * Under-the-hood, this queue can be implemented via async REST or
 * a message queue e.g. RabbitMQ.
 */
@Component
public class CostTotalMessageQueue {

    @Value("${ifs.finance-totals.authSecretKey}")
    private String financeTotalsKey;

    @Autowired
    private HashBasedMacTokenHandler hashBasedMacTokenHandler;

    private static final Logger LOG = LoggerFactory.getLogger(CostTotalMessageQueue.class);

    private AbstractRestTemplateAdaptor restTemplateAdaptor;

    @Autowired
    public CostTotalMessageQueue(
            @Qualifier("finance_data_service_adaptor") AbstractRestTemplateAdaptor restTemplateAdaptor
    ) {
        this.restTemplateAdaptor = restTemplateAdaptor;
    }

    public ServiceResult<Void> sendCostTotals(List<FinanceCostTotalResource> financeCostTotalResources) {
        restTemplateAdaptor.restPostWithEntityAsync("/cost-totals", financeCostTotalResources,
                createFinanceServiceAuthHeader(financeCostTotalResources), Void.class)
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

    private HttpHeaders createFinanceServiceAuthHeader(List<FinanceCostTotalResource> financeCostTotalResources) {
        HttpHeaders headers = new HttpHeaders();
        try {
            String token = hashBasedMacTokenHandler.calculateHash(financeTotalsKey, toJson(financeCostTotalResources));
            headers.add("X-AUTH-TOKEN", token);
        } catch (InvalidKeyException e) {
            LOG.error("Caught InvalidKeyException while trying to calculate hash", e);
        }
        return headers;
    }
}
