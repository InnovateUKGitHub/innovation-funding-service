package org.innovateuk.ifs.form.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType;
import org.innovateuk.ifs.form.builder.FormInputBuilder;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.mapper.QuestionMapper;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.security.SecuritySetter.addBasicSecurityUser;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.junit.Assert.*;

@Rollback
public class QuestionControllerIntegrationTest extends BaseControllerIntegrationTest<QuestionController> {

    @Autowired
    private FormInputRepository formInputRepository;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuestionMapper questionMapper;

    private final Long questionId = 13L;
    private QuestionResource questionResource;
    private Question question;
    private Long competitionId = 1L;

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
    public void testGetQuestionById() throws Exception {
        questionResource = controller.getQuestionById(questionId).getSuccess();

        assertNotNull(questionResource);
        assertEquals("How does your project align with the scope of this competition?", questionResource.getName());
    }

    @Test
    public void testGetQuestionByIdRemovesInactiveFormInputs() throws Exception {
        //Create an inactive form input for the question.
        Question question = questionRepository.findById(questionId).get();
        FormInputBuilder baseInput = newFormInput()
                .with(id(null))
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

        questionResource = controller.getQuestionById(questionId).getSuccess();

        assertFalse(questionResource.getFormInputs().contains(inactiveFormInput.getId()));
        assertTrue(questionResource.getFormInputs().contains(activeFormInput.getId()));
    }

    @Test
    public void testFindByCompetition() throws Exception {
        List<QuestionResource> questions = controller.findByCompetition(competitionId).getSuccess();

        assertNotNull(questions);
        assertTrue(questions.size() > 5);
    }

    @Test
    public void testGetNextQuestion() throws Exception {
        QuestionResource nextQuestion = controller.getNextQuestion(9L).getSuccess();
        assertNotNull(nextQuestion);
        assertEquals(new Long(11L), nextQuestion.getId());
    }

    @Test
    public void testGetPreviousQuestion() throws Exception {
        QuestionResource previousQuestion = controller.getPreviousQuestion(11L).getSuccess();

        assertNotNull(previousQuestion);
        assertEquals(new Long(9L), previousQuestion.getId());
    }

    @Test
    public void testGetPreviousQuestionBySection() throws Exception {
        QuestionResource previousQuestion = controller.getPreviousQuestionBySection(10L).getSuccess();
        assertNotNull(previousQuestion);
        assertNotNull(previousQuestion.getId());
        assertEquals(16L , previousQuestion.getId().longValue());
    }

    @Test
    public void testGetNextQuestionBySection() throws Exception {
        QuestionResource nextQuestion = controller.getNextQuestionBySection(10L).getSuccess();
        assertNotNull(nextQuestion);
        assertNotNull(nextQuestion.getId());
        assertEquals(40L, nextQuestion.getId().longValue());
    }

    @Test
    public void testGetQuestionByIdAndAssessmentId() throws Exception {
        loginFelixWilson();
        Long questionId = 1L;
        Long assessmentId = 7L;

        QuestionResource question = questionService.getQuestionByIdAndAssessmentId(questionId, assessmentId).getSuccess();
        assertEquals(questionId, question.getId());
    }

    @Test
    public void testGetQuestionsByAssessmentId() throws Exception {
        loginFelixWilson();
        Long assessmentId = 7L;

        List<QuestionResource> questions = questionService.getQuestionsByAssessmentId(assessmentId).getSuccess();
        // Since the assessment is for an application of competition 1, expect all of the questions of this competition that are visible for assessment
        assertEquals(asList(9L, 11L, 12L, 13L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 15L, 16L),
                simpleMap(questions, QuestionResource::getId));
    }

    @Test
    public void getQuestionByCompetitionIdAndCompetitionSetupQuestionType() {
        long competitionId = 1L;
        CompetitionSetupQuestionType type = CompetitionSetupQuestionType.APPLICATION_DETAILS;

        QuestionResource question = questionService.getQuestionByCompetitionIdAndCompetitionSetupQuestionType
                (competitionId, type).getSuccess();

        assertEquals(9L, question.getId().longValue());
    }
}
