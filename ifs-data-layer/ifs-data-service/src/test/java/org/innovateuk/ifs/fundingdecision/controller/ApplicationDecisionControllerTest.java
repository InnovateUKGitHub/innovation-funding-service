package org.innovateuk.ifs.fundingdecision.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.Decision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.fundingdecision.transactional.ApplicationFundingNotificationBulkService;
import org.innovateuk.ifs.fundingdecision.transactional.ApplicationFundingService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationDecisionControllerTest extends BaseControllerMockMVCTest<ApplicationDecisionController> {

    @Mock
    private ApplicationFundingService applicationFundingServiceMock;

    @Mock
    private ApplicationFundingNotificationBulkService applicationFundingNotificationBulkService;

    @Override
    protected ApplicationDecisionController supplyControllerUnderTest() {
        return new ApplicationDecisionController();
    }

    @Test
    public void testSaveApplicationDecisionData() throws Exception {
        Long competitionId = 1L;
        Map<Long, Decision> decision = asMap(1L, Decision.FUNDED, 2L, Decision.UNFUNDED);

        when(applicationFundingServiceMock.saveDecisionData(competitionId, decision)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/applicationfunding/1")
        			.contentType(MediaType.APPLICATION_JSON)
        			.content(objectMapper.writeValueAsString(decision)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(applicationFundingServiceMock).saveDecisionData(competitionId, decision);
    }

    @Test
    public void testSendNotificationsShouldReturnAppropriateStatusCode() throws Exception {

        Map<Long, Decision> decisions = asMap(1L, Decision.FUNDED, 2L, Decision.UNFUNDED, 3L, Decision.ON_HOLD);
        FundingNotificationResource notification = new FundingNotificationResource("Body of notification message.", decisions);

        when(applicationFundingNotificationBulkService.sendBulkFundingNotifications(notification)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/applicationfunding/send-notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(applicationFundingNotificationBulkService, times(1)).sendBulkFundingNotifications(notification);
    }
}
