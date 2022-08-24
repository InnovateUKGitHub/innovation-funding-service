package org.innovateuk.ifs.management.decision.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationDecisionToSendApplicationResource;
import org.innovateuk.ifs.application.resource.Decision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.application.service.ApplicationDecisionRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.decision.form.NotificationEmailsForm;
import org.innovateuk.ifs.management.notification.populator.SendNotificationsModelPopulator;
import org.innovateuk.ifs.management.notification.viewmodel.SendNotificationsViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.innovateuk.ifs.application.resource.Decision.*;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.HECP;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CompetitionManagementEOINotificationsControllerTest extends BaseControllerMockMVCTest<CompetitionManagementEOINotificationsController> {

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private SendNotificationsModelPopulator sendNotificationsModelPopulator;

    @Mock
    private ApplicationDecisionRestService applicationDecisionRestService;

    private Long competitionId;
    private Long applicationId1 = 1L;
    private Long applicationId2 = 2L;
    private Long applicationId3 = 3L;
    private CompetitionResource competition;

    @Override
    protected CompetitionManagementEOINotificationsController supplyControllerUnderTest() {
        return new CompetitionManagementEOINotificationsController();
    }

    @Before
    public void setup() {
        competitionId = 123L;
        applicationId1 = 1L;
        applicationId2 = 2L;
        applicationId3 = 3L;

        competition = newCompetitionResource()
                .withId(competitionId)
                .withFundingType(HECP)
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .build();
    }

    @Test
    public void sendNotifications() throws Exception {
        String title = "Eoi Approved Application";
        String lead = "Jofel Consultancy Ltd";

        ApplicationDecisionToSendApplicationResource approvedApplicationDecisionToSend = new ApplicationDecisionToSendApplicationResource(
                applicationId1, title, lead, Decision.EOI_APPROVED);
        ApplicationDecisionToSendApplicationResource rejectedApplicationDecisionToSend = new ApplicationDecisionToSendApplicationResource(
                applicationId2, title, lead, EOI_REJECTED);
        SendNotificationsViewModel sendNotificationsViewModel = new SendNotificationsViewModel(
                Arrays.asList(approvedApplicationDecisionToSend, rejectedApplicationDecisionToSend), competition, true);

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(sendNotificationsModelPopulator.populate(eq(competitionId), eq(Arrays.asList(applicationId1, applicationId2, applicationId3))
                , any(NotificationEmailsForm.class), eq(true))).thenReturn(sendNotificationsViewModel);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/competition/{competitionId}/eoi/notification/send", competitionId)
                        .param("application_ids", applicationId1.toString(), applicationId2.toString(), applicationId3.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-send-notifications"))
                .andReturn();

        SendNotificationsViewModel returnedSendNotificationsViewModel = (SendNotificationsViewModel) result.getModelAndView().getModel().get("model");

        assertNotNull(returnedSendNotificationsViewModel);
        assertTrue(returnedSendNotificationsViewModel.isEoi());
        assertEquals("Send an expression of interest notification", returnedSendNotificationsViewModel.getPageTitle());

        verify(competitionRestService).getCompetitionById(competitionId);
        verify(sendNotificationsModelPopulator).populate(eq(competitionId), eq(Arrays.asList(applicationId1, applicationId2, applicationId3))
                , any(NotificationEmailsForm.class), eq(true));
    }

    @Test
    public void sendNotificationsSubmit() throws Exception {
        String message = "Email message";

        Map<Long, Decision> decisions = new HashMap<>();
        decisions.put(applicationId1, Decision.EOI_APPROVED);
        decisions.put(applicationId2, Decision.EOI_REJECTED);

        NotificationEmailsForm notificationEmailsForm = new NotificationEmailsForm();
        notificationEmailsForm.setMessage(message);
        notificationEmailsForm.setDecisions(decisions);

        FundingNotificationResource fundingNotificationResource = new FundingNotificationResource(message,
                Map.of(applicationId1, EOI_APPROVED, applicationId2, EOI_REJECTED));

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(applicationDecisionRestService.sendApplicationDecisions(fundingNotificationResource)).thenReturn(restSuccess());

        mockMvc.perform(MockMvcRequestBuilders.post("/competition/{competitionId}/eoi/notification/send", competitionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .flashAttr("form", notificationEmailsForm))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/competition/%s/eoi/notification", competitionId)));

        verify(competitionRestService).getCompetitionById(competitionId);
        verify(applicationDecisionRestService).sendApplicationDecisions(fundingNotificationResource);
    }
}
