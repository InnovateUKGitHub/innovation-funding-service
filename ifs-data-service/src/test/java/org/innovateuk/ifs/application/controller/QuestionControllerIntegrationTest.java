package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.QuestionStatus;
import org.innovateuk.ifs.application.mapper.QuestionMapper;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.application.repository.QuestionStatusRepository;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.transactional.QuestionService;
import org.innovateuk.ifs.commons.security.SecuritySetter;
import org.innovateuk.ifs.form.builder.FormInputBuilder;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.commons.security.SecuritySetter.addBasicSecurityUser;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

@Rollback
public class QuestionControllerIntegrationTest extends BaseControllerIntegrationTest<QuestionController> {


    @Autowired
    QuestionStatusRepository questionStatusRepository;
    @Autowired
    FormInputRepository formInputRepository;
    @Autowired
    QuestionService questionService;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    QuestionMapper questionMapper;


    private final Long userId = SecuritySetter.basicSecurityUser.getId();
    private final Long applicationId = 1L;
    private final Long questionId = 13L;
    private QuestionResource questionResource;
    private Question question;
    private Long newAssigneeProcessRoleId = 5L;
    private Long organisationId = 3L;
    private Long questionStatusId = 2L;
    private Long competitionId = 1L;
    private Long sectionId = 2L;
    public static final long QUESTION_ID_WITH_MULTIPLE = 35L;


    @Before
    public void setup(){
        question = questionRepository.findOne(questionId);
        questionResource = questionMapper.mapToResource(question);

        addBasicSecurityUser();
    }


    @Override
    @Autowired
    protected void setControllerUnderTest(QuestionController controller) {
        this.controller = controller;
    }

    @Test
    public void testGetQuestionById() throws Exception {
        questionResource= controller.getQuestionById(questionId).getSuccessObject();

        assertNotNull(questionResource);
        assertEquals("How does your project align with the scope of this competition?", questionResource.getName());
    }

    @Test
    public void testGetQuestionByIdRemovesInactiveFormInputs() throws Exception {
        //Create an inactive form input for the question.
        Question question = questionRepository.findOne(questionId);
        FormInputBuilder baseInput = newFormInput()
                .withQuestion(question)
                .withPriority(1)
                .withType(FormInputType.TEXTAREA)
                .withScope(FormInputScope.APPLICATION)
                .withQuestion(question);
        FormInput inactiveFormInput = baseInput.withActive(false).build();
        FormInput activeFormInput = baseInput.withActive(true).build();
        formInputRepository.save(inactiveFormInput);
        formInputRepository.save(activeFormInput);
        flushAndClearSession();

        questionResource = controller.getQuestionById(questionId).getSuccessObject();

        assertFalse(questionResource.getFormInputs().contains(inactiveFormInput.getId()));
        assertTrue(questionResource.getFormInputs().contains(activeFormInput.getId()));
    }

    @Test
    public void testMarkAsComplete() throws Exception {
        controller.markAsComplete(questionId, applicationId, userId);


        List<QuestionStatus> statuses = questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);

