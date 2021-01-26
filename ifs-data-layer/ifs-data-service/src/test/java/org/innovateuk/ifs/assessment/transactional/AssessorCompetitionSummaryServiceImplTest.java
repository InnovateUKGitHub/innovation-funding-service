package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentApplicationAssessorCount;
import org.innovateuk.ifs.assessment.domain.AssessmentRejectOutcome;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessorCompetitionSummaryResource;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.AssessmentApplicationAssessorCountBuilder.newAssessmentApplicationAssessorCount;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessmentRejectOutcomeBuilder.newAssessmentRejectOutcome;
import static org.innovateuk.ifs.assessment.builder.AssessorAssessmentResourceBuilder.newAssessorAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessorCompetitionSummaryResourceBuilder.newAssessorCompetitionSummaryResource;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.CONFLICT_OF_INTEREST;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.*;
import static org.innovateuk.ifs.assessment.transactional.AssessorCompetitionSummaryServiceImpl.ALL_ASSESSMENT_STATES;
import static org.innovateuk.ifs.assessment.transactional.AssessorCompetitionSummaryServiceImpl.VALID_ASSESSMENT_STATES;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AssessorCompetitionSummaryServiceImplTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private AssessorCompetitionSummaryServiceImpl service;

    @Mock
    private AssessmentRepository assessmentRepositoryMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private CompetitionService competitionServiceMock;

    @Mock
    private AssessorService assessorServiceMock;

    @Test
    public void getAssessorSummary() {
        long assessorId = 1L;
        long competitionId = 1L;

        AssessorProfileResource assessor = setUpAssessor(assessorId);
        setUpCompetition(competitionId);
        Application[] applications = setUpApplications();

        AssessmentRejectOutcome rejectOutcome = newAssessmentRejectOutcome()
                .withRejectReason(CONFLICT_OF_INTEREST)
                .withRejectComment("rejection comment")
                .build();

        Assessment[] assessments = setUpAssessments(applications, rejectOutcome);

        List<AssessmentApplicationAssessorCount> assessmentCounts = newAssessmentApplicationAssessorCount()
                .withApplication(applications)
                .withAssessment(assessments)
                .withAssessorCount(5, 4, 3, 2)
                .build(4);

        when(assessmentRepositoryMock.countByParticipantUserIdAndActivityStateIn(
                assessorId, VALID_ASSESSMENT_STATES))
                .thenReturn(20L);

        when(assessmentRepositoryMock.getAssessorApplicationAssessmentCountsForStates(
                competitionId,
                assessorId,
                VALID_ASSESSMENT_STATES,
                ALL_ASSESSMENT_STATES))
                .thenReturn(assessmentCounts);

        List<Organisation> leadOrganisations = newOrganisation()
                .withId(1L, 2L, 3L, 4L)
                .withName("Lead Org 1", "Lead Org 2", "Lead Org 3", "Lead Org 4")
                .build(4);

        when(organisationRepositoryMock.findById(applications[0].getLeadOrganisationId())).thenReturn(Optional.of(leadOrganisations.get(0)));
        when(organisationRepositoryMock.findById(applications[1].getLeadOrganisationId())).thenReturn(Optional.of(leadOrganisations.get(1)));
        when(organisationRepositoryMock.findById(applications[2].getLeadOrganisationId())).thenReturn(Optional.of(leadOrganisations.get(2)));
        when(organisationRepositoryMock.findById(applications[3].getLeadOrganisationId())).thenReturn(Optional.of(leadOrganisations.get(3)));

        ServiceResult<AssessorCompetitionSummaryResource> result = service.getAssessorSummary(assessorId, competitionId);
        assertTrue(result.isSuccess());

        verify(competitionServiceMock).getCompetitionById(competitionId);
        verify(assessorServiceMock).getAssessorProfile(assessorId);
        verify(assessmentRepositoryMock).countByParticipantUserIdAndActivityStateIn(assessorId, VALID_ASSESSMENT_STATES);
        verify(assessmentRepositoryMock).getAssessorApplicationAssessmentCountsForStates(competitionId, assessorId, VALID_ASSESSMENT_STATES, ALL_ASSESSMENT_STATES);
        verify(organisationRepositoryMock).findById(applications[0].getLeadOrganisationId());
        verify(organisationRepositoryMock).findById(applications[1].getLeadOrganisationId());
        verify(organisationRepositoryMock).findById(applications[2].getLeadOrganisationId());
        verify(organisationRepositoryMock).findById(applications[3].getLeadOrganisationId());

        AssessorCompetitionSummaryResource expected = newAssessorCompetitionSummaryResource()
                .withCompetitionId(competitionId)
                .withCompetitionName("Test Competition")
                .withCompetitionStatus(IN_ASSESSMENT)
                .withTotalApplications(20L)
                .withAssessor(assessor)
                .withAssignedAssessments(
                        newAssessorAssessmentResource()
                                .withApplicationId(applications[0].getId(), applications[1].getId(), applications[2].getId(), applications[3].getId())
                                .withApplicationName(applications[0].getName(), applications[1].getName(), applications[2].getName(), applications[3].getName())
                                .withLeadOrganisation("Lead Org 1", "Lead Org 2", "Lead Org 3", "Lead Org 4")
                                .withState(ACCEPTED, SUBMITTED, REJECTED, WITHDRAWN)
                                .withTotalAssessors(5, 4, 3, 2)
                                .withRejectionReason(null, null, rejectOutcome.getRejectReason(), null)
                                .withRejectionComment(null, null, rejectOutcome.getRejectComment(), null)
                                .withAssessmentId(assessments[0].getId(), assessments[1].getId(), assessments[2].getId(), assessments[3].getId())
                                .build(4)
                )
                .build();

        AssessorCompetitionSummaryResource actual = result.getSuccess();

        assertEquals(expected, actual);
    }

    private Application[]  setUpApplications() {
        Application[] applications = newApplication()
                .withName("Test Application 1", "Test Application 2", "Test Application 3", "Test Application 4")
                .buildArray(4, Application.class);

        applications[0].setProcessRoles(
                newProcessRole()
                        .withRole(ProcessRoleType.LEADAPPLICANT)
                        .withOrganisationId(1L)
                        .build(1));
        applications[1].setProcessRoles(
                newProcessRole()
                        .withRole(ProcessRoleType.LEADAPPLICANT)
                        .withOrganisationId(2L)
                        .build(1));
        applications[2].setProcessRoles(
                newProcessRole()
                        .withRole(ProcessRoleType.LEADAPPLICANT)
                        .withOrganisationId(3L)
                        .build(1));
        applications[3].setProcessRoles(
                newProcessRole()
                        .withRole(ProcessRoleType.LEADAPPLICANT)
                        .withOrganisationId(4L)
                        .build(1));

        return applications;
    }

    private AssessorProfileResource setUpAssessor(long assessorId) {
        AssessorProfileResource assessor = newAssessorProfileResource()
                .withUser(
                        newUserResource()
                                .withId(assessorId)
                                .build())
                .withProfile(newProfileResource().build())
                .build();

        when(assessorServiceMock.getAssessorProfile(assessorId)).thenReturn(serviceSuccess(assessor));

        return assessor;
    }

    private void setUpCompetition(long competitionId) {
        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withName("Test Competition")
                .withCompetitionStatus(IN_ASSESSMENT)
                .build();

        when(competitionServiceMock.getCompetitionById(competitionId)).thenReturn(serviceSuccess(competition));
    }

    private Assessment[] setUpAssessments(Application[] applications, AssessmentRejectOutcome rejectOutcome) {
        return newAssessment()
                .withApplication(applications)
                .withProcessState(ACCEPTED, SUBMITTED, REJECTED, WITHDRAWN)
                .withRejection(null, null, rejectOutcome, null)
                .buildArray(4, Assessment.class);
    }
}