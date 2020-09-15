package org.innovateuk.ifs.form.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.mapper.QuestionMapper;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.security.SecuritySetter.addBasicSecurityUser;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.*;

@Rollback
public class QuestionControllerIntegrationTest extends BaseControllerIntegrationTest<QuestionController> {

    private static final List<Long> QUESTION_LIST = asList(248L, 9L, 249L, 11L, 12L, 13L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 15L, 16L, 269L);

    @Autowired
    private FormInputRepository formInputRepository;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuestionMapper questionMapper;

    private static final long questionId = 13L;
    private QuestionResource questionResource;
    private Question question;
    private static final long competitionId = 1L;

    @Before
    public void setup(){
        question = questionRepository.findById(questionId).get();
        questionResource = questionMapper.mapToResource(question);

        addBasicSecurityUser();
    }


    @Override
    @Autowired
    protected void setControllerUnderTest(QuestionController controller) {
        this.controller = controller;
    }

    @Test
    public void getQuestionById() {
        questionResource = controller.getQuestionById(questionId).getSuccess();

        assertNotNull(questionResource);
        assertEquals("How does your project align with the scope of this competition?", questionResource.getName());
    }

    @Test
    public void findByCompetition() {
        List<QuestionResource> questions = controller.findByCompetition(competitionId).getSuccess();

        assertNotNull(questions);
        assertTrue(questions.size() > 5);
    }

    @Test
    public void getNextQuestion() {
        QuestionResource nextQuestion = controller.getNextQuestion(12L).getSuccess();
        assertNotNull(nextQuestion);
        assertEquals(new Long(13L), nextQuestion.getId());
    }

    @Test
    public void getPreviousQuestion() {
        QuestionResource previousQuestion = controller.getPreviousQuestion(12L).getSuccess();

        assertNotNull(previousQuestion);
        assertEquals(new Long(11L), previousQuestion.getId());
    }

    @Test
    public void getQuestionByIdAndAssessmentId() {
        loginFelixWilson();
        Long questionId = 1L;
        Long assessmentId = 7L;

        QuestionResource question = questionService.getQuestionByIdAndAssessmentId(questionId, assessmentId).getSuccess();
        assertEquals(questionId, question.getId());
    }

    @Test
    public void getQuestionsByAssessmentId() {
        loginFelixWilson();
        Long assessmentId = 7L;

        List<QuestionResource> questions = questionService.getQuestionsByAssessmentId(assessmentId).getSuccess();
        // Since the assessment is for an application of competition 1, expect all of the questions of this competition that are visible for assessment
        assertEquals(QUESTION_LIST, simpleMap(questions, QuestionResource::getId));
    }

    @Test
    public void getQuestionByCompetitionIdAndQuestionSetupType() {
        long competitionId = 1L;
        QuestionSetupType type = QuestionSetupType.APPLICATION_DETAILS;

        QuestionResource question = questionService.getQuestionByCompetitionIdAndQuestionSetupType
                (competitionId, type).getSuccess();

        assertEquals(9L, question.getId().longValue());
    }
}