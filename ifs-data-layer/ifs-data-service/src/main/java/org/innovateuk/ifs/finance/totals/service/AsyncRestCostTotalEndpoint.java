package org.innovateuk.ifs.finance.totals.service;

import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.totals.FinanceCostTotalResource;
import org.innovateuk.ifs.security.HashBasedMacTokenHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.security.InvalidKeyException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;

/**
 * Communication with the finance-data-service endpoint implemented by async REST .
 * <p>
 * This is eventually expected to be replaced by an event message queue, see IFS-2391.
 */
@Component
public class AsyncRestCostTotalEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncRestCostTotalEndpoint.class);

    private String financeTotalsKey;

    private HashBasedMacTokenHandler hashBasedMacTokenHandler;

    private AbstractRestTemplateAdaptor restTemplateAdaptor;

    @Autowired
    public AsyncRestCostTotalEndpoint(
            @Qualifier("finance_data_service_adaptor") AbstractRestTemplateAdaptor restTemplateAdaptor,
            HashBasedMacTokenHandler hashBasedMacTokenHandler,
            @Value("${ifs.finance-totals.authSecretKey}") String financeTotalsKey
    ) {
        this.restTemplateAdaptor = restTemplateAdaptor;
        this.hashBasedMacTokenHandler = hashBasedMacTokenHandler;
        this.financeTotalsKey = financeTotalsKey;
    }

    public ServiceResult<Void> sendCostTotals(Long applicationId,
                                              List<FinanceCostTotalResource> financeCostTotalResources) {
        sendCostTotalsCompletable(applicationId, financeCostTotalResources);
        return ServiceResult.serviceSuccess();
    }

    CompletableFuture<ResponseEntity<Void>> sendCostTotalsCompletable(Long applicationId,
                                                                      List<FinanceCostTotalResource> financeCostTotalResources) {
        return restTemplateAdaptor.restPostWithEntityAsync("/cost-totals", financeCostTotalResources,
                createFinanceServiceAuthHeader(financeCostTotalResources), Void.class)
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        LOG.error(
                                "Failed sending financeCostTotalResources to finance-data-service for applicationId: " +
                                        "{}. Exception: {}",
                                applicationId,
                                exception.getMessage(),
                                exception
                        );
                    }
                });
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
