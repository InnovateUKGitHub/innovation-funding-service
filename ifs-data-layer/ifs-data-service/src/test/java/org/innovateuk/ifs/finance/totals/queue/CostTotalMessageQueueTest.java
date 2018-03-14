package org.innovateuk.ifs.finance.totals.queue;

import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.finance.resource.totals.FinanceCostTotalResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.innovateuk.ifs.finance.builder.sync.FinanceCostTotalResourceBuilder.newFinanceCostTotalResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CostTotalMessageQueueTest {

    @Mock
    private AbstractRestTemplateAdaptor restTemplateAdaptorMock;

    private CostTotalMessageQueue costTotalMessageQueue;

    @Before
    public void setUp() throws Exception {
        costTotalMessageQueue = new CostTotalMessageQueue(restTemplateAdaptorMock);
    }

    @Test
    public void sendCostTotals() {
        String url = "/cost-totals";

        List<FinanceCostTotalResource> costTotalResources = newFinanceCostTotalResource()
                .build(2);

        when(restTemplateAdaptorMock.restPostWithEntityAsync(url, costTotalResources, Void.class))
                .thenReturn(new CompletableFuture<>());

        costTotalMessageQueue.sendCostTotals(costTotalResources);

        verify(restTemplateAdaptorMock).restPostWithEntityAsync(url, costTotalResources, Void.class);
    }
}