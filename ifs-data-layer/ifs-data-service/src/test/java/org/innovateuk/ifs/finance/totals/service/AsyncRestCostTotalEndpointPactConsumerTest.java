package org.innovateuk.ifs.finance.totals.service;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.finance.resource.totals.FinanceCostTotalResource;
import org.innovateuk.ifs.security.HashBasedMacTokenHandler;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.innovateuk.ifs.finance.builder.sync.FinanceCostTotalResourceBuilder.newFinanceCostTotalResource;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;
import static org.innovateuk.ifs.finance.resource.totals.FinanceType.APPLICATION;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class AsyncRestCostTotalEndpointPactConsumerTest extends BaseIntegrationTest {

    @Autowired
    private AsyncRestCostTotalEndpoint asyncRestCostTotalEndpoint;

    @Value("${ifs.finance-totals.authSecretKey}")
    private String financeTotalsKey;

    private List<FinanceCostTotalResource> costTotalResources;

    private HashBasedMacTokenHandler hashBasedMacTokenHandler = new HashBasedMacTokenHandler();

    @Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2(
            "CostTotalProvider",
            "localhost",
            8080,
            this);

    @Pact(consumer = "AsyncRestCostTotalEndpointPactConsumer")
    public RequestResponsePact createPact(final PactDslWithProvider builder) throws Exception {

        String costTotalResourcesString = toJson(getCostTotalResources());

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("x-auth-token", getAuthToken(costTotalResourcesString));
        requestHeaders.setContentType(APPLICATION_JSON);

        return builder
                .given("SendCostTotalsState")
                .uponReceiving("AsyncRestCostTotalEndpointPactConsumerTest test interaction")
                .path("/cost-totals")
                .body(costTotalResourcesString)
                .method("POST")
                .headers(requestHeaders.toSingleValueMap())
                .willRespondWith()
                .body("")
                .status(CREATED.value())
                .toPact();
    }

    @Test
    @PactVerification
    public void sendCostTotals() throws Exception {
        List<FinanceCostTotalResource> costTotalResources = getCostTotalResources();

        CompletableFuture<ResponseEntity<Void>> sendCostTotalsCompletable = asyncRestCostTotalEndpoint
                .sendCostTotalsCompletable(1L, costTotalResources);

        sendCostTotalsCompletable.get();
    }

    private List<FinanceCostTotalResource> getCostTotalResources() {
        if (costTotalResources == null) {
            costTotalResources = newFinanceCostTotalResource()
                    .withFinanceType(APPLICATION)
                    .withFinanceId(123L)
                    .withFinanceRowType(
                            LABOUR,
                            OVERHEADS,
                            MATERIALS)
                    .withTotal(
                            new BigDecimal("5970.999999"),
                            new BigDecimal("552.350000"),
                            new BigDecimal("3081.505995")
                    )
                    .build(3);
        }
        return costTotalResources;
    }

    private String getAuthToken(String input) throws InvalidKeyException {
        return hashBasedMacTokenHandler.calculateHash(financeTotalsKey, input);
    }
}
