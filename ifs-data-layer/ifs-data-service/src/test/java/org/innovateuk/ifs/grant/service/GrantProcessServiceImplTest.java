package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.grant.domain.GrantProcess;
import org.innovateuk.ifs.grant.repository.GrantProcessRepository;
import org.junit.Test;
import org.mockito.Mock;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class GrantProcessServiceImplTest extends BaseServiceUnitTest<GrantProcessServiceImpl> {

    @Mock
    private GrantProcessRepository grantProcessRepository;

    @Override
    protected GrantProcessServiceImpl supplyServiceUnderTest() {
        return new GrantProcessServiceImpl();
    }

    @Test
    public void findReadyToSend() {
        GrantProcess grantProcessOne = new GrantProcess(1);
        GrantProcess grantProcessTwo = new GrantProcess(2);
        List<GrantProcess> readyToSend = asList(grantProcessOne, grantProcessTwo);

        when(grantProcessRepository.findByPendingIsTrue()).thenReturn(readyToSend);

        assertThat(service.findReadyToSend(), is(readyToSend));

        verify(grantProcessRepository, only()).findByPendingIsTrue();
    }

    @Test
    public void createGrantProcess() {
        ZonedDateTime now = ZonedDateTime.now();
        long applicationId = 7L;
        GrantProcess grantProcess = new GrantProcess(applicationId);

        when(grantProcessRepository.save(grantProcess)).thenReturn(grantProcess);

        service.createGrantProcess(applicationId);

        verify(grantProcessRepository, only())
                .save(createLambdaMatcher(g -> {
                    assertEquals(applicationId, g.getApplicationId());
                    assertFalse(g.isPending());
                    assertNull(g.getMessage());
                    assertNull(g.getSentRequested());
                    assertNull(g.getSentSucceeded());
                    assertNull(g.getLastProcessed());
                }));
    }

    @Test
    public void sendSucceeded() {
        ZonedDateTime now = ZonedDateTime.now();
        long applicationId = 7L;
        GrantProcess grantProcess = new GrantProcess(applicationId);

        when(grantProcessRepository.findOneByApplicationId(applicationId)).thenReturn(grantProcess);
        when(grantProcessRepository.save(grantProcess)).thenReturn(grantProcess);

        service.sendSucceeded(applicationId);

        verify(grantProcessRepository).findOneByApplicationId(applicationId);
        verify(grantProcessRepository).save(createLambdaMatcher(g -> {
            assertEquals(applicationId, g.getApplicationId());
            assertFalse(g.isPending());
            assertNull(g.getMessage());
            assertNull(g.getSentRequested());
            assertNotNull(g.getSentSucceeded());
            assertNull(g.getLastProcessed());
        }));
    }

    @Test
    public void sendFailed() {
        ZonedDateTime now = ZonedDateTime.now();
        long applicationId = 7L;
        String message = "message";
        GrantProcess grantProcess = new GrantProcess(applicationId);

        when(grantProcessRepository.findOneByApplicationId(applicationId)).thenReturn(grantProcess);
        when(grantProcessRepository.save(grantProcess)).thenReturn(grantProcess);

        service.sendFailed(applicationId, message);

        verify(grantProcessRepository).findOneByApplicationId(applicationId);
        verify(grantProcessRepository).save(createLambdaMatcher(g -> {
            assertEquals(applicationId, g.getApplicationId());
            assertFalse(g.isPending());
            assertEquals(message, g.getMessage());
            assertNull(g.getSentRequested());
            assertNull(g.getSentSucceeded());
            assertNotNull(g.getLastProcessed());
        }));
    }
}