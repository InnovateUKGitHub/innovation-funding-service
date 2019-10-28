package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.domain.QuestionStatus;
import org.innovateuk.ifs.application.repository.QuestionStatusRepository;
import org.innovateuk.ifs.application.transactional.QuestionStatusService;
import org.innovateuk.ifs.commons.security.SecuritySetter;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.mapper.QuestionMapper;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.security.SecuritySetter.addBasicSecurityUser;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.*;

@Rollback
public class QuestionStatusControllerIntegrationTest extends BaseControllerIntegrationTest<QuestionStatusController> {

    @Autowired
    private QuestionStatusRepository questionStatusRepository;
    @Autowired
    private FormInputRepository formInputRepository;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionStatusService questionStatusService;

    private static final long userId = SecuritySetter.basicSecurityUser.getId();
    private static final long applicationId = 1L;
    private static final long questionId = 13L;
    private Question question;
    private static final long newAssigneeProcessRoleId = 5L;
    private static final long organisationId = 3L;
    private static final long competitionId = 1L;
    private static final long QUESTION_ID_WITH_MULTIPLE = 28L;

    @Before
    public void setup(){
        question = questionRepository.findById(questionId).orElse(null);
        QuestionResource questionResource = questionMapper.mapToResource(question);

        addBasicSecurityUser();
    }

    @Override
    @Autowired
    protected void setControllerUnderTest(QuestionStatusController controller) {
        this.controller = controller;
    }

    @Test
    public void testMarkAsComplete() {
        controller.markAsComplete(questionId, applicationId, userId);


        List<QuestionStatus> statuses = questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);

        statuses.forEach(
                s -> assertTrue(s.getMarkedAsComplete())
        );
    }

    @Test
    public void testMarkAsInComplete() {
        controller.markAsInComplete(questionId, applicationId, userId);

        List<QuestionStatus> statuses = questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);

        statuses.forEach(
                s -> assertFalse(s.getMarkedAsComplete())
        );

    }

     @Test
     public void testAssign() {
        controller.assign(questionId, applicationId, newAssigneeProcessRoleId, userId);

        List<QuestionStatus> statuses = questionStatusRepository.findByQuestionIdAndApplicationId(questionId, applicationId);

         statuses.forEach(
                s -> assertEquals(newAssigneeProcessRoleId, (long) s.getAssignee().getId())
        );
    }

    @Test
    public void testGetMarkedAsComplete() {
        // Start with zero completed
        Set<Long> markedAsComplete = controller.getMarkedAsComplete(applicationId, organisationId).getSuccess();
        assertNotNull(markedAsComplete);
        assertEquals(4, markedAsComplete.size());

        // Complete one section
        controller.markAsComplete(questionId, applicationId, userId);
        markedAsComplete = controller.getMarkedAsComplete(applicationId, organisationId).getSuccess();
        assertNotNull(markedAsComplete);
        assertEquals(5, markedAsComplete.size());

        // Mark section as incomplete again.
        controller.markAsInComplete(questionId, applicationId, userId);
        markedAsComplete = controller.getMarkedAsComplete(applicationId, organisationId).getSuccess();
        assertNotNull(markedAsComplete);
        assertEquals(4, markedAsComplete.size());
    }

    @Test
    public void testUpdateNotification() {
        QuestionStatus questionStatus = questionStatusRepository.findByQuestionIdAndApplicationIdAndAssigneeId(questionId, applicationId, userId);

        controller.updateNotification(questionStatus.getId(), true);

        questionStatus = questionStatusRepository.findByQuestionIdAndApplicationIdAndAssigneeId(questionId, applicationId, userId);
        assertTrue(questionStatus.getNotified());

        controller.updateNotification(questionStatus.getId(), false);

        questionStatus = questionStatusRepository.findByQuestionIdAndApplicationIdAndAssigneeId(questionId, applicationId, userId);
        assertFalse(questionStatus.getNotified());
    }

    @Test
    public void testIsMarkedAsComplete() {
        assertFalse(questionStatusService.isMarkedAsComplete(question, applicationId, organisationId).getSuccess());

        controller.markAsComplete(questionId, applicationId, userId);

        assertTrue(questionStatusService.isMarkedAsComplete(question, applicationId, organisationId).getSuccess());
    }

    @Test
    public void testIsMarkedAsCompleteMultiple() {
        question = questionRepository.findById(QUESTION_ID_WITH_MULTIPLE).get();
        controller.markAsInComplete(QUESTION_ID_WITH_MULTIPLE, applicationId, userId);

        assertFalse(questionStatusService.isMarkedAsComplete(question, applicationId, organisationId).getSuccess());

        controller.markAsComplete(QUESTION_ID_WITH_MULTIPLE, applicationId, userId);
        controller.markAsComplete(QUESTION_ID_WITH_MULTIPLE, applicationId, 2L);
        controller.markAsComplete(QUESTION_ID_WITH_MULTIPLE, applicationId, 8L);
        controller.markAsComplete(QUESTION_ID_WITH_MULTIPLE, applicationId, 9L);

        assertTrue(questionStatusService.isMarkedAsComplete(question, applicationId, organisationId).getSuccess());
    }

    @Test
    public void testGetQuestionByIdAndAssessmentId() {
        loginFelixWilson();
        Long questionId = 1L;
        Long assessmentId = 7L;

        QuestionResource question = questionService.getQuestionByIdAndAssessmentId(questionId, assessmentId).getSuccess();
        assertEquals(questionId, question.getId());
    }

    @Test
    public void testGetQuestionsByAssessmentId() {
        loginFelixWilson();
        Long assessmentId = 7L;

        List<QuestionResource> questions = questionService.getQuestionsByAssessmentId(assessmentId).getSuccess();
        // Since the assessment is for an application of competition 1, expect all of the questions of this competition that are visible for assessment
        assertEquals(asList(248L, 9L, 249L, 11L, 12L, 13L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 15L, 16L, 269L),
                simpleMap(questions, QuestionResource::getId));
    }
}
