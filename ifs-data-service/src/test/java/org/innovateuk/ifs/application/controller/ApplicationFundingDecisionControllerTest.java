package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.commons.rest.RestErrorResponse;
import org.innovateuk.ifs.util.MapFunctions;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationFundingDecisionControllerTest extends BaseControllerMockMVCTest<ApplicationFundingDecisionController> {

    @Override
    protected ApplicationFundingDecisionController supplyControllerUnderTest() {
        return new ApplicationFundingDecisionController();
    }

    @Test
    public void testSaveApplicationFundingDecisionData() throws Exception {
        Long competitionId = 1L;
        Map<Long, FundingDecision> decision = MapFunctions.asMap(1L, FundingDecision.FUNDED, 2L, FundingDecision.UNFUNDED);

        when(applicationFundingServiceMock.saveFundingDecisionData(competitionId, decision)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/applicationfunding/1")
        			.contentType(MediaType.APPLICATION_JSON)
        			.content(objectMapper.writeValueAsString(decision)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void testSendNotificationsShouldReturnAppropriateStatusCode() throws Exception {

        Map<Long, FundingDecision> decisions = MapFunctions.asMap(1L, FundingDecision.FUNDED, 2L, FundingDecision.UNFUNDED, 3L, FundingDecision.ON_HOLD);
        FundingNotificationResource notification = new FundingNotificationResource("Body of notification message.", decisions);

        when(projectServiceMock.createProjectsFromFundingDecisions(decisions)).thenReturn(serviceSuccess());
        when(applicationFundingServiceMock.notifyLeadApplicantsOfFundingDecisions(notification)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/applicationfunding/sendNotifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void testSendNotificationsButErrorOccursCreatingProjects() throws Exception {

        Map<Long, FundingDecision> decisions = MapFunctions.asMap(1L, FundingDecision.FUNDED, 2L, FundingDecision.UNFUNDED, 3L, FundingDecision.ON_HOLD);
        FundingNotificationResource notification = new FundingNotificationResource("Body of notification message.", decisions);

        when(projectServiceMock.createProjectsFromFundingDecisions(decisions)).thenReturn(serviceFailure(internalServerErrorError()));

        mockMvc.perform(post("/applicationfunding/sendNotifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(toJson(new RestErrorResponse(internalServerErrorError()))));

        verify(applicationFundingServiceMock, never()).notifyLeadApplicantsOfFundingDecisions(any(FundingNotificationResource.class));
    }

    @Test
    public void testSendNotificationsButErrorOccursSendingNotifications() throws Exception {

        Map<Long, FundingDecision> decisions = MapFunctions.asMap(1L, FundingDecision.FUNDED, 2L, FundingDecision.UNFUNDED, 3L, FundingDecision.ON_HOLD);
        FundingNotificationResource notification = new FundingNotificationResource("Body of notification message.", decisions);

        when(projectServiceMock.createProjectsFromFundingDecisions(decisions)).thenReturn(serviceSuccess());
        when(applicationFundingServiceMock.notifyLeadApplicantsOfFundingDecisions(notification)).thenReturn(serviceFailure(internalServerErrorError()));

        mockMvc.perform(post("/applicationfunding/sendNotifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(toJson(new RestErrorResponse(internalServerErrorError()))));
    }
}
