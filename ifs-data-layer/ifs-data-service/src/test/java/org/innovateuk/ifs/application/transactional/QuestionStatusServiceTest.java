package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.QuestionStatusRepository;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.form.transactional.SectionService;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.transactional.UserService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashSet;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ASSIGNEE_SHOULD_BE_APPLICANT;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QuestionStatusServiceTest extends BaseUnitTestMocksTest {

    @InjectMocks
    protected QuestionStatusService questionService = new QuestionStatusServiceImpl();

    @Mock
    private SectionService sectionService;

    @Mock
    private UserService userService;

    @Mock
    private QuestionRepository questionRepositoryMock;

    @Mock
    private QuestionStatusRepository questionStatusRepository;

    @Mock
    private ProcessRoleRepository processRoleRepositoryMock;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Test
    public void assignTest() throws Exception {
        final long applicationId = 1232L;
        final long questionId = 2228L;
        final long assigneeId = 51234L;
        final long assignedById = 72834L;
        QuestionApplicationCompositeId questionApplicationCompositeId = new QuestionApplicationCompositeId(questionId, applicationId);

        when(questionRepositoryMock.findOne(questionId)).thenReturn(newQuestion().build());
        when(processRoleRepositoryMock.findOne(assigneeId)).thenReturn(newProcessRole().withUser(newUser().withId(assigneeId).build()).withApplication(newApplication().withId(applicationId).build()).build());
        when(processRoleRepositoryMock.findOne(assignedById)).thenReturn(newProcessRole().build());
        Competition competitionMock = mock(Competition.class);
        when(competitionMock.getCompetitionStatus()).thenReturn(CompetitionStatus.OPEN);
        Application application = newApplication().withCompetition(competitionMock).build();
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(userService.findAssignableUsers(applicationId)).thenReturn(serviceSuccess(new HashSet(newUserResource().withId(assigneeId).build(1))));

        ServiceResult<Void> result = questionService.assign(questionApplicationCompositeId, assigneeId, assignedById);

        assertTrue(result.isSuccess());

        Long differentApplicationId = 1233L;
        when(processRoleRepositoryMock.findOne(assigneeId))
                .thenReturn(newProcessRole().withUser(newUser().withId(2L).build()).withApplication(newApplication().withId(differentApplicationId).build()).build());

        when(userService.findAssignableUsers(applicationId)).thenReturn(serviceSuccess(new HashSet(newUserResource().withId(1L).build(1))));

        ServiceResult<Void> resultTwo = questionService.assign(questionApplicationCompositeId, assigneeId, assignedById);

        assertTrue(resultTwo.isFailure());
        assertEquals(ASSIGNEE_SHOULD_BE_APPLICANT.getErrorKey(), resultTwo.getFailure().getErrors().get(0).getErrorKey());
    }

}
