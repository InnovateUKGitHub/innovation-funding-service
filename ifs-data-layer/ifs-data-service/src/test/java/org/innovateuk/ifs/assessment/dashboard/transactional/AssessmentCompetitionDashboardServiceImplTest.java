package org.innovateuk.ifs.assessment.dashboard.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.assessment.resource.dashboard.AssessorCompetitionDashboardResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.assessment.builder.ApplicationAssessmentResourceBuilder.newApplicationAssessmentResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.ACCEPTED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AssessmentCompetitionDashboardServiceImplTest extends BaseServiceUnitTest<AssessmentCompetitionDashboardServiceImpl> {

    @InjectMocks
    private final AssessmentCompetitionDashboardService assessmentCompetitionDashboardService = new AssessmentCompetitionDashboardServiceImpl();

    @Mock
    private CompetitionRepository competitionRepositoryMock;

    @Mock
    private ApplicationAssessmentService applicationAssessmentServiceMock;

    @Override
    protected AssessmentCompetitionDashboardServiceImpl supplyServiceUnderTest() {
        return new AssessmentCompetitionDashboardServiceImpl();
    }

    @Test
    public void getAssessorCompetitionDashboardResource() {
        User user = newUser().withId(52L).build();
        User leadTechnologist = newUser().withId(42L).withFirstName("Paul").withLastName("Plum").build();
        Competition competition = newCompetition()
                .withId(8L)
                .withName("Test Competition")
                .withLeadTechnologist(leadTechnologist)
                .withAssessorAcceptsDate(LocalDate.now().atStartOfDay().minusDays(2).atZone(ZoneId.systemDefault()))
                .withAssessorDeadlineDate(LocalDate.now().atStartOfDay().plusDays(4).atZone(ZoneId.systemDefault()))
                .build();
        ApplicationAssessmentResource applicationAssessmentResource = newApplicationAssessmentResource()
                .withApplicationId(1L)
                .withCompetitionName("Test Competition")
                .withAssessmentId(2L)
                .withLeadOrganisation("Lead Company")
                .withState(ACCEPTED)
                .build();

        when(applicationAssessmentServiceMock.getApplicationAssessmentResource(user.getId(), competition.getId())).thenReturn(serviceSuccess(singletonList(applicationAssessmentResource)));
        when(competitionRepositoryMock.findById(8L)).thenReturn(Optional.of(competition));

        ServiceResult<AssessorCompetitionDashboardResource> result = service.getAssessorCompetitionDashboardResource(user.getId(), competition.getId());

        assertTrue(result.isSuccess());
        verify(applicationAssessmentServiceMock, times(1)).getApplicationAssessmentResource(user.getId(), competition.getId());
        verify(competitionRepositoryMock, times(1)).findById(competition.getId());
        verifyNoMoreInteractions(applicationAssessmentServiceMock);
    }
}