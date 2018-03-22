package org.innovateuk.ifs.finance.totals.service;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.totals.FinanceCostTotalResource;
import org.innovateuk.ifs.finance.resource.totals.FinanceType;
import org.innovateuk.ifs.security.HashBasedMacTokenHandler;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.util.List;

import static org.innovateuk.ifs.finance.builder.sync.FinanceCostTotalResourceBuilder.newFinanceCostTotalResource;
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
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2("CostTotalProvider",
            "localhost", 8080, this);

    @Pact(consumer = "AsyncRestCostTotalEndpointPactConsumer")
    public RequestResponsePact createPact(final PactDslWithProvider builder) throws Exception {

        String costTotalResourcesString = toJson(getCostTotalResources());

        HttpHeaders headers = new HttpHeaders();
        headers.add("x-auth-token", getAuthToken(costTotalResourcesString));
        headers.add("user-agent", "Java/" + System.getProperty("java.version"));
        headers.setConnection("keep-alive");
        headers.setContentType(APPLICATION_JSON);

        return builder
                .given("SendCostTotalsState")
                .uponReceiving("AsyncRestCostTotalEndpointPactConsumerTest test interaction")
                .path("/cost-totals")
                .body(costTotalResourcesString)
                .method("POST")
                .headers(headers.toSingleValueMap())
                .willRespondWith()
                // TODO any headers for verification?
                .status(CREATED.value())
                .toPact();
    }

    @Test
    @PactVerification
    public void sendCostTotals() throws Exception {
        List<FinanceCostTotalResource> costTotalResources = getCostTotalResources();
        asyncRestCostTotalEndpoint.sendCostTotals(costTotalResources);
    }

    private List<FinanceCostTotalResource> getCostTotalResources() {
        if (costTotalResources == null) {
            costTotalResources = newFinanceCostTotalResource()
                    .withFinanceType(FinanceType.APPLICATION)
                    .withFinanceId(123L)
                    .withFinanceRowType(
                            FinanceRowType.LABOUR,
                            FinanceRowType.OVERHEADS,
                            FinanceRowType.MATERIALS)
                    .withTotal(
                            BigDecimal.valueOf(5970.00),
                            BigDecimal.valueOf(552.35),
                            BigDecimal.valueOf(3081.50)
                    )
                    .build(3);
        }
        return costTotalResources;
    }

    private String getAuthToken(String input) throws InvalidKeyException {
        return hashBasedMacTokenHandler.calculateHash(financeTotalsKey, input);
    }
}
