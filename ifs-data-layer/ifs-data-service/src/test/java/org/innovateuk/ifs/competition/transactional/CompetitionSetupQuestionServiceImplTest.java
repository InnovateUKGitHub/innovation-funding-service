package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.GuidanceRow;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.repository.GuidanceRowRepository;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType;
import org.innovateuk.ifs.competition.resource.GuidanceRowResource;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.mapper.GuidanceRowMapper;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.application.builder.GuidanceRowBuilder.newFormInputGuidanceRow;
import static org.innovateuk.ifs.application.builder.GuidanceRowResourceBuilder.newFormInputGuidanceRowResourceBuilder;
import static org.innovateuk.ifs.application.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupQuestionResourceBuilder.newCompetitionSetupQuestionResource;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the CompetitionSetupQuestionServiceImpl with mocked repositories/mappers.
 */
public class CompetitionSetupQuestionServiceImplTest extends BaseServiceUnitTest<CompetitionSetupQuestionServiceImpl> {

    @Override
    protected CompetitionSetupQuestionServiceImpl supplyServiceUnderTest() {
        return new CompetitionSetupQuestionServiceImpl();
    }

    private static String number = "number";
    private static String shortTitle = CompetitionSetupQuestionType.SCOPE.getShortName();
    private static String newShortTitle = "CannotBeSet";
    private static String title = "title";
    private static String subTitle = "subTitle";
    private static String guidanceTitle = "guidanceTitle";
    private static String guidance = "guidance";
    private static Integer maxWords = 1;
    private static String assessmentGuidanceAnswer = "assessmentGuidance";
    private static String assessmentGuidanceTitle = "assessmentGuidanceTitle";
    private static Integer assessmentMaxWords = 2;
    private static Integer scoreTotal = 10;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private FormInputRepository formInputRepository;

    @Mock
    private GuidanceRowMapper guidanceRowMapper;

    @Mock
    private GuidanceRowRepository guidanceRowRepository;

    @Test
    public void test_getByQuestionId() {
        Long questionId = 1L;
        List<GuidanceRow> guidanceRows = newFormInputGuidanceRow().build(1);
        Question question = newQuestion().
                withFormInputs(asList(
                        newFormInput()
                                .withType(FormInputType.FILEUPLOAD)
                                .withScope(FormInputScope.APPLICATION)
                                .build(),
                        newFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withScope(FormInputScope.APPLICATION)
                                .withWordCount(maxWords)
                                .withGuidanceTitle(guidanceTitle)
                                .withGuidanceAnswer(guidance)
                                .build(),
                        newFormInput()
                                .withType(FormInputType.TEXTAREA)
                                .withScope(FormInputScope.ASSESSMENT)
                                .withWordCount(assessmentMaxWords)
                                .withGuidanceTitle(assessmentGuidanceTitle)
                                .withGuidanceAnswer(assessmentGuidanceAnswer)
                                .withGuidanceRows(guidanceRows)
                                .build(),
                        newFormInput()
                                .withType(FormInputType.ASSESSOR_SCORE)
                                .withScope(FormInputScope.ASSESSMENT)
                                .build(),
                        newFormInput()
                                .withType(FormInputType.ASSESSOR_RESEARCH_CATEGORY)
                                .withScope(FormInputScope.ASSESSMENT)
                                .build(),
                        newFormInput()
                                .withType(FormInputType.ASSESSOR_APPLICATION_IN_SCOPE)
                                .withScope(FormInputScope.ASSESSMENT)
                                .build()
                        )

                )
                .withQuestionNumber(number)
                .withAssessorMaximumScore(scoreTotal)
                .withDescription(subTitle)
                .withShortName(shortTitle)
                .withName(title)
                .withId(questionId)
                .build();


        when(questionRepository.findOne(questionId)).thenReturn(question);
        when(guidanceRowMapper.mapToResource(guidanceRows)).thenReturn(new ArrayList<>());

        ServiceResult<CompetitionSetupQuestionResource> result = service.getByQuestionId(questionId);

        assertTrue(result.isSuccess());

        CompetitionSetupQuestionResource resource = result.getSuccessObjectOrThrowException();

        assertEquals(resource.getAppendix(), true);
        assertEquals(resource.getScored(), true);
        assertEquals(resource.getWrittenFeedback(), true);
        assertEquals(resource.getScope(), true);
        assertEquals(resource.getResearchCategoryQuestion(), true);
        assertEquals(resource.getAssessmentGuidance(), assessmentGuidanceAnswer);
        assertEquals(resource.getAssessmentGuidanceTitle(), assessmentGuidanceTitle);
        assertEquals(resource.getAssessmentMaxWords(), assessmentMaxWords);
        assertEquals(resource.getGuidanceTitle(), guidanceTitle);
        assertEquals(resource.getMaxWords(), maxWords);
        assertEquals(resource.getScoreTotal(), scoreTotal);
        assertEquals(resource.getNumber(), number);
        assertEquals(resource.getQuestionId(), questionId);
        assertEquals(resource.getSubTitle(), subTitle);
        assertEquals(resource.getShortTitle(), shortTitle);
        assertEquals(resource.getTitle(), title);
        assertEquals(resource.getGuidance(), guidance);
        assertEquals(resource.getType(), CompetitionSetupQuestionType.SCOPE);
        assertEquals(resource.getShortTitleEditable(), false);

        verify(guidanceRowMapper).mapToResource(guidanceRows);
    }

