package org.innovateuk.ifs.grants;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.activitylog.transactional.ActivityLogService;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.grants.domain.GrantsFinanceContactInvite;
import org.innovateuk.ifs.grants.domain.GrantsInvite;
import org.innovateuk.ifs.grants.domain.GrantsMonitoringOfficerInvite;
import org.innovateuk.ifs.grants.domain.GrantsProjectManagerInvite;
import org.innovateuk.ifs.grants.repository.GrantsFinanceContactInviteRepository;
import org.innovateuk.ifs.grants.repository.GrantsMonitoringOfficerInviteRepository;
import org.innovateuk.ifs.grants.repository.GrantsProjectManagerInviteRepository;
import org.innovateuk.ifs.grants.transactional.GrantsInviteService;
import org.innovateuk.ifs.grants.transactional.GrantsInviteServiceImpl;
import org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static freemarker.template.utility.Collections12.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource.GrantsInviteRole.GRANTS_PROJECT_MANAGER;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder.newApplicationInvite;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class GrantsInviteServiceImplTest extends BaseServiceUnitTest<GrantsInviteService> {

    @Override
    protected GrantsInviteService supplyServiceUnderTest() { return new GrantsInviteServiceImpl(); }

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private InviteOrganisationRepository inviteOrganisationRepository;

    @Mock
    private GrantsProjectManagerInviteRepository grantsProjectManagerInviteRepository;

    @Mock
    private GrantsFinanceContactInviteRepository grantsFinanceContactInviteRepository;

    @Mock
    private GrantsMonitoringOfficerInviteRepository grantsMonitoringOfficerInviteRepository;

    @Mock
    private ActivityLogService activityLogService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private LoggedInUserSupplier loggedInUserSupplier;

    private long projectId = 1L;

    private Organisation organisation;
    private Application application;
    private Project project;
    private ApplicationInvite applicationInvite;
    private InviteOrganisation inviteOrganisation;
    private GrantsInvite grantsInvite;


    @Before
    public void setup() {
        organisation = newOrganisation().withName("Ludlow").build();
        application = newApplication().build();
        project = newProject()
                .withApplication(application)
                .withPartnerOrganisations(singletonList(newPartnerOrganisation()
                        .withOrganisation(organisation)
                        .withLeadOrganisation(true)
                        .build()))
                .build();
        applicationInvite = newApplicationInvite().withApplication(application).build();
        inviteOrganisation = newInviteOrganisation()
                .withOrganisation(organisation)
                .withOrganisationName(organisation.getName())
                .withInvites(singletonList(applicationInvite))
                .build();
        grantsInvite = new GrantsInvite("Mr.Fly", "test@email.com", "hask3456jk", inviteOrganisation, project, CREATED);
    }

    @Test
    public void sendInviteGrantsProjectManager() {
        GrantsInviteResource invite = new GrantsInviteResource(grantsInvite.getInviteOrganisation().getOrganisationName(),
                grantsInvite.getName(),
                grantsInvite.getEmail(),
                GRANTS_PROJECT_MANAGER);
        GrantsProjectManagerInvite grantsProjectManagerInvite = new GrantsProjectManagerInvite(grantsInvite.getName(),
                grantsInvite.getEmail(),
                grantsInvite.getHash(),
                grantsInvite.getInviteOrganisation(),
                grantsInvite.getProject(),
                grantsInvite.getStatus());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(inviteOrganisationRepository.save(inviteOrganisation)).thenReturn(inviteOrganisation);
        when(grantsProjectManagerInviteRepository.save(grantsProjectManagerInvite)).thenReturn(grantsProjectManagerInvite);
        when(notificationService.sendNotificationWithFlush(any(), any())).thenReturn(serviceSuccess());
        when(loggedInUserSupplier.get()).thenReturn(newUser().build());

        ServiceResult<Void> result = service.sendInvite(projectId, invite);

        assertTrue(result.isSuccess());
    }

    @Test
    public void sendInviteGrantsProjectFinanceContact() {
        GrantsInviteResource invite = new GrantsInviteResource(grantsInvite.getInviteOrganisation().getOrganisationName(),
                grantsInvite.getName(),
                grantsInvite.getEmail(),
                GRANTS_PROJECT_MANAGER);
        GrantsFinanceContactInvite grantsFinanceContactInvite = new GrantsFinanceContactInvite(grantsInvite.getName(),
                grantsInvite.getEmail(),
                grantsInvite.getHash(),
                grantsInvite.getInviteOrganisation(),
                grantsInvite.getProject(),
                grantsInvite.getStatus());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(inviteOrganisationRepository.save(inviteOrganisation)).thenReturn(inviteOrganisation);
        when(grantsFinanceContactInviteRepository.save(grantsFinanceContactInvite)).thenReturn(grantsFinanceContactInvite);
        when(notificationService.sendNotificationWithFlush(any(), any())).thenReturn(serviceSuccess());
        when(loggedInUserSupplier.get()).thenReturn(newUser().build());

        ServiceResult<Void> result = service.sendInvite(projectId, invite);

        assertTrue(result.isSuccess());
    }

    @Test
    public void sendInviteGrantsProjectMonitoringOfficer() {
        GrantsInviteResource invite = new GrantsInviteResource(grantsInvite.getInviteOrganisation().getOrganisationName(),
                grantsInvite.getName(),
                grantsInvite.getEmail(),
                GRANTS_PROJECT_MANAGER);
        GrantsMonitoringOfficerInvite grantsMonitoringOfficerInvite = new GrantsMonitoringOfficerInvite(grantsInvite.getName(),
                grantsInvite.getEmail(),
                grantsInvite.getHash(),
                grantsInvite.getProject(),
                grantsInvite.getStatus());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(inviteOrganisationRepository.save(inviteOrganisation)).thenReturn(inviteOrganisation);
        when(grantsMonitoringOfficerInviteRepository.save(grantsMonitoringOfficerInvite)).thenReturn(grantsMonitoringOfficerInvite);
        when(notificationService.sendNotificationWithFlush(any(), any())).thenReturn(serviceSuccess());
        when(loggedInUserSupplier.get()).thenReturn(newUser().build());

        ServiceResult<Void> result = service.sendInvite(projectId, invite);

        assertTrue(result.isSuccess());
    }

}
