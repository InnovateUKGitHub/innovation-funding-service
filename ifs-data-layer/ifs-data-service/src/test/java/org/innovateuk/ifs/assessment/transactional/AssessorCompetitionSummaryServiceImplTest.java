package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.domain.AssessmentApplicationAssessorCount;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentRejectOutcome;
import org.innovateuk.ifs.assessment.resource.AssessorCompetitionSummaryResource;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.List;

import static java.util.Optional.of;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.AssessmentApplicationAssessorCountBuilder.newAssessmentApplicationAssessorCount;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessmentRejectOutcomeBuilder.newAssessmentRejectOutcome;
import static org.innovateuk.ifs.assessment.builder.AssessorAssessmentResourceBuilder.newAssessorAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessorCompetitionSummaryResourceBuilder.newAssessorCompetitionSummaryResource;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.CONFLICT_OF_INTEREST;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.ACCEPTED;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.REJECTED;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.SUBMITTED;
import static org.innovateuk.ifs.assessment.transactional.AssessorCompetitionSummaryServiceImpl.INCLUDED_ASSESSMENT_STATES;
import static org.innovateuk.ifs.assessment.transactional.AssessorCompetitionSummaryServiceImpl.VALID_ASSESSMENT_STATES;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AssessorCompetitionSummaryServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private AssessorCompetitionSummaryServiceImpl service;

    @Test
    public void getAssessorSummary() throws Exception {
        long assessorId = 1L;
        long competitionId = 1L;

        AssessorProfileResource assessor = newAssessorProfileResource()
                .withUser(
                        newUserResource()
                                .withId(assessorId)
                                .build()
                )
                .withProfile(newProfileResource().build())
                .build();

        when(assessorServiceMock.getAssessorProfile(assessorId)).thenReturn(serviceSuccess(assessor));

        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withName("Test Competition")
                .withCompetitionStatus(IN_ASSESSMENT)
                .build();

        when(competitionServiceMock.getCompetitionById(competitionId)).thenReturn(serviceSuccess(competition));

        Application[] applications = newApplication()
                .withName("Test Application 1", "Test Application 2", "Test Application 3")
                .buildArray(3, Application.class);

        applications[0].setProcessRoles(
                newProcessRole()
                        .withRole(UserRoleType.LEADAPPLICANT)
                        .withOrganisationId(1L)
                        .build(1)
        );
        applications[1].setProcessRoles(
                newProcessRole()
                        .withRole(UserRoleType.LEADAPPLICANT)
                        .withOrganisationId(2L)
                        .build(1)
        );
        applications[2].setProcessRoles(
                newProcessRole()
                        .withRole(UserRoleType.LEADAPPLICANT)
                        .withOrganisationId(3L)
                        .build(1)
        );

        AssessmentRejectOutcome rejectOutcome = newAssessmentRejectOutcome()
                .withRejectReason(CONFLICT_OF_INTEREST)
                .withRejectComment("rejection comment")
                .build();

        Assessment[] assessments = newAssessment()
                .withApplication(applications)
                .withActivityState(
                        new ActivityState(APPLICATION_ASSESSMENT, ACCEPTED.getBackingState()),
                        new ActivityState(APPLICATION_ASSESSMENT, SUBMITTED.getBackingState()),
                        new ActivityState(APPLICATION_ASSESSMENT, REJECTED.getBackingState()))
                .withRejection(null, null, rejectOutcome)
                .buildArray(3, Assessment.class);

        List<AssessmentApplicationAssessorCount> assessmentCounts = newAssessmentApplicationAssessorCount()
                .withApplication(applications)
                .withAssessment(assessments)
                .withAssessorCount(5, 4, 3)
                .build(3);

        when(assessmentRepositoryMock.countByParticipantUserIdAndActivityStateStateIn(
                assessorId,
                INCLUDED_ASSESSMENT_STATES
        ))
                .thenReturn(20L);

        when(assessmentRepositoryMock.getAssessorApplicationAssessmentCountsForStates(
                competitionId,
                assessorId,
                INCLUDED_ASSESSMENT_STATES
        ))
                .thenReturn(assessmentCounts);

        List<Organisation> leadOrganisations = newOrganisation()
                .withId(1L, 2L, 3L)
                .withName("Lead Org 1", "Lead Org 2", "Lead Org 3")
                .build(3);

        when(organisationRepositoryMock.findOne(applications[0].getLeadOrganisationId())).thenReturn(leadOrganisations.get(0));
        when(organisationRepositoryMock.findOne(applications[1].getLeadOrganisationId())).thenReturn(leadOrganisations.get(1));
        when(organisationRepositoryMock.findOne(applications[2].getLeadOrganisationId())).thenReturn(leadOrganisations.get(2));

        ServiceResult<AssessorCompetitionSummaryResource> result = service.getAssessorSummary(assessorId, competitionId);
        assertTrue(result.isSuccess());

        verify(competitionServiceMock).getCompetitionById(competitionId);
        verify(assessorServiceMock).getAssessorProfile(assessorId);
        verify(assessmentRepositoryMock).countByParticipantUserIdAndActivityStateStateIn(assessorId, INCLUDED_ASSESSMENT_STATES);
        verify(assessmentRepositoryMock).getAssessorApplicationAssessmentCountsForStates(competitionId, assessorId, INCLUDED_ASSESSMENT_STATES);
        verify(organisationRepositoryMock).findOne(applications[0].getLeadOrganisationId());
        verify(organisationRepositoryMock).findOne(applications[1].getLeadOrganisationId());
        verify(organisationRepositoryMock).findOne(applications[2].getLeadOrganisationId());

        AssessorCompetitionSummaryResource expected = newAssessorCompetitionSummaryResource()
                .withCompetitionId(competitionId)
                .withCompetitionName("Test Competition")
                .withCompetitionStatus(IN_ASSESSMENT)
                .withTotalApplications(20L)
                .withAssessor(assessor)
                .withAssignedAssessments(
                        newAssessorAssessmentResource()
                                .withApplicationId(applications[0].getId(), applications[1].getId(), applications[2].getId())
                                .withApplicationName(applications[0].getName(), applications[1].getName(), applications[2].getName())
                                .withLeadOrganisation("Lead Org 1", "Lead Org 2", "Lead Org 3")
                                .withState(ACCEPTED, SUBMITTED, REJECTED)
                                .withTotalAssessors(5, 4, 2)
                                .withRejectionReason(null, null, rejectOutcome.getRejectReason())
                                .withRejectionComment(null, null, rejectOutcome.getRejectComment())
                                .build(3)
                )
                .build();

        AssessorCompetitionSummaryResource actual = result.getSuccessObjectOrThrowException();

        assertEquals(expected, actual);
    }
}
