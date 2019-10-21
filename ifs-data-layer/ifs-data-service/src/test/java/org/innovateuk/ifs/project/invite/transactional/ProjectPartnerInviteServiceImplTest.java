package org.innovateuk.ifs.project.invite.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.invite.repository.ProjectPartnerInviteRepository;
import org.innovateuk.ifs.project.invite.resource.ProjectPartnerInviteResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.Optional.of;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@RunWith(MockitoJUnitRunner.class)
public class ProjectPartnerInviteServiceImplTest {

    @InjectMocks
    private ProjectPartnerInviteServiceImpl service;

    @Mock
    private ProjectPartnerInviteRepository projectPartnerInviteRepository;

    @Mock
    private InviteOrganisationRepository inviteOrganisationRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private SystemNotificationSource systemNotificationSource;

    @Mock
    private ProjectInviteValidator projectInviteValidator;

    @Test
    public void invite() {
        setField(service, "webBaseUrl", "webBaseUrl");
        long projectId = 1L;
        String organisationName = "Org";
        String userName = "Someone";
        String email = "someone@gmail.com";
        Application application = newApplication().build();
        Organisation leadOrg = newOrganisation()
                .withName("Lead org")
                .build();
        ProjectPartnerInviteResource invite = new ProjectPartnerInviteResource(organisationName, userName, email);
        Project project = newProject()
                .withName("Project")
                .withApplication(application)
                .withPartnerOrganisations(newPartnerOrganisation()
                        .withOrganisation(leadOrg)
                        .withLeadOrganisation(true)
                        .build(1))
                .build();

        when(projectRepository.findById(projectId)).thenReturn(of(project));
        when(projectInviteValidator.validate(projectId, invite)).thenReturn(serviceSuccess());
        when(inviteOrganisationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(projectPartnerInviteRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, Object> notificationArguments = new HashMap<>();
        notificationArguments.put("inviteUrl", "webBaseUrl");
        notificationArguments.put("applicationId", application.getId());
        notificationArguments.put("projectName", "Project");
        notificationArguments.put("leadOrganisationName", "Lead org");

        NotificationTarget to = new UserNotificationTarget(userName, email);

        Notification notification = new Notification(systemNotificationSource, singletonList(to), ProjectPartnerInviteServiceImpl.Notifications.INVITE_PROJECT_PARTNER_ORGANISATION, notificationArguments);
        when(notificationService.sendNotificationWithFlush(notification, NotificationMedium.EMAIL)).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.invitePartnerOrganisation(projectId, invite);

        assertTrue(result.isSuccess());
        verify(notificationService).sendNotificationWithFlush(notification, NotificationMedium.EMAIL);
    }
}
