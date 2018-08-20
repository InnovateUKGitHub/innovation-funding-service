package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationIneligibleSendResource;
import org.innovateuk.ifs.application.resource.ApplicationNotificationTemplateResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationNotificationTemplateRestService;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.management.ineligible.controller.CompetitionManagementSendIneligibleController;
import org.innovateuk.ifs.management.ineligible.form.InformIneligibleForm;
import org.innovateuk.ifs.management.ineligible.populator.InformIneligibleModelPopulator;
import org.innovateuk.ifs.management.ineligible.viewmodel.InformIneligibleViewModel;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationIneligibleSendResourceBuilder.newApplicationIneligibleSendResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.resource.ApplicationState.INELIGIBLE;
import static org.innovateuk.ifs.application.resource.ApplicationState.OPEN;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.resource.Role.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CompetitionManagementSendIneligibleControllerTest extends BaseControllerMockMVCTest<CompetitionManagementSendIneligibleController> {

    @Spy
    @InjectMocks
    private InformIneligibleModelPopulator informIneligibleModelPopulator;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private ApplicationNotificationTemplateRestService applicationNotificationTemplateRestService;

    @Override
    protected CompetitionManagementSendIneligibleController supplyControllerUnderTest() {
        return new CompetitionManagementSendIneligibleController();
    }

    @Test
    public void getSendIneligible() throws Exception {
        long applicationId = 1L;
        long competitionId = 2L;
        String competitionName = "competition";
        String applicationName = "application";
        String leadApplicant = "lead applicant";

        ApplicationResource applicationResource = newApplicationResource()
                .withId(applicationId)
                .withApplicationState(INELIGIBLE)
                .withCompetitionName(competitionName)
                .withName(applicationName)
                .withCompetition(competitionId)
                .build();
        List<ProcessRoleResource> processRoles = newProcessRoleResource()
                .withRoleName(COLLABORATOR.getName(), LEADAPPLICANT.getName(), COLLABORATOR.getName())
                .withUserName("other", leadApplicant, "an other")
                .withUserId(1L, 2L, 3L)
                .build(3);

        InformIneligibleViewModel expectedViewModel =
                new InformIneligibleViewModel(competitionId, applicationId, competitionName, applicationName, leadApplicant);
        InformIneligibleForm expectedForm = new InformIneligibleForm();
        expectedForm.setMessage("MessageBody");
        expectedForm.setSubject(String.format("Notification regarding your application %s: %s", applicationResource.getId(), applicationResource.getName()));

        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(applicationResource));
        when(userRestService.findProcessRole(applicationId)).thenReturn(restSuccess(processRoles));
        when(applicationNotificationTemplateRestService.getIneligibleNotificationTemplate(competitionId))
                .thenReturn(restSuccess(new ApplicationNotificationTemplateResource("MessageBody")));

        mockMvc.perform(get("/competition/application/{applicationId}/ineligible", applicationId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().attribute("form", expectedForm));
        verify(applicationRestService, only()).getApplicationById(applicationId);
        verify(userRestService, only()).findProcessRole(applicationId);
    }

    @Test
    public void getSendIneligibleWrongState() throws Exception {
        long applicationId = 1L;
        long competitionId = 2L;

        ApplicationResource applicationResource = newApplicationResource()
                .withApplicationState(OPEN)
                .withId(applicationId)
                .withCompetition(competitionId)
                .build();

        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(applicationResource));

        mockMvc.perform(get("/competition/application/{applicationId}/ineligible", applicationId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/" + competitionId + "/applications/ineligible"));

        verify(applicationRestService, only()).getApplicationById(applicationId);
    }

    @Test
    public void sendEmail() throws Exception {
        long applicationId = 1L;
        long competitionId = 2L;
        String subject = "subject";
        String message = "message";

        ApplicationResource applicationResource = newApplicationResource()
                .withId(applicationId)
                .withCompetition(competitionId)
                .build();

        ApplicationIneligibleSendResource expectedSendResource = newApplicationIneligibleSendResource()
                .withSubject(subject)
                .withMessage(message)
                .build();

        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(applicationResource));
        when(applicationRestService.informIneligible(applicationId, expectedSendResource)).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/application/{applicationId}/ineligible/send", applicationId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("subject", subject)
                .param("message", message))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/" + competitionId + "/applications/ineligible"));

        InOrder inOrder = inOrder(applicationRestService);
        inOrder.verify(applicationRestService).getApplicationById(applicationId);
        inOrder.verify(applicationRestService).informIneligible(applicationId, expectedSendResource);
        inOrder.verifyNoMoreInteractions();
    }
}
