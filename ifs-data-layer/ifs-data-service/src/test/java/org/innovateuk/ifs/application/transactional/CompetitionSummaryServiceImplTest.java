package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.*;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.ASSESSOR;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class CompetitionSummaryServiceImplTest extends BaseUnitTestMocksTest {

    private static final long COMP_ID = 123L;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private CompetitionRepository competitionRepositoryMock;

    @Mock
    private ApplicationService applicationServiceMock;

    @Mock
    private AssessmentParticipantRepository assessmentParticipantRepositoryMock;


    @InjectMocks
    private CompetitionSummaryService competitionSummaryService = new CompetitionSummaryServiceImpl();

    private Competition competition;

    @Before
    public void setUp() {
        competition = newCompetition()
                .withId(COMP_ID)
                .withName("compname")
                .withCompetitionStatus(IN_ASSESSMENT)
                .withEndDate(ZonedDateTime.of(2016, 5, 23, 8, 30, 0, 0, ZoneId.systemDefault()))
                .build();

        when(competitionRepositoryMock.findById(COMP_ID)).thenReturn(Optional.of(competition));
        when(applicationRepositoryMock.countByCompetitionId(COMP_ID)).thenReturn(83);
        when(applicationRepositoryMock.countByCompetitionIdAndApplicationProcessActivityStateInAndCompletionLessThanEqual(
                COMP_ID, CREATED_AND_OPEN_STATUSES, new BigDecimal(50L))
        )
                .thenReturn(1);
        when(applicationRepositoryMock.countByCompetitionIdAndApplicationProcessActivityStateIn(COMP_ID, SUBMITTED_AND_INELIGIBLE_STATES)).thenReturn(5);
        when(applicationRepositoryMock.countByCompetitionIdAndApplicationProcessActivityStateIn(COMP_ID, INELIGIBLE_STATES)).thenReturn(2);
        when(applicationRepositoryMock.countByCompetitionIdAndApplicationProcessActivityState(COMP_ID, ApplicationState.APPROVED)).thenReturn(8);
        when(assessmentParticipantRepositoryMock.countByCompetitionIdAndRole(COMP_ID, ASSESSOR)).thenReturn(10);
    }

    @Test
    public void getCompetitionSummaryByCompetitionId() {
        ServiceResult<CompetitionSummaryResource> result = competitionSummaryService.getCompetitionSummaryByCompetitionId(COMP_ID);

        assertTrue(result.isSuccess());

        CompetitionSummaryResource summaryResource = result.getSuccess();

        assertEquals(COMP_ID, summaryResource.getCompetitionId());
        assertEquals(competition.getName(), summaryResource.getCompetitionName());
        assertEquals(competition.getCompetitionStatus(), summaryResource.getCompetitionStatus());
        assertEquals(83, summaryResource.getTotalNumberOfApplications());
        assertEquals(1, summaryResource.getApplicationsStarted());
        assertEquals(0, summaryResource.getApplicationsInProgress());
        assertEquals(5, summaryResource.getApplicationsSubmitted());
        assertEquals(2, summaryResource.getIneligibleApplications());
        assertEquals(78, summaryResource.getApplicationsNotSubmitted());
        assertEquals(ZonedDateTime.of(2016, 5, 23, 8, 30, 0, 0, ZoneId.systemDefault()), summaryResource.getApplicationDeadline());
        assertEquals(10, summaryResource.getAssessorsInvited());
        assertEquals(8, summaryResource.getApplicationsFunded());
    }
}
