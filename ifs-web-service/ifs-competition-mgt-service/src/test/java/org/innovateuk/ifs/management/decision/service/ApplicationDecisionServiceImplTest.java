package org.innovateuk.ifs.management.decision.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.Decision;
import org.innovateuk.ifs.application.service.ApplicationDecisionRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.innovateuk.ifs.application.builder.ApplicationSummaryResourceBuilder.newApplicationSummaryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ApplicationDecisionServiceImplTest extends BaseServiceUnitTest<ApplicationDecisionServiceImpl> {
    @Mock
    private ApplicationDecisionRestService applicationDecisionRestService;

    protected ApplicationDecisionServiceImpl supplyServiceUnderTest() {
        return new ApplicationDecisionServiceImpl();
    }

    @Test
    public void saveDecision() throws Exception {
        List<Long> applicationIds = new ArrayList<>();
        applicationIds.add(8L);
        applicationIds.add(9L);

        ApplicationSummaryPageResource applicationSummaryPageResource = new ApplicationSummaryPageResource();
        List<ApplicationSummaryResource> applicationSummaryResources = newApplicationSummaryResource().withId(8L, 9L).build(4);
        applicationSummaryPageResource.setContent(applicationSummaryResources);

        when(applicationDecisionRestService.saveApplicationDecisionData(any(), any())).thenReturn(restSuccess());

        service.saveApplicationDecisionData(1L, Decision.ON_HOLD, applicationIds);

        Map<Long, Decision> expectedDecisionMap = new HashMap<>();
        expectedDecisionMap.put(8L, Decision.ON_HOLD);
        expectedDecisionMap.put(9L, Decision.ON_HOLD);

        verify(applicationDecisionRestService).saveApplicationDecisionData(1L, expectedDecisionMap);
    }

    @Test
    public void saveDecision_undecidedDecisionChoice() throws Exception {
        List<Long> applicationIds = new ArrayList<>();
        applicationIds.add(8L);
        applicationIds.add(9L);

        ApplicationSummaryPageResource applicationSummaryPageResource = new ApplicationSummaryPageResource();
        List<ApplicationSummaryResource> applicationSummaryResources = newApplicationSummaryResource().withId(8L, 9L).build(2);
        applicationSummaryPageResource.setContent(applicationSummaryResources);

        ServiceResult<Void> result = service.saveApplicationDecisionData(1L, Decision.UNDECIDED, applicationIds);

        assertTrue(result.isFailure());

        verifyZeroInteractions(applicationDecisionRestService);
    }

    @Test
    public void saveDecision_getDecisionForStringShouldReturnAppropriateDecision() throws Exception {
        String decisionString = "ON_HOLD";

        Optional<Decision> decision = service.getDecisionForString(decisionString);

        assertTrue(decision.isPresent());
        assertEquals(decision.get(), Decision.ON_HOLD);
    }

    @Test
    public void saveDecision_getDecisionForStringShouldReturnEmptyOptionalIfDecisionisUnrecognized() throws Exception {
        String decisionString = "NOT_A_FUNDING_DECISION_AT_ALL";

        Optional<Decision> decision = service.getDecisionForString(decisionString);

        assertTrue(!decision.isPresent());
    }
}