        statuses.forEach(
                s -> assertTrue(s.getMarkedAsComplete())
        );
    }

    @Test
    public void testMarkAsInComplete() throws Exception {
        controller.markAsInComplete(questionId, applicationId, userId);

        List<QuestionStatus> statuses = questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);

        statuses.forEach(
                s -> assertFalse(s.getMarkedAsComplete())
        );

    }

     @Test
     public void testAssign() throws Exception {
        controller.assign(questionId, applicationId, newAssigneeProcessRoleId, userId);

        List<QuestionStatus> statuses = questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);

         statuses.forEach(
                s -> assertEquals(newAssigneeProcessRoleId, s.getAssignee().getId())
        );
    }

    @Ignore
    @Test
    public void testAssignMultiple() throws Exception {
        //Todo: don't know how to implement this, can we assign questions that are in the finance form for example?
    }

    @Test
    public void testGetMarkedAsComplete() throws Exception {
        // Start with zero completed
        Set<Long> markedAsComplete = controller.getMarkedAsComplete(applicationId, organisationId).getSuccessObject();
        assertNotNull(markedAsComplete);
        assertEquals(7, markedAsComplete.size());

        // Complete one section
        controller.markAsComplete(questionId, applicationId, userId);
        markedAsComplete = controller.getMarkedAsComplete(applicationId, organisationId).getSuccessObject();
        assertNotNull(markedAsComplete);
        assertEquals(8, markedAsComplete.size());

        // Mark section as incomplete again.
        controller.markAsInComplete(questionId, applicationId, userId);
        markedAsComplete = controller.getMarkedAsComplete(applicationId, organisationId).getSuccessObject();
        assertNotNull(markedAsComplete);
        assertEquals(7, markedAsComplete.size());
    }

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

    @Test
    public void testFindByCompetition() throws Exception {
        List<QuestionResource> questions = controller.findByCompetition(competitionId).getSuccessObject();

        assertNotNull(questions);
        assertTrue(questions.size() > 5);
    }

    @Test
    public void testGetNextQuestion() throws Exception {
        QuestionResource nextQuestion = controller.getNextQuestion(9L).getSuccessObject();
        assertNotNull(nextQuestion);
        assertEquals(new Long(11L), nextQuestion.getId());
    }

    @Test
    public void testGetPreviousQuestion() throws Exception {
        QuestionResource previousQuestion = controller.getPreviousQuestion(11L).getSuccessObject();

        assertNotNull(previousQuestion);
        assertEquals(new Long(9L), previousQuestion.getId());
    }

    @Test
    public void testGetPreviousQuestionBySection() throws Exception {
        QuestionResource previousQuestion = controller.getPreviousQuestionBySection(10L).getSuccessObject();
        assertNotNull(previousQuestion);
        assertNotNull(previousQuestion.getId());
        assertEquals(16L , previousQuestion.getId().longValue());
    }

    @Test
    public void testGetNextQuestionBySection() throws Exception {
        QuestionResource nextQuestion = controller.getNextQuestionBySection(10L).getSuccessObject();
        assertNotNull(nextQuestion);
        assertNotNull(nextQuestion.getId());
        assertEquals(40L, nextQuestion.getId().longValue());
    }

    @Test
    public void testIsMarkedAsComplete() throws Exception {
        assertFalse(questionService.isMarkedAsComplete(question, applicationId, organisationId).getSuccessObject());

        controller.markAsComplete(questionId, applicationId, userId);

        assertTrue(questionService.isMarkedAsComplete(question, applicationId, organisationId).getSuccessObject());
    }

    @Test
    public void testIsMarkedAsCompleteMultiple() throws Exception {
        question = questionRepository.findOne(QUESTION_ID_WITH_MULTIPLE);

        assertFalse(questionService.isMarkedAsComplete(question, applicationId, organisationId).getSuccessObject());

        controller.markAsComplete(QUESTION_ID_WITH_MULTIPLE, applicationId, userId);
        controller.markAsComplete(QUESTION_ID_WITH_MULTIPLE, applicationId, 2L);
        controller.markAsComplete(QUESTION_ID_WITH_MULTIPLE, applicationId, 8L);
        controller.markAsComplete(QUESTION_ID_WITH_MULTIPLE, applicationId, 9L);

        assertTrue(questionService.isMarkedAsComplete(question, applicationId, organisationId).getSuccessObject());
    }

    @Test
    public void testGetQuestionByIdAndAssessmentId() throws Exception {
        loginFelixWilson();
        Long questionId = 1L;
        Long assessmentId = 7L;

        QuestionResource question = questionService.getQuestionByIdAndAssessmentId(questionId, assessmentId).getSuccessObject();
        assertEquals(questionId, question.getId());
    }

    @Test
    public void testGetQuestionsByAssessmentId() throws Exception {
        loginFelixWilson();
        Long assessmentId = 7L;

        List<QuestionResource> questions = questionService.getQuestionsByAssessmentId(assessmentId).getSuccessObject();
        // Since the assessment is for an application of competition 1, expect all of the questions of this competition that are visible for assessment
        assertEquals(asList(9L, 11L, 12L, 13L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 15L, 16L, 20L, 42L, 40L, 38L, 35L, 41L, 36L),
                simpleMap(questions, QuestionResource::getId));
    }
}
