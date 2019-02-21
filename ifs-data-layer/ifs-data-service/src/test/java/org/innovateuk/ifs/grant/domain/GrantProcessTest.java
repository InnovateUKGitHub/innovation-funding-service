package org.innovateuk.ifs.grant.domain;

import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;
import static org.junit.Assert.*;

public class GrantProcessTest {

    private static final long APPLICATION_ID = 5L;
    private static final ZonedDateTime SEND_REQUESTED_DATE = now();
    private static final ZonedDateTime SEND_SUCCEEDED_DATE = now().plus(Duration.ofHours(1));
    private static final ZonedDateTime SEND_FAILED_DATE = now().plus(Duration.ofHours(2));
    private static final String SEND_FAILED_MESSAGE = "send failed";

    private GrantProcess grantProcess;
    private GrantProcess sendRequestedGrantProcess;
    private GrantProcess sendSucceededGrantProcess;
    private GrantProcess sendFailedGrantProcess;

    @Before
    public void setUp() throws Exception {
        grantProcess = new GrantProcess(APPLICATION_ID);
        sendRequestedGrantProcess = new GrantProcess(APPLICATION_ID).requestSend(SEND_REQUESTED_DATE);
        sendSucceededGrantProcess = new GrantProcess(APPLICATION_ID).requestSend(SEND_REQUESTED_DATE).sendSucceeded(SEND_SUCCEEDED_DATE);
        sendFailedGrantProcess = new GrantProcess(APPLICATION_ID).requestSend(SEND_REQUESTED_DATE).sendFailed(SEND_FAILED_DATE, SEND_FAILED_MESSAGE);
    }

    @Test
    public void getApplicationId() {
        assertEquals(APPLICATION_ID, grantProcess.getApplicationId());
        assertEquals(APPLICATION_ID, sendRequestedGrantProcess.getApplicationId());
        assertEquals(APPLICATION_ID, sendSucceededGrantProcess.getApplicationId());
        assertEquals(APPLICATION_ID, sendFailedGrantProcess.getApplicationId());
    }

    @Test
    public void getSentRequested() {
        assertNull(grantProcess.getSentRequested());
        assertEquals(SEND_REQUESTED_DATE, sendRequestedGrantProcess.getSentRequested());
        assertEquals(SEND_REQUESTED_DATE, sendSucceededGrantProcess.getSentRequested());
        assertEquals(SEND_REQUESTED_DATE, sendFailedGrantProcess.getSentRequested());
    }

    @Test
    public void getSentSucceeded() {
        assertNull(grantProcess.getSentSucceeded());
        assertNull(sendRequestedGrantProcess.getSentSucceeded());
        assertEquals(SEND_SUCCEEDED_DATE, sendSucceededGrantProcess.getSentSucceeded());
        assertNull(sendFailedGrantProcess.getSentSucceeded());
    }

    @Test
    public void isPending() {
        assertTrue(grantProcess.isPending());
        assertFalse(sendRequestedGrantProcess.isPending());
        assertTrue(sendSucceededGrantProcess.isPending());
        assertFalse(sendFailedGrantProcess.isPending());
    }

    @Test
    public void getMessage() {
        assertNull(grantProcess.getMessage());
        assertNull(sendRequestedGrantProcess.getMessage());
        assertNull(sendSucceededGrantProcess.getMessage());
        assertEquals(SEND_FAILED_MESSAGE, sendFailedGrantProcess.getMessage());
    }

    @Test
    public void getLastProcessed() {
        assertNull(grantProcess.getLastProcessed());
        assertNull(sendRequestedGrantProcess.getLastProcessed());
        assertNull(sendSucceededGrantProcess.getLastProcessed());
        assertEquals(SEND_FAILED_DATE, sendFailedGrantProcess.getLastProcessed());
    }
}