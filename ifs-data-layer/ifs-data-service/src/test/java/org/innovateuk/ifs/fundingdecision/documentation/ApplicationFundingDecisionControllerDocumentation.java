package org.innovateuk.ifs.fundingdecision.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.fundingdecision.controller.ApplicationFundingDecisionController;
import org.innovateuk.ifs.fundingdecision.transactional.ApplicationFundingNotificationBulkService;
import org.innovateuk.ifs.fundingdecision.transactional.ApplicationFundingService;
import org.innovateuk.ifs.util.MapFunctions;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.innovateuk.ifs.application.resource.FundingDecision.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.FundingNotificationResourceDocs.fundingNotificationResourceFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedRequestFields;

public class ApplicationFundingDecisionControllerDocumentation extends BaseControllerMockMVCTest<ApplicationFundingDecisionController> {

    @Mock
    private ApplicationFundingService applicationFundingService;

    @Mock
    private ApplicationFundingNotificationBulkService applicationFundingNotificationBulkService;

    @Override
    protected ApplicationFundingDecisionController supplyControllerUnderTest() {
        return new ApplicationFundingDecisionController();
    }

    @Test
    public void saveFundingDecisionData() throws Exception {
        Long competitionId = 1L;
        Map<Long, FundingDecision> decision = MapFunctions.asMap(1L, FUNDED, 2L, UNFUNDED);

        when(applicationFundingService.saveFundingDecisionData(competitionId, decision)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/applicationfunding/1")
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(decision)));
    }

    @Test
    public void sendNotifications() throws Exception {
        Map<Long, FundingDecision> decisions = MapFunctions.asMap(1L, FUNDED, 2L, UNFUNDED, 3L, ON_HOLD);
        FundingNotificationResource notification = new FundingNotificationResource("Body of notification message.", decisions);

        when(applicationFundingNotificationBulkService.sendBulkFundingNotifications(notification)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/applicationfunding/send-notifications")
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)));
    }
}
