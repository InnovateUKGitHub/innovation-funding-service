package org.innovateuk.ifs.supporter.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.supporter.domain.SupporterAssignment;
import org.innovateuk.ifs.supporter.mapper.SupporterAssignmentMapper;
import org.innovateuk.ifs.supporter.repository.SupporterAssignmentRepository;
import org.innovateuk.ifs.supporter.resource.*;
import org.innovateuk.ifs.supporter.workflow.SupporterAssignmentWorkflowHandler;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.NotificationMedium;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.workflow.audit.ProcessHistoryRepository;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.LambdaMatcher.lambdaMatches;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.supporter.domain.builder.SupporterAssignmentBuilder.newSupporterAssignment;
import static org.innovateuk.ifs.supporter.domain.builder.SupporterOutcomeBuilder.newSupporterOutcome;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.organisation.builder.SimpleOrganisationBuilder.newSimpleOrganisation;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SupporterAssignmentServiceImplTest extends BaseServiceUnitTest<SupporterAssignmentService> {
    @Mock
    private SupporterAssignmentWorkflowHandler supporterAssignmentWorkflowHandler;

    @Mock
    private SupporterAssignmentRepository supporterAssignmentRepository;

    @Mock
    private ProcessHistoryRepository processHistoryRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private SupporterAssignmentMapper supporterAssignmentMapper;

    @Override
    protected SupporterAssignmentService supplyServiceUnderTest() {
        return new SupporterAssignmentServiceImpl();
    }

    @Test
    public void getAssignment() {
        long userId = 1L;
        long applicationId = 2l;
        SupporterAssignment supporterAssignment = newSupporterAssignment()
                .withProcessState(SupporterState.REJECTED)
                .withSupporterOutcome(newSupporterOutcome()
                        .withComment("Terrible")
                        .build()
                )
                .build();
        SupporterAssignmentResource resource = new SupporterAssignmentResource();
        resource.setAssignmentId(supporterAssignment.getId());
        resource.setComments(supporterAssignment.getSupporterOutcome().getComment());
        resource.setState(supporterAssignment.getProcessState());

        when(supporterAssignmentRepository.findByParticipantIdAndTargetId(userId, applicationId)).thenReturn(of(supporterAssignment));
        when(supporterAssignmentMapper.mapToResource(supporterAssignment)).thenReturn(resource);


        ServiceResult<SupporterAssignmentResource> result = service.getAssignment(userId, applicationId);

        assertThat(result.isSuccess(), equalTo(true));
        assertThat(result.getSuccess().getAssignmentId(), equalTo(supporterAssignment.getId()));
        assertThat(result.getSuccess().getComments(), equalTo("Terrible"));
        assertThat(result.getSuccess().getState(), equalTo(SupporterState.REJECTED));
        assertThat(result.isSuccess(), equalTo(true));
    }

    @Test
    public void assign() {
        long userId = 1L;
        long applicationId = 2l;
        User user = newUser().build();
        Application application = newApplication().withCompetition(newCompetition().build()).build();
        when(supporterAssignmentRepository.existsByParticipantIdAndTargetId(userId, applicationId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(of(user));
        when(applicationRepository.findById(applicationId)).thenReturn(of(application));
        when(supporterAssignmentRepository.save(any())).thenAnswer(inv -> {
            SupporterAssignment c = inv.getArgument(0);
            c.setId(4L);
            return c;
        });
        when(notificationService.sendNotificationWithFlush(any(), eq(NotificationMedium.EMAIL))).thenReturn(serviceSuccess());
        when(supporterAssignmentMapper.mapToResource(any(SupporterAssignment.class))).thenReturn(new SupporterAssignmentResource());

        ServiceResult<SupporterAssignmentResource> result = service.assign(userId, applicationId);

        assertThat(result.isSuccess(), equalTo(true));
        verify(supporterAssignmentRepository).save(any());
        verify(notificationService).sendNotificationWithFlush(any(), eq(NotificationMedium.EMAIL));
    }

    @Test
    public void removeAssignment() {
        long userId = 1L;
        long applicationId = 2l;
        User user = newUser().build();
        Application application = newApplication().withCompetition(newCompetition().build()).build();
        long cofunderAssignmentId = 6L;
        SupporterAssignment supporterAssignment = newSupporterAssignment()
                .withId(supporterAssignmentId)
                .withApplication(application)
                .build();

        when(userRepository.findById(userId)).thenReturn(of(user));
        when(applicationRepository.findById(applicationId)).thenReturn(of(application));
        when(supporterAssignmentRepository.findByParticipantIdAndTargetId(userId, applicationId)).thenReturn(of(supporterAssignment));
        when(notificationService.sendNotificationWithFlush(any(), eq(NotificationMedium.EMAIL))).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.removeAssignment(userId, applicationId);

        assertThat(result.isSuccess(), equalTo(true));
        verify(processHistoryRepository).deleteByProcessId(supporterAssignmentId);
        verify(supporterAssignmentRepository).delete(supporterAssignment);
        verify(notificationService).sendNotificationWithFlush(any(), eq(NotificationMedium.EMAIL));
    }

    @Test
    public void decision_reject() {
        long assignmentId = 1L;
        SupporterDecisionResource decision = new SupporterDecisionResource();
        decision.setComments("Terrible");
        decision.setAccept(false);
        Application application = newApplication().withCompetition(newCompetition().build()).build();
        SupporterAssignment supporterAssignment = newSupporterAssignment()
                .withApplication(application)
                .build();

        when(supporterAssignmentRepository.findById(assignmentId)).thenReturn(of(supporterAssignment));
        when(supporterAssignmentWorkflowHandler.reject(eq(supporterAssignment), any())).thenReturn(true);

        ServiceResult<Void> result = service.decision(assignmentId, decision);

        assertThat(result.isSuccess(), equalTo(true));
        verify(supporterAssignmentWorkflowHandler).reject(eq(supporterAssignment), argThat(lambdaMatches(outcome -> {
            assertThat(outcome.getComment(), equalTo("Terrible"));
            assertThat(outcome.isFundingConfirmation(), equalTo(false));
            return true;
        })));
    }

    @Test
    public void decision_accept() {
        long assignmentId = 1L;
        SupporterDecisionResource decision = new SupporterDecisionResource();
        decision.setComments("Amazing");
        decision.setAccept(true);
        Application application = newApplication().withCompetition(newCompetition().build()).build();
        SupporterAssignment supporterAssignment = newSupporterAssignment()
                .withApplication(application)
                .build();

        when(supporterAssignmentRepository.findById(assignmentId)).thenReturn(of(supporterAssignment));
        when(supporterAssignmentWorkflowHandler.accept(eq(supporterAssignment), any())).thenReturn(true);

        ServiceResult<Void> result = service.decision(assignmentId, decision);

        assertThat(result.isSuccess(), equalTo(true));
        verify(supporterAssignmentWorkflowHandler).accept(eq(supporterAssignment), argThat(lambdaMatches(outcome -> {
            assertThat(outcome.getComment(), equalTo("Amazing"));
            assertThat(outcome.isFundingConfirmation(), equalTo(true));
            return true;
        })));
    }

    @Test
    public void edit() {
        long assignmentId = 1L;
        Application application = newApplication().withCompetition(newCompetition().build()).build();
        SupporterAssignment supporterAssignment = newSupporterAssignment()
                .withApplication(application)
                .build();

        when(supporterAssignmentRepository.findById(assignmentId)).thenReturn(of(supporterAssignment));
        when(supporterAssignmentWorkflowHandler.edit(eq(supporterAssignment))).thenReturn(true);

        ServiceResult<Void> result = service.edit(assignmentId);

        assertThat(result.isSuccess(), equalTo(true));
        verify(supporterAssignmentWorkflowHandler).edit(supporterAssignment);
    }

    @Test
    public void findApplicationsNeedingSupporters() {
        long competitionId = 1L;
        String filter = "filter";
        PageRequest pageRequest = PageRequest.of(0, 1);
        ApplicationsForCofundingResource app = new ApplicationsForCofundingResource();
        Page<ApplicationsForCofundingResource> page = new PageImpl<>(newArrayList(app), pageRequest, 1L);

        when(supporterAssignmentRepository.findApplicationsForCofunding(competitionId, filter, pageRequest)).thenReturn(page);

        ServiceResult<ApplicationsForCofundingPageResource> result = service.findApplicationsNeedingSupporters(competitionId, filter, pageRequest);

        assertThat(result.isSuccess(), equalTo(true));
        assertThat(result.getSuccess().getContent().get(0), equalTo(app));
        assertThat(result.getSuccess().getTotalElements(), equalTo(1L));
        assertThat(result.getSuccess().getTotalPages(), equalTo(1));
        assertThat(result.getSuccess().getNumber(), equalTo(0));
        assertThat(result.getSuccess().getSize(), equalTo(1));
    }

    @Test
    public void findAvailableSupportersForApplication() {
        long applicationId = 1L;
        long unassignedProfileId = 2L;
        long assignedProfileId = 3L;
        String filter = "filter";
        PageRequest pageRequest = PageRequest.of(0, 1);
        User unsassignedUser = newUser()
                .withEmailAddress("myemail@email.com")
                .withFirstName("Bob")
                .withLastName("Bobberson")
                .withProfileId(unassignedProfileId)
                .build();
        User assignedUser = newUser()
                .withEmailAddress("assigned@email.com")
                .withFirstName("Assigned")
                .withLastName("Guy")
                .withProfileId(assignedProfileId)
                .build();
        SupporterAssignment supporterAssignment = newSupporterAssignment()
                .withParticipant(assignedUser)
                .build();
        Page<User> page = new PageImpl<>(newArrayList(unsassignedUser), pageRequest, 1L);
        Profile unassignedProfile = newProfile()
                .withSimpleOrganisation(
                        newSimpleOrganisation()
                                .withName("Simply an organisation")
                                .build()
                ).build();
        Profile assignedProfile = newProfile()
                .withSimpleOrganisation(
                        newSimpleOrganisation()
                                .withName("Simply an assigned organisation")
                                .build()
                ).build();
        when(supporterAssignmentRepository.findUsersAvailableForCofunding(applicationId, filter, pageRequest)).thenReturn(page);
        when(profileRepository.findById(unassignedProfileId)).thenReturn(of(unassignedProfile));
        when(profileRepository.findById(assignedProfileId)).thenReturn(of(assignedProfile));
        when(supporterAssignmentRepository.findByTargetId(applicationId)).thenReturn(Arrays.asList(supporterAssignment));

        ServiceResult<SupportersAvailableForApplicationPageResource> result = service.findAvailableSupportersForApplication(applicationId, filter, pageRequest);

        assertThat(result.isSuccess(), equalTo(true));
        assertThat(result.getSuccess().getContent().get(0).getEmail(), equalTo(unsassignedUser.getEmail()));
        assertThat(result.getSuccess().getContent().get(0).getName(), equalTo("Bob Bobberson"));
        assertThat(result.getSuccess().getContent().get(0).getUserId(), equalTo(unsassignedUser.getId()));
        assertThat(result.getSuccess().getContent().get(0).getOrganisation(), equalTo("Simply an organisation"));
        assertThat(result.getSuccess().getTotalElements(), equalTo(1L));
        assertThat(result.getSuccess().getTotalPages(), equalTo(1));
        assertThat(result.getSuccess().getNumber(), equalTo(0));
        assertThat(result.getSuccess().getSize(), equalTo(1));
        assertThat(result.getSuccess().getAssignedSupporters().get(0).getEmail(), equalTo(assignedUser.getEmail()));
        assertThat(result.getSuccess().getAssignedSupporters().get(0).getName(), equalTo("Assigned Guy"));
        assertThat(result.getSuccess().getAssignedSupporters().get(0).getUserId(), equalTo(assignedUser.getId()));
        assertThat(result.getSuccess().getAssignedSupporters().get(0).getOrganisation(), equalTo("Simply an assigned organisation"));
    }

    @Test
    public void findAvailableSupportersUserIdsForApplication() {
        long applicationId = 4L;
        String filter = "w";
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        when(supporterAssignmentRepository.usersAvailableForCofundingUserIds(applicationId, filter)).thenReturn(ids);

        ServiceResult<List<Long>> result = service.findAvailableSupportersUserIdsForApplication(applicationId, filter);

        assertThat(result.isSuccess(), equalTo(true));
        assertThat(result.getSuccess(), equalTo(ids));
    }
}
