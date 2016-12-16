package org.innovateuk.ifs.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.email.resource.EmailContent;
import org.innovateuk.ifs.invite.resource.AssessorInviteToSendResource;
import org.innovateuk.ifs.management.model.SendInvitePopulator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.email.builders.EmailContentResourceBuilder.newEmailContentResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteToSendResourceBuilder.newAssessorInviteToSendResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CompetitionManagementSendInviteControllerTest extends BaseControllerMockMVCTest<CompetitionManagementSendInviteController> {


    @Spy
    @InjectMocks
    private SendInvitePopulator sendInvitePopulator;

    @Override
    protected CompetitionManagementSendInviteController supplyControllerUnderTest() {
        return new CompetitionManagementSendInviteController();
    }

    @Override
    @Before
    public void setUp() {
        super.setUp();

    }

    @Test
    public void inviteEmail() throws Exception {
        long inviteId = 4L;
        AssessorInviteToSendResource invite = newAssessorInviteToSendResource().build();
        when(competitionInviteRestService.getCreated(inviteId)).thenReturn(restSuccess(invite));

        mockMvc.perform(get("/competition/assessors/invite/{inviteId}", inviteId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/send-invites"));

        verify(competitionInviteRestService, only()).getCreated(inviteId);
    }

    @Test
    public void sendEmail() throws Exception {
        long inviteId = 4L;
        long competitionId = 5L;
        AssessorInviteToSendResource invite = newAssessorInviteToSendResource().withCompetitionId(competitionId).build();
        EmailContent emailContent = newEmailContentResource().build();
        when(competitionInviteRestService.sendInvite(eq(inviteId), any(EmailContent.class))).thenReturn(restSuccess(invite));

        mockMvc.perform(post("/competition/assessors/invite/{inviteId}/send", inviteId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s/assessors/invite", competitionId)));

        verify(competitionInviteRestService, only()).sendInvite(eq(inviteId), any(EmailContent.class));

    }

}
