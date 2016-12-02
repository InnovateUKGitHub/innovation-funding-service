package com.worth.ifs.competition.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.domain.GuidanceRow;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.repository.QuestionRepository;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionSetupQuestionResource;
import com.worth.ifs.competition.resource.CompetitionSetupQuestionType;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.mapper.GuidanceRowMapper;
import com.worth.ifs.form.repository.FormInputRepository;
import com.worth.ifs.form.resource.FormInputScope;
import com.worth.ifs.form.resource.FormInputType;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.application.builder.GuidanceRowBuilder.newFormInputGuidanceRow;
import static com.worth.ifs.application.builder.QuestionBuilder.newQuestion;
import static com.worth.ifs.competition.builder.CompetitionSetupQuestionResourceBuilder.newCompetitionSetupQuestionResource;
import static com.worth.ifs.form.builder.FormInputBuilder.newFormInput;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
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
    private static String assessmentGuidance = "assessmentGuidance";
    private static Integer assessmentMaxWords = 2;
    private static Integer scoreTotal = 10;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private FormInputRepository formInputRepository;

    @Mock
    private GuidanceRowMapper guidanceRowMapper;

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
                                .withGuidanceTitle(assessmentGuidance)
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
        assertEquals(resource.getType(), CompetitionSetupQuestionType.SCOPE);
        assertEquals(resource.getShortTitleEditable(), false);

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
                .withShortTitle(newShortTitle)
                .withSubTitle(subTitle)
                .withQuestionId(questionId)
                .build();

        Question question = newQuestion().
                withShortName(CompetitionSetupQuestionType.SCOPE.getShortName()).build();

        FormInput questionFormInput = newFormInput().build();
        FormInput appendixFormInput = newFormInput().build();

        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.APPLICATION, FormInputType.TEXTAREA)).thenReturn(questionFormInput);
        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.APPLICATION, FormInputType.FILEUPLOAD)).thenReturn(appendixFormInput);
        when(questionRepository.findOne(questionId)).thenReturn(question);

        ServiceResult<CompetitionSetupQuestionResource> result = service.save(resource);

        assertTrue(result.isSuccess());
        assertNotEquals(question.getQuestionNumber(), number);
        assertEquals(question.getDescription(), subTitle);
        assertEquals(question.getName(), title);
        assertEquals(questionFormInput.getGuidanceTitle(), guidanceTitle);
        assertEquals(questionFormInput.getGuidanceAnswer(), guidance);
        assertEquals(questionFormInput.getWordCount(), maxWords);
        //Short name shouldn't be set on SCOPE question.
        assertNotEquals(question.getShortName(), newShortTitle);
        assertEquals(question.getShortName(), shortTitle);

        assertEquals(appendixFormInput.getActive(), false);
    }
}
