package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.crm.transactional.CrmService;
import org.innovateuk.ifs.grant.domain.GrantProcess;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.schedule.transactional.ScheduleResponse;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.sil.grant.resource.Participant;
import org.innovateuk.ifs.sil.grant.service.GrantEndpoint;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.transactional.UserService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.ProjectParticipantRole.*;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests around the {@link GrantServiceImpl}.
 */
public class GrantServiceImplTest extends BaseServiceUnitTest<GrantServiceImpl> {
    private static final long APPLICATION_ID = 9L;

    @Mock
    private GrantEndpoint grantEndpoint;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private GrantProcessService grantProcessService;

    @Mock
    private GrantMapper grantMapper;

    @Mock
    private UserService userService;

    @Mock
    private CrmService crmService;

    @Override
    protected GrantServiceImpl supplyServiceUnderTest() {
        return new GrantServiceImpl();
    }

    @Test
    public void sendReadyProjects() {
        User user = newUser()
                .withFirstName("A")
                .withLastName("B")
                .withEmailAddress("a@b.com")
                .withUid("uid")
                .build();

        setLoggedInUser(newUserResource()
                .withId(user.getId())
                .build());

        Organisation organisation1 = newOrganisation().build();
        Organisation organisation2 = newOrganisation().build();

        ProjectUser projectManager = newProjectUser()
                .withRole(PROJECT_MANAGER)
                .withUser(user)
                .withOrganisation(organisation1)
                .build();

        ProjectUser financeContactOrg1 = newProjectUser()
                .withRole(PROJECT_FINANCE_CONTACT)
                .withUser(newUser().withUid("financeContactOrg1").build())
                .withOrganisation(organisation1)
                .build();

        ProjectUser normalPartnerOrg1 = newProjectUser()
                .withRole(PROJECT_PARTNER)
                .withUser(newUser().withUid("normalPartnerOrg1").build())
                .withOrganisation(organisation1)
                .build();

        ProjectUser financeContactOrg2 = newProjectUser()
                .withRole(PROJECT_FINANCE_CONTACT)
                .withUser(newUser().withUid("financeContactOrg2").build())
                .withOrganisation(organisation2)
                .build();

        ProjectUser normalPartnerOrg2 = newProjectUser()
                .withRole(PROJECT_PARTNER)
                .withUser(newUser().withUid("normalPartnerOrg2").build())
                .withOrganisation(organisation2)
                .build();

        List<ProjectUser> projectUsers =
                asList(projectManager, financeContactOrg1, normalPartnerOrg1, financeContactOrg2, normalPartnerOrg2);

        List<Participant> participants = projectUsers.stream()
                .map(projectUser -> {
                    Participant participant = new Participant();
                    participant.setContactId(projectUser.getId());
                    return participant;
                }).collect(Collectors.toList());


        Project project = newProject()
                .withId(1L)
                .withDuration(12L)
                .withProjectUsers(projectUsers)
                .withPartnerOrganisations(newPartnerOrganisation()
                        .withOrganisation(organisation1, organisation2)
                        .build(2)
                )
                .withApplication(
                        newApplication()
                                .withId(APPLICATION_ID)
                                .withCompetition(
                                        newCompetition().withId(2L).build())
                                .build()).build();
        GrantProcess process = new GrantProcess(APPLICATION_ID);
        Grant grant = new Grant().id(APPLICATION_ID);
        grant.setParticipants(participants);

        when(projectRepository.findOneByApplicationId(APPLICATION_ID)).thenReturn(project);
        when(crmService.syncCrmContact(anyLong())).thenReturn(serviceSuccess());
        when(grantMapper.mapToGrant(project)).thenReturn(grant);
        when(grantEndpoint.send(grant)).thenReturn(serviceSuccess());
        when(grantProcessService.findOneReadyToSend()).thenReturn(of(process));

        ServiceResult<ScheduleResponse> result = service.sendReadyProjects();

        assertThat(result.isSuccess(), equalTo(true));

        ScheduleResponse scheduleResponse = result.getSuccess();
        assertThat(scheduleResponse.getResponse(), equalTo("Project sent: " + APPLICATION_ID));

        verify(grantEndpoint, only()).send(createLambdaMatcher(matchGrant(project)));

        // assert that the Project Manager and the Finance Contacts for each Partner Organisation are granted access to
        // Live Projects
        assertTrue(projectManager.getUser().hasRole(Role.LIVE_PROJECTS_USER));
        assertTrue(financeContactOrg1.getUser().hasRole(Role.LIVE_PROJECTS_USER));
        assertTrue(financeContactOrg2.getUser().hasRole(Role.LIVE_PROJECTS_USER));

        // assert that "normal" Partner users are NOT granted access to Live Projects
        assertFalse(normalPartnerOrg1.getUser().hasRole(Role.LIVE_PROJECTS_USER));
        assertFalse(normalPartnerOrg2.getUser().hasRole(Role.LIVE_PROJECTS_USER));

        verify(userService).evictUserCache("uid");
        verify(userService).evictUserCache("financeContactOrg1");
        verify(userService).evictUserCache("financeContactOrg2");
        verifyNoMoreInteractions(userService);
        verify(crmService, times(5)).syncCrmContact(anyLong());
    }

