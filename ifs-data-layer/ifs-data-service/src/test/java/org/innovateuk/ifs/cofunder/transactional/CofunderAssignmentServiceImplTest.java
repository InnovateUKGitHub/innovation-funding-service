package org.innovateuk.ifs.cofunder.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.cofunder.domain.CofunderAssignment;
import org.innovateuk.ifs.cofunder.mapper.CofunderAssignmentMapper;
import org.innovateuk.ifs.cofunder.repository.CofunderAssignmentRepository;
import org.innovateuk.ifs.cofunder.resource.*;
import org.innovateuk.ifs.cofunder.workflow.CofunderAssignmentWorkflowHandler;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.NotificationMedium;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
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
import static org.innovateuk.ifs.cofunder.domain.builder.CofunderAssignmentBuilder.newCofunderAssignment;
import static org.innovateuk.ifs.cofunder.domain.builder.CofunderOutcomeBuilder.newCofunderOutcome;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.organisation.builder.SimpleOrganisationBuilder.newSimpleOrganisation;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CofunderAssignmentServiceImplTest extends BaseServiceUnitTest<CofunderAssignmentService> {
    @Mock
    private CofunderAssignmentWorkflowHandler cofunderAssignmentWorkflowHandler;

    @Mock
    private CofunderAssignmentRepository cofunderAssignmentRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private CofunderAssignmentMapper cofunderAssignmentMapper;

    @Override
    protected CofunderAssignmentService supplyServiceUnderTest() {
        return new CofunderAssignmentServiceImpl();
    }

    @Test
    public void getAssignment() {
        long userId = 1L;
        long applicationId = 2l;
        CofunderAssignment cofunderAssignment = newCofunderAssignment()
                .withProcessState(CofunderState.REJECTED)
                .withCofunderOutcome(newCofunderOutcome()
                        .withComment("Terrible")
                        .build()
                )
                .build();
        CofunderAssignmentResource resource = new CofunderAssignmentResource();
        resource.setAssignmentId(cofunderAssignment.getId());
        resource.setComments(cofunderAssignment.getCofunderOutcome().getComment());
        resource.setState(cofunderAssignment.getProcessState());

        when(cofunderAssignmentRepository.findByParticipantIdAndTargetId(userId, applicationId)).thenReturn(of(cofunderAssignment));
        when(cofunderAssignmentMapper.mapToResource(cofunderAssignment)).thenReturn(resource);


        ServiceResult<CofunderAssignmentResource> result = service.getAssignment(userId, applicationId);

        assertThat(result.isSuccess(), equalTo(true));
        assertThat(result.getSuccess().getAssignmentId(), equalTo(cofunderAssignment.getId()));
        assertThat(result.getSuccess().getComments(), equalTo("Terrible"));
        assertThat(result.getSuccess().getState(), equalTo(CofunderState.REJECTED));
        assertThat(result.isSuccess(), equalTo(true));
    }

    @Test
    public void assign() {
        long userId = 1L;
        long applicationId = 2l;
        User user = newUser().build();
        Application application = newApplication().withCompetition(newCompetition().build()).build();
        when(cofunderAssignmentRepository.existsByParticipantIdAndTargetId(userId, applicationId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(of(user));
        when(applicationRepository.findById(applicationId)).thenReturn(of(application));
        when(cofunderAssignmentRepository.save(any())).thenAnswer(inv -> {
            CofunderAssignment c = inv.getArgument(0);
            c.setId(4L);
            return c;
        });
        when(notificationService.sendNotificationWithFlush(any(), eq(NotificationMedium.EMAIL))).thenReturn(serviceSuccess());
        when(cofunderAssignmentMapper.mapToResource(any(CofunderAssignment.class))).thenReturn(new CofunderAssignmentResource());

        ServiceResult<CofunderAssignmentResource> result = service.assign(userId, applicationId);

        assertThat(result.isSuccess(), equalTo(true));
        verify(cofunderAssignmentRepository).save(any());
        verify(notificationService).sendNotificationWithFlush(any(), eq(NotificationMedium.EMAIL));
    }

    @Test
    public void removeAssignment() {
        long userId = 1L;
        long applicationId = 2l;
        User user = newUser().build();
        Application application = newApplication().withCompetition(newCompetition().build()).build();
        CofunderAssignment cofunderAssignment = newCofunderAssignment()
                .withApplication(application)
                .build();

        when(userRepository.findById(userId)).thenReturn(of(user));
        when(applicationRepository.findById(applicationId)).thenReturn(of(application));
        when(cofunderAssignmentRepository.findByParticipantIdAndTargetId(userId, applicationId)).thenReturn(of(cofunderAssignment));
        when(notificationService.sendNotificationWithFlush(any(), eq(NotificationMedium.EMAIL))).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.removeAssignment(userId, applicationId);

        assertThat(result.isSuccess(), equalTo(true));
        verify(cofunderAssignmentRepository).delete(cofunderAssignment);
        verify(notificationService).sendNotificationWithFlush(any(), eq(NotificationMedium.EMAIL));
    }

    @Test
    public void decision_reject() {
        long assignmentId = 1L;
        CofunderDecisionResource decision = new CofunderDecisionResource();
        decision.setComments("Terrible");
        decision.setAccept(false);
        Application application = newApplication().withCompetition(newCompetition().build()).build();
        CofunderAssignment cofunderAssignment = newCofunderAssignment()
                .withApplication(application)
                .build();

        when(cofunderAssignmentRepository.findById(assignmentId)).thenReturn(of(cofunderAssignment));
        when(cofunderAssignmentWorkflowHandler.reject(eq(cofunderAssignment), any())).thenReturn(true);

        ServiceResult<Void> result = service.decision(assignmentId, decision);

        assertThat(result.isSuccess(), equalTo(true));
        verify(cofunderAssignmentWorkflowHandler).reject(eq(cofunderAssignment), argThat(lambdaMatches(outcome -> {
            assertThat(outcome.getComment(), equalTo("Terrible"));
            assertThat(outcome.isFundingConfirmation(), equalTo(false));
            return true;
        })));
    }

    @Test
    public void decision_accept() {
        long assignmentId = 1L;
        CofunderDecisionResource decision = new CofunderDecisionResource();
        decision.setComments("Amazing");
        decision.setAccept(true);
        Application application = newApplication().withCompetition(newCompetition().build()).build();
        CofunderAssignment cofunderAssignment = newCofunderAssignment()
                .withApplication(application)
                .build();

        when(cofunderAssignmentRepository.findById(assignmentId)).thenReturn(of(cofunderAssignment));
        when(cofunderAssignmentWorkflowHandler.accept(eq(cofunderAssignment), any())).thenReturn(true);

        ServiceResult<Void> result = service.decision(assignmentId, decision);

        assertThat(result.isSuccess(), equalTo(true));
        verify(cofunderAssignmentWorkflowHandler).accept(eq(cofunderAssignment), argThat(lambdaMatches(outcome -> {
            assertThat(outcome.getComment(), equalTo("Amazing"));
            assertThat(outcome.isFundingConfirmation(), equalTo(true));
            return true;
        })));
    }

    @Test
    public void edit() {
        long assignmentId = 1L;
        Application application = newApplication().withCompetition(newCompetition().build()).build();
        CofunderAssignment cofunderAssignment = newCofunderAssignment()
                .withApplication(application)
                .build();

        when(cofunderAssignmentRepository.findById(assignmentId)).thenReturn(of(cofunderAssignment));
        when(cofunderAssignmentWorkflowHandler.edit(eq(cofunderAssignment))).thenReturn(true);

        ServiceResult<Void> result = service.edit(assignmentId);

        assertThat(result.isSuccess(), equalTo(true));
        verify(cofunderAssignmentWorkflowHandler).edit(cofunderAssignment);
    }

    @Test
    public void findApplicationsNeedingCofunders() {
        long competitionId = 1L;
        String filter = "filter";
        PageRequest pageRequest = PageRequest.of(0, 1);
        ApplicationsForCofundingResource app = new ApplicationsForCofundingResource();
        Page<ApplicationsForCofundingResource> page = new PageImpl<>(newArrayList(app), pageRequest, 1L);

        when(cofunderAssignmentRepository.findApplicationsForCofunding(competitionId, filter, pageRequest)).thenReturn(page);

        ServiceResult<ApplicationsForCofundingPageResource> result = service.findApplicationsNeedingCofunders(competitionId, filter, pageRequest);

        assertThat(result.isSuccess(), equalTo(true));
        assertThat(result.getSuccess().getContent().get(0), equalTo(app));
        assertThat(result.getSuccess().getTotalElements(), equalTo(1L));
        assertThat(result.getSuccess().getTotalPages(), equalTo(1));
        assertThat(result.getSuccess().getNumber(), equalTo(0));
        assertThat(result.getSuccess().getSize(), equalTo(1));
    }

    @Test
    public void findAvailableCofundersForApplication() {
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
        CofunderAssignment cofunderAssignment = newCofunderAssignment()
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
        when(cofunderAssignmentRepository.findUsersAvailableForCofunding(applicationId, filter, pageRequest)).thenReturn(page);
        when(profileRepository.findById(unassignedProfileId)).thenReturn(of(unassignedProfile));
        when(profileRepository.findById(assignedProfileId)).thenReturn(of(assignedProfile));
        when(cofunderAssignmentRepository.findByTargetId(applicationId)).thenReturn(Arrays.asList(cofunderAssignment));

        ServiceResult<CofundersAvailableForApplicationPageResource> result = service.findAvailableCofundersForApplication(applicationId, filter, pageRequest);

        assertThat(result.isSuccess(), equalTo(true));
        assertThat(result.getSuccess().getContent().get(0).getEmail(), equalTo(unsassignedUser.getEmail()));
        assertThat(result.getSuccess().getContent().get(0).getName(), equalTo("Bob Bobberson"));
        assertThat(result.getSuccess().getContent().get(0).getUserId(), equalTo(unsassignedUser.getId()));
        assertThat(result.getSuccess().getContent().get(0).getOrganisation(), equalTo("Simply an organisation"));
        assertThat(result.getSuccess().getTotalElements(), equalTo(1L));
        assertThat(result.getSuccess().getTotalPages(), equalTo(1));
        assertThat(result.getSuccess().getNumber(), equalTo(0));
        assertThat(result.getSuccess().getSize(), equalTo(1));
        assertThat(result.getSuccess().getAssignedCofunders().get(0).getEmail(), equalTo(assignedUser.getEmail()));
        assertThat(result.getSuccess().getAssignedCofunders().get(0).getName(), equalTo("Assigned Guy"));
        assertThat(result.getSuccess().getAssignedCofunders().get(0).getUserId(), equalTo(assignedUser.getId()));
        assertThat(result.getSuccess().getAssignedCofunders().get(0).getOrganisation(), equalTo("Simply an assigned organisation"));
    }

    @Test
    public void findAvailableCofundersUserIdsForApplication() {
        long applicationId = 4L;
        String filter = "w";
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        when(cofunderAssignmentRepository.usersAvailableForCofundingUserIds(applicationId, filter)).thenReturn(ids);

        ServiceResult<List<Long>> result = service.findAvailableCofundersUserIdsForApplication(applicationId, filter);

        assertThat(result.isSuccess(), equalTo(true));
        assertThat(result.getSuccess(), equalTo(ids));
    }
}
