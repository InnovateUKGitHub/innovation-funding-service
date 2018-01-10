package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.setup.repository.SetupStatusRepository;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import javax.persistence.EntityManager;
import java.util.List;

import static org.innovateuk.ifs.application.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.application.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

public class QuestionTemplatePersistorImplTest extends BaseServiceUnitTest<QuestionTemplatePersistorImpl> {
    public QuestionTemplatePersistorImpl supplyServiceUnderTest() {
        return new QuestionTemplatePersistorImpl();
    }

    @Mock
    private EntityManager entityManagerMock;

    @Mock
    private FormInputTemplatePersistorImpl formInputTemplatePersistorMock;

    @Mock
    private SetupStatusRepository setupStatusRepository;

    @Test
    public void persistByEntity_questionIsBeingSavedAndFormInputPersistCalledInCorrectOrder() throws Exception {
        List<Question> questions = newQuestion().withId(1L, 2L).withName("Question 1", "Question 2").build(2);

        service.persistByEntity(questions);

        List<Question> expectedInitializedQuestions = newQuestion().withId().withName("Question 1", "Question 2").build(2);

        InOrder inOrder = inOrder(entityManagerMock, questionRepositoryMock, formInputTemplatePersistorMock);

        inOrder.verify(entityManagerMock).detach(questions.get(0));
        inOrder.verify(questionRepositoryMock).save(refEq(expectedInitializedQuestions.get(0)));
        inOrder.verify(formInputTemplatePersistorMock).persistByParentEntity(refEq(expectedInitializedQuestions.get(0)));

        inOrder.verify(entityManagerMock).detach(questions.get(1));
        inOrder.verify(questionRepositoryMock).save(refEq(expectedInitializedQuestions.get(1)));
        inOrder.verify(formInputTemplatePersistorMock).persistByParentEntity(refEq(expectedInitializedQuestions.get(1)));
    }

    @Test
    public void persistByPrecedingEntity_questionIsInitializedAndFormInputPersistCalledInCorrectOrder() throws Exception {
        List<Question> questions = newQuestion().withId(1L, 2L).withName("Question 1", "Question 2").build(2);
        Competition competition = newCompetition().build();
        Section section = newSection()
                .withCompetition(competition)
                .withQuestions(questions).build();

        service.persistByParentEntity(section);

        List<Question> expectedInitializedQuestions = newQuestion()
                .withId()
                .withName("Question 1", "Question 2")
                .withSection(section)
                .withCompetition(competition)
                .build(2);

        InOrder inOrder = inOrder(entityManagerMock, questionRepositoryMock, formInputTemplatePersistorMock);

        inOrder.verify(entityManagerMock).detach(questions.get(0));
        inOrder.verify(questionRepositoryMock).save(refEq(expectedInitializedQuestions.get(0)));
        inOrder.verify(formInputTemplatePersistorMock).persistByParentEntity(refEq(expectedInitializedQuestions.get(0)));

        inOrder.verify(entityManagerMock).detach(questions.get(1));
        inOrder.verify(questionRepositoryMock).save(refEq(expectedInitializedQuestions.get(1)));
        inOrder.verify(formInputTemplatePersistorMock).persistByParentEntity(refEq(expectedInitializedQuestions.get(1)));
    }

    @Test
    public void deleteEntityById_questionIsDeletedAndFormInputCleanIsCalledInCorrectOrder() throws Exception {
        Question question = newQuestion().withId(1L).build();

        when(questionRepositoryMock.findOne(question.getId())).thenReturn(question);

        service.deleteEntityById(question.getId());

        InOrder inOrder = inOrder(formInputTemplatePersistorMock, entityManagerMock, questionRepositoryMock, setupStatusRepository);
        inOrder.verify(formInputTemplatePersistorMock).cleanForParentEntity(question);
        inOrder.verify(entityManagerMock).detach(question);
        inOrder.verify(setupStatusRepository).deleteByClassNameAndClassPk(Question.class.getName(), question.getId());
        inOrder.verify(questionRepositoryMock).delete(question.getId());
    }

    @Test
    public void cleanForPrecedingEntity() throws Exception {
        List<Question> questions = newQuestion().withId(1L,2L).build(2);
        Section section = newSection().withQuestions(questions).build();

        service.cleanForParentEntity(section);

        InOrder inOrder = inOrder(formInputTemplatePersistorMock, entityManagerMock, questionRepositoryMock, setupStatusRepository);
        inOrder.verify(formInputTemplatePersistorMock).cleanForParentEntity(questions.get(0));
        inOrder.verify(entityManagerMock).detach(questions.get(0));
        inOrder.verify(setupStatusRepository).deleteByClassNameAndClassPk(Question.class.getName(), questions.get(0).getId());
        inOrder.verify(questionRepositoryMock).delete(questions.get(0).getId());

        inOrder.verify(formInputTemplatePersistorMock).cleanForParentEntity(questions.get(1));
        inOrder.verify(entityManagerMock).detach(questions.get(1));
        inOrder.verify(setupStatusRepository).deleteByClassNameAndClassPk(Question.class.getName(), questions.get(1).getId());
        inOrder.verify(questionRepositoryMock).delete(questions.get(1).getId());
    }
}