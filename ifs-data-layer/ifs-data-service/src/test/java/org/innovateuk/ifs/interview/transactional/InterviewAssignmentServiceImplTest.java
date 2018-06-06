package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.resource.PageResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentBuilder.newInterviewAssignment;
import static org.innovateuk.ifs.interview.resource.InterviewAssignmentState.AWAITING_FEEDBACK_RESPONSE;
import static org.innovateuk.ifs.interview.resource.InterviewAssignmentState.SUBMITTED_FEEDBACK_RESPONSE;
import static org.innovateuk.ifs.invite.builder.StagedApplicationResourceBuilder.newStagedApplicationResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.util.CollectionFunctions.forEachWithIndex;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class InterviewAssignmentServiceImplTest extends BaseServiceUnitTest<InterviewAssignmentServiceImpl> {

    private static final long COMPETITION_ID = 1L;
    private static final Pageable PAGE_REQUEST = new PageRequest(0, 20);
    private static final int TOTAL_APPLICATIONS = 2;
    private static final Organisation LEAD_ORGANISATION = newOrganisation().withName("lead org").build();
    private static final List<Application> EXPECTED_AVAILABLE_APPLICATIONS =
            newApplication()
                    .withProcessRoles(
                            newProcessRole()
                                    .withUser(newUser().build())
                                    .withRole(Role.LEADAPPLICANT)
                                    .withOrganisationId(LEAD_ORGANISATION.getId())
                                    .build()
                    )
                    .build(TOTAL_APPLICATIONS);

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private InterviewAssignmentRepository interviewAssignmentRepositoryMock;

    @Override
    protected InterviewAssignmentServiceImpl supplyServiceUnderTest() {
        return new InterviewAssignmentServiceImpl();
    }

    @Test
    public void getAvailableApplications() {
        Page<Application> expectedPage = new PageImpl<>(EXPECTED_AVAILABLE_APPLICATIONS, PAGE_REQUEST, TOTAL_APPLICATIONS);

        when(applicationRepositoryMock.findSubmittedApplicationsNotOnInterviewPanel(COMPETITION_ID, PAGE_REQUEST)).thenReturn(expectedPage);
        when(organisationRepositoryMock.findOne(LEAD_ORGANISATION.getId())).thenReturn(LEAD_ORGANISATION);

        AvailableApplicationPageResource availableApplicationPageResource = service.getAvailableApplications(COMPETITION_ID, PAGE_REQUEST).getSuccess();

        assertPageRequestMatchesPageResource(PAGE_REQUEST, availableApplicationPageResource);

        assertEquals(TOTAL_APPLICATIONS, availableApplicationPageResource.getTotalElements());

        assertAvailableApplicationResourcesMatch(LEAD_ORGANISATION, EXPECTED_AVAILABLE_APPLICATIONS, availableApplicationPageResource);

        InOrder inOrder = inOrder(applicationRepositoryMock, organisationRepositoryMock);
        inOrder.verify(applicationRepositoryMock).findSubmittedApplicationsNotOnInterviewPanel(COMPETITION_ID, PAGE_REQUEST);
        inOrder.verify(organisationRepositoryMock, times(TOTAL_APPLICATIONS)).findOne(LEAD_ORGANISATION.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getStagedApplications() {
        List<InterviewAssignment> expectedInterviewPanels = newInterviewAssignment()
                .withParticipant(
                        newProcessRole()
                                .withRole(Role.INTERVIEW_LEAD_APPLICANT)
                                .withOrganisationId(LEAD_ORGANISATION.getId())
                                .build()
                )
                .withTarget(
                        newApplication()
                            .build()

                )
                .build(TOTAL_APPLICATIONS);

        Page<InterviewAssignment> expectedPage = new PageImpl<>(expectedInterviewPanels, PAGE_REQUEST, TOTAL_APPLICATIONS);

        when(interviewAssignmentRepositoryMock.findByTargetCompetitionIdAndActivityState(
                COMPETITION_ID, InterviewAssignmentState.CREATED, PAGE_REQUEST)).thenReturn(expectedPage);

        when(organisationRepositoryMock.findOne(LEAD_ORGANISATION.getId())).thenReturn(LEAD_ORGANISATION);

        InterviewAssignmentStagedApplicationPageResource stagedApplicationPageResource = service.getStagedApplications(COMPETITION_ID, PAGE_REQUEST).getSuccess();

        assertPageRequestMatchesPageResource(PAGE_REQUEST, stagedApplicationPageResource);
        assertEquals(TOTAL_APPLICATIONS, stagedApplicationPageResource.getTotalElements());

        assertEquals(expectedInterviewPanels.size(), stagedApplicationPageResource.getContent().size());

        assertStagedApplicationResourcesMatch(LEAD_ORGANISATION, expectedInterviewPanels, stagedApplicationPageResource);

        InOrder inOrder = inOrder(interviewAssignmentRepositoryMock, organisationRepositoryMock);
        inOrder.verify(interviewAssignmentRepositoryMock)
                .findByTargetCompetitionIdAndActivityState(COMPETITION_ID, InterviewAssignmentState.CREATED, PAGE_REQUEST);
        inOrder.verify(organisationRepositoryMock, times(TOTAL_APPLICATIONS)).findOne(LEAD_ORGANISATION.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getAvailableApplicationIds() {
        when(applicationRepositoryMock.findSubmittedApplicationsNotOnInterviewPanel(COMPETITION_ID))
                .thenReturn(EXPECTED_AVAILABLE_APPLICATIONS);

        List<Long> availableApplicationIds = service.getAvailableApplicationIds(COMPETITION_ID).getSuccess();

        assertEquals(simpleMap(EXPECTED_AVAILABLE_APPLICATIONS, Application::getId), availableApplicationIds);

        verify(applicationRepositoryMock, only()).findSubmittedApplicationsNotOnInterviewPanel(COMPETITION_ID);
    }

    @Test
    public void assignApplications() {
        List<StagedApplicationResource> stagedApplications = newStagedApplicationResource()
                .withApplicationId(simpleMap(EXPECTED_AVAILABLE_APPLICATIONS, Application::getId).toArray(new Long[TOTAL_APPLICATIONS]))
                .withCompetitionId(COMPETITION_ID)
                .build(TOTAL_APPLICATIONS);

        forEachWithIndex(EXPECTED_AVAILABLE_APPLICATIONS, (i, expectedApplication) -> {
            when(applicationRepositoryMock.findOne(expectedApplication.getId()))
                    .thenReturn(expectedApplication);

            when(interviewAssignmentRepositoryMock.save(interviewPanelLambdaMatcher(expectedApplication)))
                    .thenReturn(newInterviewAssignment().build()
            );
        });

        service.assignApplications(stagedApplications).getSuccess();

        InOrder inOrder = inOrder(applicationRepositoryMock,
                interviewAssignmentRepositoryMock);

        forEachWithIndex(EXPECTED_AVAILABLE_APPLICATIONS, (i, expectedApplication) -> {
            inOrder.verify(applicationRepositoryMock).findOne(expectedApplication.getId());
            inOrder.verify(interviewAssignmentRepositoryMock).save(interviewPanelLambdaMatcher(expectedApplication));
        });
    }

    @Test
    public void unstageApplication() {
        long applicationId = 1L;

        ServiceResult<Void> result = service.unstageApplication(applicationId);

        assertTrue(result.isSuccess());
        verify(interviewAssignmentRepositoryMock).deleteByTargetIdAndActivityState(applicationId, InterviewAssignmentState.CREATED);
    }

    @Test
    public void unstageApplications() {
        long competitionId = 1L;
        ServiceResult<Void> result = service.unstageApplications(1L);

        assertTrue(result.isSuccess());
        verify(interviewAssignmentRepositoryMock).deleteByTargetCompetitionIdAndActivityState(competitionId, InterviewAssignmentState.CREATED);
    }

    @Test
    public void isApplicationAssigned() {
        long applicationId = 1L;
        when(interviewAssignmentRepositoryMock.existsByTargetIdAndActivityStateIn(applicationId,
                asList(AWAITING_FEEDBACK_RESPONSE, SUBMITTED_FEEDBACK_RESPONSE)))
                .thenReturn(true);

        ServiceResult<Boolean> result = service.isApplicationAssigned(applicationId);

        assertTrue(result.getSuccess());
    }

    private static InterviewAssignment interviewPanelLambdaMatcher(Application application) {
        return createLambdaMatcher((InterviewAssignment interviewPanel) -> {
            ProcessRole participant = interviewPanel.getParticipant();
            assertEquals(application.getId(), interviewPanel.getTarget().getId());
            assertEquals(application.getId(), participant.getApplicationId());
            assertSame(participant.getRole(), Role.INTERVIEW_LEAD_APPLICANT);
            assertEquals(participant.getUser(), application.getLeadApplicant());
            assertEquals(participant.getOrganisationId(), application.getLeadOrganisationId());
        });
    }

    private static void assertAvailableApplicationResourcesMatch(Organisation leadOrganisation, List<Application> expectedApplications, AvailableApplicationPageResource availableApplicationPageResource) {
        final List<AvailableApplicationResource> availableApplicationResources = availableApplicationPageResource.getContent();

        assertEquals(expectedApplications.size(), availableApplicationResources.size());

        for (int i=0; i<expectedApplications.size(); i++) {
            final AvailableApplicationResource availableApplicationResource = availableApplicationResources.get(i);
            final Application expectedApplication = expectedApplications.get(i);
            assertAvailableApplicationResourceMatches(expectedApplication, availableApplicationResource, leadOrganisation);
        }
    }

    private static void assertAvailableApplicationResourceMatches(Application application, AvailableApplicationResource availableApplicationResource, Organisation leadOrganisation) {
        assertEquals(application.getId(), (Long) availableApplicationResource.getId());
        assertEquals(application.getName(), availableApplicationResource.getName());
        assertEquals(leadOrganisation.getName(), availableApplicationResource.getLeadOrganisation());
    }

    private static void assertStagedApplicationResourcesMatch(Organisation leadOrganisation, List<InterviewAssignment> expectedInterviewPanels, InterviewAssignmentStagedApplicationPageResource stagedApplicationPageResource) {
        final List<InterviewAssignmentStagedApplicationResource> stagedApplicationResources =  stagedApplicationPageResource.getContent();

        assertEquals(expectedInterviewPanels.size(), stagedApplicationResources.size());

        for (int i=0; i<expectedInterviewPanels.size(); i++) {
            final InterviewAssignmentStagedApplicationResource stagedApplicationResource = stagedApplicationResources.get(i);
            final InterviewAssignment expectedInterviewPanel = expectedInterviewPanels.get(i);
            assertStagedApplicationResourceMatches(expectedInterviewPanel, stagedApplicationResource, leadOrganisation);
        }
    }

    private static void assertStagedApplicationResourceMatches(InterviewAssignment interviewPanel, InterviewAssignmentStagedApplicationResource stagedApplicationResource, Organisation leadOrganisation) {
        assertEquals(interviewPanel.getId(), (Long) stagedApplicationResource.getId());
        final Application application = interviewPanel.getTarget();
        assertEquals(application.getId(), (Long) stagedApplicationResource.getApplicationId());
        assertEquals(application.getName(), stagedApplicationResource.getApplicationName());
        assertEquals(leadOrganisation.getName(), stagedApplicationResource.getLeadOrganisationName());
    }

    private static void assertPageRequestMatchesPageResource(Pageable pageRequest, PageResource<?> pageResource) {
        assertEquals(pageRequest.getPageNumber(), pageResource.getNumber());
        assertEquals(pageRequest.getPageSize(), pageResource.getSize());
    }
}