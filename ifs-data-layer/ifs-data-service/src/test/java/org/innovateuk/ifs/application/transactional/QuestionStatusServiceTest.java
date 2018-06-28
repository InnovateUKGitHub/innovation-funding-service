package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.QuestionStatus;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.QuestionStatusRepository;
import org.innovateuk.ifs.application.resource.QuestionApplicationCompositeId;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.transactional.UserService;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ASSIGNEE_SHOULD_BE_APPLICANT;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class QuestionStatusServiceTest extends BaseUnitTestMocksTest {

    @InjectMocks
    protected QuestionStatusService questionStatusService = new QuestionStatusServiceImpl();

    @Mock
    private UserService userServiceMock;

    @Mock
    private QuestionRepository questionRepositoryMock;

    @Mock
    private QuestionStatusRepository questionStatusRepositoryMock;

    @Mock
    private ProcessRoleRepository processRoleRepositoryMock;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private ApplicationProgressService applicationProgressServiceMock;

    @Test
    public void assignTest() throws Exception {
        final long applicationId = 1232L;
        final long questionId = 2228L;
        final long assigneeId = 51234L;
        final long assignedById = 72834L;
        QuestionApplicationCompositeId questionApplicationCompositeId = new QuestionApplicationCompositeId(questionId, applicationId);

        when(questionRepositoryMock.findById(questionId)).thenReturn(Optional.of(newQuestion().build()));
        when(processRoleRepositoryMock.findById(assigneeId)).thenReturn(Optional.of(newProcessRole().withUser(newUser().withId(assigneeId).build()).withApplication(newApplication().withId(applicationId).build()).build()));
        when(processRoleRepositoryMock.findById(assignedById)).thenReturn(Optional.of(newProcessRole().build()));
        Competition competitionMock = mock(Competition.class);
        when(competitionMock.getCompetitionStatus()).thenReturn(CompetitionStatus.OPEN);
        Application application = newApplication().withCompetition(competitionMock).build();
        when(applicationRepositoryMock.findById(applicationId)).thenReturn(Optional.of(application));
        when(userServiceMock.findAssignableUsers(applicationId)).thenReturn(serviceSuccess(new HashSet(newUserResource().withId(assigneeId).build(1))));

        ServiceResult<Void> result = questionStatusService.assign(questionApplicationCompositeId, assigneeId, assignedById);

        assertTrue(result.isSuccess());

        Long differentApplicationId = 1233L;
        when(processRoleRepositoryMock.findById(assigneeId))
                .thenReturn(Optional.of(newProcessRole().withUser(newUser().withId(2L).build()).withApplication(newApplication().withId(differentApplicationId).build()).build()));

        when(userServiceMock.findAssignableUsers(applicationId)).thenReturn(serviceSuccess(new HashSet(newUserResource().withId(1L).build(1))));

        ServiceResult<Void> resultTwo = questionStatusService.assign(questionApplicationCompositeId, assigneeId, assignedById);

        assertTrue(resultTwo.isFailure());
        assertEquals(ASSIGNEE_SHOULD_BE_APPLICANT.getErrorKey(), resultTwo.getFailure().getErrors().get(0).getErrorKey());
    }

    @Test
    public void markTeamAsInComplete() throws Exception {
        ProcessRole markedAsInCompleteBy = newProcessRole().build();
        Application application = newApplication().build();
        Question question = newQuestion().build();

        QuestionApplicationCompositeId questionApplicationCompositeId = new QuestionApplicationCompositeId
                (question.getId(), application.getId());

        when(processRoleRepositoryMock.findById(markedAsInCompleteBy.getId())).thenReturn(Optional.of(markedAsInCompleteBy));
        when(applicationRepositoryMock.findById(application.getId())).thenReturn(Optional.of(application));
        when(questionRepositoryMock.findById(question.getId())).thenReturn(Optional.of(question));
        when(questionStatusRepositoryMock.findByQuestionIdAndApplicationId(question.getId(), application.getId()))
                .thenReturn(null);
        when(applicationProgressServiceMock.updateApplicationProgress(application.getId())).thenReturn(serviceSuccess
                (new BigDecimal("33.33")));

        ServiceResult<List<ValidationMessages>> result = questionStatusService.markTeamAsInComplete
                (questionApplicationCompositeId, markedAsInCompleteBy.getId());

        assertTrue(result.isSuccess());
        assertTrue(result.getSuccess().isEmpty());

        InOrder inOrder = inOrder(processRoleRepositoryMock, applicationRepositoryMock, questionRepositoryMock,
                questionStatusRepositoryMock, applicationProgressServiceMock);
        inOrder.verify(processRoleRepositoryMock).findById(markedAsInCompleteBy.getId());
        inOrder.verify(applicationRepositoryMock).findById(application.getId());
        inOrder.verify(questionRepositoryMock).findById(question.getId());
        inOrder.verify(questionStatusRepositoryMock).findByQuestionIdAndApplicationId(question.getId(), application.getId());
        inOrder.verify(questionStatusRepositoryMock).save(createQuestionStatusLambdaMatcher(question,
                application, markedAsInCompleteBy, false));
        inOrder.verify(applicationProgressServiceMock).updateApplicationProgress(application.getId());
        inOrder.verifyNoMoreInteractions();
    }

    private QuestionStatus createQuestionStatusLambdaMatcher(Question question,
                                                             Application application,
                                                             ProcessRole markedAsCompleteBy,
                                                             boolean markedAsComplete) {
        return createLambdaMatcher(questionStatus -> {
            assertEquals(question, questionStatus.getQuestion());
            assertEquals(application, questionStatus.getApplication());
            assertEquals(markedAsCompleteBy, questionStatus.getMarkedAsCompleteBy());
            assertEquals(markedAsComplete, questionStatus.getMarkedAsComplete());
        });
    }
}
