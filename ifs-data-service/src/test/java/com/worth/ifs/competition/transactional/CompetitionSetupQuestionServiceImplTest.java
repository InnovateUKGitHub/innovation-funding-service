package com.worth.ifs.competition.transactional;

import com.worth.ifs.*;
import com.worth.ifs.application.domain.*;
import com.worth.ifs.application.repository.*;
import com.worth.ifs.assessment.resource.*;
import com.worth.ifs.commons.service.*;
import com.worth.ifs.competition.resource.*;
import com.worth.ifs.form.domain.*;
import com.worth.ifs.form.mapper.*;
import com.worth.ifs.form.repository.*;
import com.worth.ifs.form.resource.*;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.application.builder.GuidanceRowBuilder.*;
import static com.worth.ifs.application.builder.QuestionBuilder.*;
import static com.worth.ifs.competition.builder.CompetitionSetupQuestionResourceBuilder.*;
import static com.worth.ifs.form.builder.FormInputBuilder.*;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the CompetitionServiceImpl with mocked repositories/mappers.
 */
public class CompetitionSetupQuestionServiceImplTest extends BaseServiceUnitTest<CompetitionSetupQuestionServiceImpl> {

    @Override
    protected CompetitionSetupQuestionServiceImpl supplyServiceUnderTest() {
        return new CompetitionSetupQuestionServiceImpl();
    }
    private static String number = "number";
    private static String shortTitle = "shortTitle";
    private static String title = "title";
    private static String subTitle = "subTitle";
    private static String guidanceTitle = "guidanceTitle";
    private static String guidance = "guidance";
    private static Integer maxWords = 1;
    private static String assessmentGuidance = "assessmentGuidance";
    private static Integer assessmentMaxWords = 2;
    private static Integer scoreTotal = 10;


    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private FormInputRepository formInputRepository;

    @Mock
    private FormInputTypeRepository formInputTypeRepository;

    @Mock
    private GuidanceRowMapper guidanceRowMapper;

    @Test
    public void test_getByQuestionId() {
        Long questionId = 1L;
        List<GuidanceRow> guidanceRows = newFormInputGuidanceRow().build(1);
        Question question = newQuestion().
                withFormInputs(asList(
                        newFormInput()
                            .withFormInputType(ApplicantFormInputType.FILE_UPLOAD.getTitle())
                            .withScope(FormInputScope.APPLICATION)
                            .build(),
                        newFormInput()
                            .withFormInputType(ApplicantFormInputType.QUESTION.getTitle())
                            .withScope(FormInputScope.APPLICATION)
                            .withWordCount(maxWords)
                            .withGuidanceQuestion(guidanceTitle)
                            .withGuidanceAnswer(guidance)
                            .build(),
                        newFormInput()
                            .withFormInputType(AssessorFormInputType.FEEDBACK.getTitle())
                            .withScope(FormInputScope.ASSESSMENT)
                            .withWordCount(assessmentMaxWords)
                            .withGuidanceQuestion(assessmentGuidance)
                            .withFormInputGuidanceRows(guidanceRows)
                            .build(),
                        newFormInput()
                            .withFormInputType(AssessorFormInputType.SCORE.getTitle())
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
        assertEquals(resource.getAssessmentGuidance(), assessmentGuidance);
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

        verify(guidanceRowMapper).mapToResource(guidanceRows);
    }

    @Test
    public void test_save() {
        long questionId = 1L;

        CompetitionSetupQuestionResource resource = newCompetitionSetupQuestionResource()
                .withAppendix(false)
                .withGuidance(guidance)
                .withGuidanceTitle(guidanceTitle)
                .withMaxWords(maxWords)
                .withNumber(number)
                .withTitle(title)
                .withShortTitle(shortTitle)
                .withSubTitle(subTitle)
                .withQuestionId(questionId)
                .build();

        Question question = newQuestion().build();

        FormInput questionFormInput = newFormInput().build();
        FormInput appendixFormInput = newFormInput().build();

        when(formInputRepository.findByQuestionIdAndScopeAndFormInputType_Title(questionId, FormInputScope.APPLICATION, ApplicantFormInputType.QUESTION.getTitle())).thenReturn(questionFormInput);
        when(formInputRepository.findByQuestionIdAndScopeAndFormInputType_Title(questionId, FormInputScope.APPLICATION, ApplicantFormInputType.FILE_UPLOAD.getTitle())).thenReturn(appendixFormInput);
        when(questionRepository.findOne(questionId)).thenReturn(question);

        ServiceResult<CompetitionSetupQuestionResource> result = service.save(resource);

        verify(formInputRepository).delete(appendixFormInput);

        assertNotEquals(question.getQuestionNumber(), number);
        assertEquals(question.getDescription(), subTitle);
        assertEquals(question.getShortName(), shortTitle);
        assertEquals(question.getName(), title);
        assertEquals(questionFormInput.getGuidanceQuestion(), guidanceTitle);
        assertEquals(questionFormInput.getGuidanceAnswer(), guidance);
        assertEquals(questionFormInput.getWordCount(), maxWords);
    }
}
