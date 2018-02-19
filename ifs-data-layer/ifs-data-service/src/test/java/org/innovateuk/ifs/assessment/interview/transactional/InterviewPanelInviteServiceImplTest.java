package org.innovateuk.ifs.assessment.interview.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterviewPanel;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelState;
import org.innovateuk.ifs.invite.resource.AvailableApplicationPageResource;
import org.innovateuk.ifs.invite.resource.AvailableApplicationResource;
import org.innovateuk.ifs.invite.resource.InterviewPanelStagedApplicationPageResource;
import org.innovateuk.ifs.invite.resource.InterviewPanelStagedApplicationResource;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.interview.builder.AssessmentInterviewPanelBuilder.newAssessmentInterviewPanel;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class InterviewPanelInviteServiceImplTest extends BaseServiceUnitTest<InterviewPanelInviteServiceImpl> {

    @Override
    protected InterviewPanelInviteServiceImpl supplyServiceUnderTest() {
        return new InterviewPanelInviteServiceImpl();
    }

    @Test
    public void getAvailableApplications() {
        final long competitionId = 1L;
        final int pageNumber = 0;
        final int pageSize = 20;
        final int totalApplications = 2;
        final String leadOrganisationName = "lead org";

        final Pageable pageRequest = new PageRequest(pageNumber, pageSize);

        final Organisation leadOrganisation = newOrganisation().withName(leadOrganisationName).build();

        List<Application> expectedApplications =
                newApplication()
                        .withProcessRoles(
                                newProcessRole()
                                        .withRole(newRole().withType(UserRoleType.LEADAPPLICANT).build())
                                        .withOrganisationId(leadOrganisation.getId())
                                        .build()
                        )
                        .build(totalApplications);

        Page<Application> expectedPage = new PageImpl<>(expectedApplications, pageRequest, totalApplications);

        when(applicationRepositoryMock.findSubmittedApplicationsNotOnInterviewPanel(competitionId, pageRequest)).thenReturn(expectedPage);
        when(organisationRepositoryMock.findOne(leadOrganisation.getId())).thenReturn(leadOrganisation);

        AvailableApplicationPageResource availableApplicationPageResource = service.getAvailableApplications(competitionId, pageRequest).getSuccess();

        assertEquals(pageNumber, availableApplicationPageResource.getNumber());
        assertEquals(pageSize, availableApplicationPageResource.getSize());
        assertEquals(totalApplications, availableApplicationPageResource.getTotalElements());

        assertAvailableApplicationResourcesMatch(leadOrganisation, expectedApplications, availableApplicationPageResource);

        InOrder inOrder = inOrder(applicationRepositoryMock, organisationRepositoryMock);
        inOrder.verify(applicationRepositoryMock).findSubmittedApplicationsNotOnInterviewPanel(competitionId, pageRequest);
        inOrder.verify(organisationRepositoryMock, times(totalApplications)).findOne(leadOrganisation.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getStagedApplications() {
        final long competitionId = 1L;
        final int pageNumber = 0;
        final int pageSize = 20;
        final int totalApplications = 2;
        final String leadOrganisationName = "lead org";

        final Pageable pageRequest = new PageRequest(pageNumber, pageSize);

        final Organisation leadOrganisation = newOrganisation().withName(leadOrganisationName).build();

        List<AssessmentInterviewPanel> expectedInterviewPanels = newAssessmentInterviewPanel()
                .withParticipant(
                        newProcessRole()
                                .withRole(UserRoleType.INTERVIEW_LEAD_APPLICANT)
                                .withOrganisationId(leadOrganisation.getId())
                                .build()
                )
                .withTarget(
                        newApplication()
                            .build()

                )
                .build(totalApplications);

        Page<AssessmentInterviewPanel> expectedPage = new PageImpl<>(expectedInterviewPanels, pageRequest, totalApplications);

        when(assessmentInterviewPanelRepositoryMock.findByTargetCompetitionIdAndActivityStateState(
                competitionId, AssessmentInterviewPanelState.CREATED.getBackingState(), pageRequest)).thenReturn(expectedPage);

        when(organisationRepositoryMock.findOne(leadOrganisation.getId())).thenReturn(leadOrganisation);

        InterviewPanelStagedApplicationPageResource stagedApplicationPageResource = service.getStagedApplications(competitionId, pageRequest).getSuccess();

        assertEquals(pageNumber, stagedApplicationPageResource.getNumber());
        assertEquals(pageSize, stagedApplicationPageResource.getSize());
        assertEquals(totalApplications, stagedApplicationPageResource.getTotalElements());

        assertEquals(expectedInterviewPanels.size(), stagedApplicationPageResource.getContent().size());

        assertStagedApplicationResourcesMatch(leadOrganisation, expectedInterviewPanels, stagedApplicationPageResource);

        InOrder inOrder = inOrder(assessmentInterviewPanelRepositoryMock, organisationRepositoryMock);
        inOrder.verify(assessmentInterviewPanelRepositoryMock)
                .findByTargetCompetitionIdAndActivityStateState(competitionId, AssessmentInterviewPanelState.CREATED.getBackingState(), pageRequest);
        inOrder.verify(organisationRepositoryMock, times(totalApplications)).findOne(leadOrganisation.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getAvailableApplicationIds() {
    }

    @Test
    public void assignApplications() {
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

    private static void assertStagedApplicationResourcesMatch(Organisation leadOrganisation, List<AssessmentInterviewPanel> expectedInterviewPanels, InterviewPanelStagedApplicationPageResource stagedApplicationPageResource) {
        final List<InterviewPanelStagedApplicationResource> stagedApplicationResources =  stagedApplicationPageResource.getContent();

        assertEquals(expectedInterviewPanels.size(), stagedApplicationResources.size());

        for (int i=0; i<expectedInterviewPanels.size(); i++) {
            final InterviewPanelStagedApplicationResource stagedApplicationResource = stagedApplicationResources.get(i);
            final AssessmentInterviewPanel expectedInterviewPanel = expectedInterviewPanels.get(i);
            assertStagedApplicationResourceMatches(expectedInterviewPanel, stagedApplicationResource, leadOrganisation);
        }
    }

    private static void assertStagedApplicationResourceMatches(AssessmentInterviewPanel interviewPanel, InterviewPanelStagedApplicationResource stagedApplicationResource, Organisation leadOrganisation) {
        assertEquals(interviewPanel.getId(), (Long) stagedApplicationResource.getId());
        final Application application = interviewPanel.getTarget();
        assertEquals(application.getId(), (Long) stagedApplicationResource.getApplicationId());
        assertEquals(application.getName(), stagedApplicationResource.getApplicationName());
        assertEquals(leadOrganisation.getName(), stagedApplicationResource.getLeadOrganisationName());
    }
}