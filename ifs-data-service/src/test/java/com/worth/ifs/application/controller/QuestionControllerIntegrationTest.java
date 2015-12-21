package com.worth.ifs.application.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.QuestionStatus;
import com.worth.ifs.application.repository.QuestionStatusRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class QuestionControllerIntegrationTest extends BaseControllerIntegrationTest<QuestionController> {


    @Autowired
    QuestionStatusRepository questionStatusRepository;

    private final Long userId = 1L;
    private final Long applicationId = 1L;
    private final Long questionId = 13L;
    private Question question;
    private Long newAssigneeProcessRoleId = 5L;
    private Long organisationId = 3L;
    private Long questionStatusId = 2L;
    private Long competitionId = 1L;
    private Long sectionId = 2L;
    public static final long QUESTION_ID_WITH_MULTIPLE = 35L;


    @Before
    public void setup(){
        question = controller.getQuestionById(questionId);
    }


    @Override
    @Autowired
    protected void setControllerUnderTest(QuestionController controller) {
        this.controller = controller;
    }

    @Rollback
    @Test
    public void testGetQuestionById() throws Exception {
        question = controller.getQuestionById(questionId);

        assertNotNull(question);
        assertEquals("How does your project align with the scope of this competition?", question.getName());
    }

    @Rollback
    @Test
    public void testMarkAsComplete() throws Exception {
        controller.markAsComplete(questionId, applicationId, userId);


        List<QuestionStatus> statuses = questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);

        statuses.forEach(
                s -> assertTrue(s.getMarkedAsComplete())
        );
    }

    @Rollback
    @Test
    public void testMarkAsInComplete() throws Exception {
        controller.markAsInComplete(questionId, applicationId, userId);

        List<QuestionStatus> statuses = questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);

        statuses.forEach(
                s -> assertFalse(s.getMarkedAsComplete())
        );

    }

    @Rollback
     @Test
     public void testAssign() throws Exception {
        controller.assign(questionId, applicationId, newAssigneeProcessRoleId, userId);

        List<QuestionStatus> statuses = questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);

        statuses.forEach(
                s -> assertEquals(newAssigneeProcessRoleId, s.getAssignee().getId())
        );
    }

    @Ignore
    @Rollback
    @Test
    public void testAssignMultiple() throws Exception {
        //Todo: don't know how to implement this, can we assign questions that are in the finance form for example?
    }

    @Rollback
    @Test
    public void testGetMarkedAsComplete() throws Exception {
        // Start with zero completed
        Set<Long> markedAsComplete = controller.getMarkedAsComplete(applicationId, organisationId);
        assertNotNull(markedAsComplete);
        assertEquals(7, markedAsComplete.size());

        // Complete one section
        controller.markAsComplete(questionId, applicationId, userId);
        markedAsComplete = controller.getMarkedAsComplete(applicationId, organisationId);
        assertNotNull(markedAsComplete);
        assertEquals(8, markedAsComplete.size());

        // Mark section as incomplete again.
        controller.markAsInComplete(questionId, applicationId, userId);
        markedAsComplete = controller.getMarkedAsComplete(applicationId, organisationId);
        assertNotNull(markedAsComplete);
        assertEquals(7, markedAsComplete.size());
    }

    @Rollback
    @Test
    public void testUpdateNotification() throws Exception {
        QuestionStatus questionStatus = questionStatusRepository.findByQuestionIdAndApplicationIdAndAssigneeId(questionId, applicationId, userId);

        controller.updateNotification(questionStatus.getId(), true);

        questionStatus = questionStatusRepository.findByQuestionIdAndApplicationIdAndAssigneeId(questionId, applicationId, userId);
        assertTrue(questionStatus.getNotified());

        controller.updateNotification(questionStatus.getId(), false);

        questionStatus = questionStatusRepository.findByQuestionIdAndApplicationIdAndAssigneeId(questionId, applicationId, userId);
        assertFalse(questionStatus.getNotified());
    }

    @Rollback
    @Test
    public void testFindByCompetition() throws Exception {
        List<Question> questions = controller.findByCompetition(competitionId);

        assertNotNull(questions);
        assertTrue(questions.size() > 5);
    }

    @Rollback
    @Test
    public void testGetNextQuestion() throws Exception {
        Question nextQuestion = controller.getNextQuestion(9L);
        assertNotNull(nextQuestion);
        assertEquals(11L, nextQuestion.getId().longValue());
    }

    @Rollback
    @Test
    public void testGetPreviousQuestion() throws Exception {
        Question previousQuestion = controller.getPreviousQuestion(11L);

        assertNotNull(previousQuestion);
        assertEquals(9L, previousQuestion.getId().longValue());
    }

    @Rollback
    @Test
    public void testGetPreviousQuestionBySection() throws Exception {
        Question previousQuestion = controller.getPreviousQuestionBySection(10L);
        assertNotNull(previousQuestion);
        assertNotNull(previousQuestion.getId());
        assertEquals(16L , previousQuestion.getId().longValue());
    }

    @Rollback
    @Test
    public void testGetNextQuestionBySection() throws Exception {
        Question nextQuestion = controller.getNextQuestionBySection(10L);
        assertNotNull(nextQuestion);
        assertNotNull(nextQuestion.getId());
        assertEquals(36L, nextQuestion.getId().longValue());
    }

    @Rollback
    @Test
    public void testIsMarkedAsComplete() throws Exception {
        assertFalse(controller.isMarkedAsComplete(question, applicationId, organisationId));

        controller.markAsComplete(questionId, applicationId, userId);

        assertTrue(controller.isMarkedAsComplete(question, applicationId, organisationId));
    }

    @Rollback
    @Test
    public void testIsMarkedAsCompleteMultiple() throws Exception {
        question = controller.getQuestionById(QUESTION_ID_WITH_MULTIPLE);


        assertFalse(controller.isMarkedAsComplete(question, applicationId, organisationId));

        controller.markAsComplete(QUESTION_ID_WITH_MULTIPLE, applicationId, userId);
        controller.markAsComplete(QUESTION_ID_WITH_MULTIPLE, applicationId, 2L);
        controller.markAsComplete(QUESTION_ID_WITH_MULTIPLE, applicationId, 8L);
        controller.markAsComplete(QUESTION_ID_WITH_MULTIPLE, applicationId, 9L);

        assertTrue(controller.isMarkedAsComplete(question, applicationId, organisationId));
    }
}