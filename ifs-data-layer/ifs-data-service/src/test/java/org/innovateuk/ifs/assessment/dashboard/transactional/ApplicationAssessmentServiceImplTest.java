package org.innovateuk.ifs.assessment.dashboard.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static freemarker.template.utility.Collections12.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.ACCEPTED;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.ProcessRoleType.LEADAPPLICANT;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationAssessmentServiceImplTest extends BaseServiceUnitTest<ApplicationAssessmentServiceImpl> {

    @InjectMocks
    private final ApplicationAssessmentService applicationAssessmentService = new ApplicationAssessmentServiceImpl();

    @Mock
    private AssessmentRepository assessmentRepositoryMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Override
    protected ApplicationAssessmentServiceImpl supplyServiceUnderTest() {
        return new ApplicationAssessmentServiceImpl();
    }

    @Test
    public void getApplicationAssessmentResource() {
        User user = newUser().withId(1L).build();
        Competition competition = newCompetition().withId(8L).withName("Test Competition").build();
        Organisation organisation = newOrganisation().withId(9L).withName("Lead Organisation").build();
        ProcessRole processRole = newProcessRole().withRole(LEADAPPLICANT).withOrganisationId(organisation.getId()).build();
        Application application = newApplication()
                .withId(1L)
                .withApplicationState(ApplicationState.SUBMITTED)
                .withCompetition(competition)
                .withProcessRole(processRole)
                .build();
        Assessment assessment = newAssessment().withId(5L).withApplication(application).withProcessState(ACCEPTED).build();

        when(assessmentRepositoryMock.findByParticipantUserIdAndTargetCompetitionId(user.getId(), competition.getId()))
                .thenReturn(singletonList(assessment));
        when(organisationRepositoryMock.findById(organisation.getId())).thenReturn(Optional.of(organisation));

        ServiceResult<List<ApplicationAssessmentResource>> result = service.getApplicationAssessmentResource(user.getId(), competition.getId());

        assertTrue(result.isSuccess());
        verify(assessmentRepositoryMock, times(1)).findByParticipantUserIdAndTargetCompetitionId(user.getId(), competition.getId());
        verify(organisationRepositoryMock, times(1)).findById(organisation.getId());
        verifyNoMoreInteractions(assessmentRepositoryMock);
    }
}