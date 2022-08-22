package org.innovateuk.ifs.fundingdecision.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.Decision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.fundingdecision.controller.ApplicationDecisionController;
import org.innovateuk.ifs.fundingdecision.transactional.ApplicationFundingNotificationBulkService;
import org.innovateuk.ifs.fundingdecision.transactional.ApplicationFundingService;
import org.innovateuk.ifs.util.MapFunctions;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.innovateuk.ifs.application.resource.Decision.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class ApplicationDecisionControllerDocumentation extends BaseControllerMockMVCTest<ApplicationDecisionController> {

    @Mock
    private ApplicationFundingService applicationFundingService;

    @Mock
    private ApplicationFundingNotificationBulkService applicationFundingNotificationBulkService;

    @Override
    protected ApplicationDecisionController supplyControllerUnderTest() {
        return new ApplicationDecisionController();
    }

    @Test
    public void saveDecisionData() throws Exception {
        Long competitionId = 1L;
        Map<Long, Decision> decision = MapFunctions.asMap(1L, FUNDED, 2L, UNFUNDED);

        when(applicationFundingService.saveDecisionData(competitionId, decision)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/applicationfunding/1")
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(decision)));
    }

    @Test
    public void sendNotifications() throws Exception {
        Map<Long, Decision> decisions = MapFunctions.asMap(1L, FUNDED, 2L, UNFUNDED, 3L, ON_HOLD);
        FundingNotificationResource notification = new FundingNotificationResource("Body of notification message.", decisions);

        when(applicationFundingNotificationBulkService.sendBulkFundingNotifications(notification)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/applicationfunding/send-notifications")
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)));
    }
}
