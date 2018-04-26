package org.innovateuk.ifs.finance.totals.service;

import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.finance.resource.totals.FinanceCostTotalResource;
import org.innovateuk.ifs.finance.totals.service.AsyncRestCostTotalEndpoint;
import org.innovateuk.ifs.security.HashBasedMacTokenHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.innovateuk.ifs.finance.builder.sync.FinanceCostTotalResourceBuilder.newFinanceCostTotalResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AsyncRestCostTotalEndpointTest {

    @Mock
    private AbstractRestTemplateAdaptor restTemplateAdaptorMock;

    private AsyncRestCostTotalEndpoint costTotalEndpoint;

    private HashBasedMacTokenHandler hashBasedMacTokenHandler = new HashBasedMacTokenHandler();

    @Before
    public void setUp() throws Exception {
        costTotalEndpoint = new AsyncRestCostTotalEndpoint(
                restTemplateAdaptorMock,
                new HashBasedMacTokenHandler(),
                "supersecretkey"
        );
    }

    @Test
    public void sendCostTotals() throws Exception {
        String url = "/cost-totals";

        List<FinanceCostTotalResource> costTotalResources = newFinanceCostTotalResource()
                .build(2);

        HttpHeaders authHeader = new HttpHeaders();
        authHeader.add("X-AUTH-TOKEN", hashBasedMacTokenHandler.calculateHash("supersecretkey",
                toJson(costTotalResources)));

        when(restTemplateAdaptorMock.restPostWithEntityAsync(url, costTotalResources, authHeader, Void.class))
                .thenReturn(new CompletableFuture<>());

        costTotalEndpoint.sendCostTotals(1L, costTotalResources);

        verify(restTemplateAdaptorMock).restPostWithEntityAsync(url, costTotalResources, authHeader, Void.class);
    }
}