    @Test
    public void test_save() {
        long questionId = 1L;

        List<GuidanceRowResource> guidanceRows = newFormInputGuidanceRowResourceBuilder().build(1);
        when(guidanceRowMapper.mapToDomain(guidanceRows)).thenReturn(new ArrayList<>());

        CompetitionSetupQuestionResource resource = newCompetitionSetupQuestionResource()
                .withAppendix(false)
                .withGuidance(guidance)
                .withGuidanceTitle(guidanceTitle)
                .withMaxWords(maxWords)
                .withNumber(number)
                .withTitle(title)
                .withShortTitle(newShortTitle)
                .withSubTitle(subTitle)
                .withQuestionId(questionId)
                .withAssessmentGuidance(assessmentGuidanceAnswer)
                .withAssessmentGuidanceTitle(assessmentGuidanceTitle)
                .withAssessmentMaxWords(assessmentMaxWords)
                .withGuidanceRows(guidanceRows)
                .withScored(true)
                .withScoreTotal(scoreTotal)
                .withWrittenFeedback(true)
                .build();

        Question question = newQuestion().
                withShortName(CompetitionSetupQuestionType.SCOPE.getShortName()).build();

        FormInput questionFormInput = newFormInput().build();
        FormInput appendixFormInput = newFormInput().build();
        FormInput researchCategoryQuestionFormInput = newFormInput().build();
        FormInput scopeQuestionFormInput = newFormInput().build();
        FormInput scoredQuestionFormInput = newFormInput().build();
        FormInput writtenFeedbackFormInput = newFormInput().build();

        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.APPLICATION, FormInputType.TEXTAREA)).thenReturn(questionFormInput);
        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.APPLICATION, FormInputType.FILEUPLOAD)).thenReturn(appendixFormInput);
        when(questionRepository.findOne(questionId)).thenReturn(question);
        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.ASSESSMENT, FormInputType.ASSESSOR_RESEARCH_CATEGORY)).thenReturn(researchCategoryQuestionFormInput);
        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.ASSESSMENT, FormInputType.ASSESSOR_APPLICATION_IN_SCOPE)).thenReturn(scopeQuestionFormInput);
        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.ASSESSMENT, FormInputType.ASSESSOR_SCORE)).thenReturn(scoredQuestionFormInput);
        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.ASSESSMENT, FormInputType.TEXTAREA)).thenReturn(writtenFeedbackFormInput);

        doNothing().when(guidanceRowRepository).delete(writtenFeedbackFormInput.getGuidanceRows());
        when(guidanceRowRepository.save(writtenFeedbackFormInput.getGuidanceRows())).thenReturn(writtenFeedbackFormInput.getGuidanceRows());

        ServiceResult<CompetitionSetupQuestionResource> result = service.save(resource);

        assertTrue(result.isSuccess());
        assertNotEquals(question.getQuestionNumber(), number);
        assertEquals(question.getDescription(), subTitle);
        assertEquals(question.getName(), title);
        assertEquals(questionFormInput.getGuidanceTitle(), guidanceTitle);
        assertEquals(questionFormInput.getGuidanceAnswer(), guidance);
        assertEquals(questionFormInput.getWordCount(), maxWords);
        assertEquals(writtenFeedbackFormInput.getGuidanceAnswer(), assessmentGuidanceAnswer);
        assertEquals(writtenFeedbackFormInput.getGuidanceTitle(), assessmentGuidanceTitle);
        //Short name shouldn't be set on SCOPE question.
        assertNotEquals(question.getShortName(), newShortTitle);
        assertEquals(question.getShortName(), shortTitle);

        assertEquals(appendixFormInput.getActive(), false);

        assertEquals(researchCategoryQuestionFormInput.getActive(), true);
        assertEquals(scopeQuestionFormInput.getActive(), true);
        assertEquals(scoredQuestionFormInput.getActive(), true);
        assertEquals(writtenFeedbackFormInput.getActive(), true);

        verify(guidanceRowMapper).mapToDomain(guidanceRows);
    }
}