    @Test
    public void syncParticipantsFailed() {
        User user = newUser()
                .withFirstName("A")
                .withLastName("B")
                .withEmailAddress("a@b.com")
                .withUid("uid")
                .build();

        setLoggedInUser(newUserResource()
                .withId(user.getId())
                .build());

        Organisation organisation1 = newOrganisation().build();
        Organisation organisation2 = newOrganisation().build();

        ProjectUser projectManager = newProjectUser()
                .withRole(PROJECT_MANAGER)
                .withUser(user)
                .withOrganisation(organisation1)
                .build();

        ProjectUser financeContactOrg1 = newProjectUser()
                .withRole(PROJECT_FINANCE_CONTACT)
                .withUser(newUser().withUid("financeContactOrg1").build())
                .withOrganisation(organisation1)
                .build();

        ProjectUser normalPartnerOrg1 = newProjectUser()
                .withRole(PROJECT_PARTNER)
                .withUser(newUser().withUid("normalPartnerOrg1").build())
                .withOrganisation(organisation1)
                .build();

        ProjectUser financeContactOrg2 = newProjectUser()
                .withRole(PROJECT_FINANCE_CONTACT)
                .withUser(newUser().withUid("financeContactOrg2").build())
                .withOrganisation(organisation2)
                .build();

        ProjectUser normalPartnerOrg2 = newProjectUser()
                .withRole(PROJECT_PARTNER)
                .withUser(newUser().withUid("normalPartnerOrg2").build())
                .withOrganisation(organisation2)
                .build();

        List<ProjectUser> projectUsers =
                asList(projectManager, financeContactOrg1, normalPartnerOrg1, financeContactOrg2, normalPartnerOrg2);

        List<Participant> participants = projectUsers.stream()
                .map(projectUser -> {
                    Participant participant = new Participant();
                    participant.setContactId(projectUser.getId());
                    return participant;
                }).collect(Collectors.toList());


        Project project = newProject()
                .withId(1L)
                .withDuration(12L)
                .withProjectUsers(projectUsers)
                .withPartnerOrganisations(newPartnerOrganisation()
                        .withOrganisation(organisation1, organisation2)
                        .build(2)
                )
                .withApplication(
                        newApplication()
                                .withId(APPLICATION_ID)
                                .withCompetition(
                                        newCompetition().withId(2L).build())
                                .build()).build();
        GrantProcess process = new GrantProcess(APPLICATION_ID);
        Grant grant = new Grant().id(APPLICATION_ID);
        grant.setParticipants(participants);

        when(projectRepository.findOneByApplicationId(APPLICATION_ID)).thenReturn(project);
        when(crmService.syncCrmContact(anyLong())).thenReturn(
                serviceFailure(new Error("sync participants failed", HttpStatus.INTERNAL_SERVER_ERROR)));
        when(grantMapper.mapToGrant(project)).thenReturn(grant);
        when(grantProcessService.findOneReadyToSend()).thenReturn(of(process));
        doNothing().when(grantProcessService).sendFailed(anyLong(), anyString());

        ServiceResult<ScheduleResponse> result = service.sendReadyProjects();
        assertThat(result.isSuccess(), equalTo(true));

        ScheduleResponse scheduleResponse = result.getSuccess();
        assertThat(scheduleResponse.getResponse(), equalTo("Project send failed: " + APPLICATION_ID));

        verify(crmService, times(1)).syncCrmContact(anyLong());
        verify(grantProcessService).findOneReadyToSend();
        verify(grantProcessService).sendFailed(anyLong(), anyString());
        verifyNoMoreInteractions(grantProcessService);
        verifyZeroInteractions(grantEndpoint);
    }

    private static Predicate<Grant> matchGrant(Project project) {
        return grant -> {
            assertThat(grant.getId(), equalTo(project.getApplication().getId()));
            return true;
        };
    }
}
