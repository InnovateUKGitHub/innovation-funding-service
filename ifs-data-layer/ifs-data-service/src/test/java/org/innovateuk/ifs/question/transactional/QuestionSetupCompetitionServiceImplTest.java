package org.innovateuk.ifs.question.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.error.CommonErrors;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.innovateuk.ifs.competition.resource.GuidanceRowResource;
import org.innovateuk.ifs.file.resource.FileTypeCategory;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.GuidanceRow;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.mapper.GuidanceRowMapper;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.repository.GuidanceRowRepository;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.form.repository.SectionRepository;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.question.transactional.template.QuestionPriorityOrderService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.util.Arrays.asList;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_EDITABLE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionSetupQuestionResourceBuilder.newCompetitionSetupQuestionResource;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.PDF;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.SPREADSHEET;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.GuidanceRowBuilder.newFormInputGuidanceRow;
import static org.innovateuk.ifs.form.builder.GuidanceRowResourceBuilder.newFormInputGuidanceRowResourceBuilder;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.form.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.form.resource.QuestionType.LEAD_ONLY;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;
import static org.innovateuk.ifs.setup.resource.QuestionSection.PROJECT_DETAILS;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests the QuestionCompetitionServiceImpl with mocked repositories/mappers.
 */
public class QuestionSetupCompetitionServiceImplTest extends BaseServiceUnitTest<QuestionSetupCompetitionServiceImpl> {

    @Override
    protected QuestionSetupCompetitionServiceImpl supplyServiceUnderTest() {
        return new QuestionSetupCompetitionServiceImpl();
    }

    private static String number = "number";
    private static String shortTitle = QuestionSetupType.SCOPE.getShortName();
    private static String newShortTitle = "ScopeTwo";
    private static String title = "title";
    private static String subTitle = "subTitle";
    private static String guidanceTitle = "guidanceTitle";
    private static String guidance = "guidance";
    private static String fileUploadGuidance = "fileUploadGuidance";
    private static Integer maxWords = 1;
    private static String assessmentGuidanceAnswer = "assessmentGuidance";
    private static String assessmentGuidanceTitle = "assessmentGuidanceTitle";
    private static Integer assessmentMaxWords = 2;
    private static Integer scoreTotal = 10;
    private static QuestionSetupType questionSetupType = QuestionSetupType.SCOPE;

    @Mock
    private CompetitionRepository competitionRepositoryMock;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private FormInputRepository formInputRepository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private GuidanceRowMapper guidanceRowMapper;

    @Mock
    private GuidanceRowRepository guidanceRowRepository;

    @Mock
    private QuestionSetupTemplateService questionSetupTemplateService;

    @Mock
    private QuestionPriorityOrderService questionPriorityOrderService;

    @Test
    public void getByQuestionId() {
        Long questionId = 1L;
        List<GuidanceRow> guidanceRows = newFormInputGuidanceRow().build(1);
        Question question = newQuestion().
                withFormInputs(asList(
                        newFormInput()
                                .withType(FormInputType.FILEUPLOAD)
                                .withScope(FormInputScope.APPLICATION)
                                .withAllowedFileTypes(asSet(PDF, SPREADSHEET))
                                .withGuidanceAnswer(fileUploadGuidance)
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
                .withQuestionSetupType(questionSetupType)
                .withId(questionId)
                .build();


        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(guidanceRowMapper.mapToResource(guidanceRows)).thenReturn(new ArrayList<>());

        ServiceResult<CompetitionSetupQuestionResource> result = service.getByQuestionId(questionId);

        assertTrue(result.isSuccess());

        CompetitionSetupQuestionResource resource = result.getSuccess();

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
        assertEquals(resource.getType(), QuestionSetupType.SCOPE);
        assertEquals(resource.getAppendixGuidance(), fileUploadGuidance);
        assertEquals(resource.getAllowedFileTypes(), asSet(PDF, SPREADSHEET));
        verify(guidanceRowMapper).mapToResource(guidanceRows);
    }

    @Test
    public void update() {
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
                withShortName(newShortTitle).build();

        FormInput questionFormInput = newFormInput().build();
        FormInput appendixFormInput = newFormInput().build();
        FormInput researchCategoryQuestionFormInput = newFormInput().build();
        FormInput scopeQuestionFormInput = newFormInput().build();
        FormInput scoredQuestionFormInput = newFormInput().build();
        FormInput writtenFeedbackFormInput = newFormInput()
                .withGuidanceRows(newFormInputGuidanceRow().build(2))
                .build();

        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.APPLICATION, FormInputType.TEXTAREA)).thenReturn(questionFormInput);
        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.APPLICATION, FormInputType.FILEUPLOAD)).thenReturn(appendixFormInput);
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.ASSESSMENT, FormInputType.ASSESSOR_RESEARCH_CATEGORY)).thenReturn(researchCategoryQuestionFormInput);
        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.ASSESSMENT, FormInputType.ASSESSOR_APPLICATION_IN_SCOPE)).thenReturn(scopeQuestionFormInput);
        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.ASSESSMENT, FormInputType.ASSESSOR_SCORE)).thenReturn(scoredQuestionFormInput);
        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.ASSESSMENT, FormInputType.TEXTAREA)).thenReturn(writtenFeedbackFormInput);

        doNothing().when(guidanceRowRepository).deleteAll(writtenFeedbackFormInput.getGuidanceRows());
        when(guidanceRowRepository.saveAll(writtenFeedbackFormInput.getGuidanceRows())).thenReturn(writtenFeedbackFormInput.getGuidanceRows());

        ServiceResult<CompetitionSetupQuestionResource> result = service.update(resource);

        assertTrue(result.isSuccess());
        assertNotEquals(question.getQuestionNumber(), number);
        assertEquals(question.getDescription(), subTitle);
        assertEquals(question.getName(), title);
        assertEquals(questionFormInput.getGuidanceTitle(), guidanceTitle);
        assertEquals(questionFormInput.getGuidanceAnswer(), guidance);
        assertEquals(questionFormInput.getWordCount(), maxWords);
        assertEquals(writtenFeedbackFormInput.getGuidanceAnswer(), assessmentGuidanceAnswer);
        assertEquals(writtenFeedbackFormInput.getGuidanceTitle(), assessmentGuidanceTitle);
        assertEquals(question.getShortName(), newShortTitle);

        assertEquals(appendixFormInput.getActive(), false);
        assertEquals(appendixFormInput.getGuidanceAnswer(), null);

        assertEquals(researchCategoryQuestionFormInput.getActive(), true);
        assertEquals(scopeQuestionFormInput.getActive(), true);
        assertEquals(scoredQuestionFormInput.getActive(), true);
        assertEquals(writtenFeedbackFormInput.getActive(), true);

        verify(guidanceRowMapper).mapToDomain(guidanceRows);
    }

    @Test
    public void update_shouldNotChangeAppendixFormInputWhenOptionIsNull() {
        setMocksForSuccessfulUpdate();
        CompetitionSetupQuestionResource resource = createValidQuestionResourceWithoutAppendixOptions();

        resource.setAppendix(false);
        resource.setAllowedFileTypes(asSet(PDF));
        resource.setAppendixGuidance(fileUploadGuidance);


        boolean appendixEnabled = true;
        String guidanceAnswer = "Only excel files with spaghetti VB macros allowed";
        FileTypeCategory allowedFileTypes = FileTypeCategory.fromDisplayName("PDF");

        FormInput appendixFormInput = newFormInput()
                .withActive(appendixEnabled)
                .withGuidanceAnswer(guidanceAnswer)
                .withAllowedFileTypes(asSet(allowedFileTypes))
                .build();
        //Override repository response set in prerequisites test prep function
        when(formInputRepository.findByQuestionIdAndScopeAndType(
                1L,
                FormInputScope.APPLICATION,
                FormInputType.FILEUPLOAD
        )).thenReturn(appendixFormInput);

        ServiceResult<CompetitionSetupQuestionResource> result = service.update(resource);

        assertEquals(true, result.isSuccess());
        assertNotEquals(appendixEnabled, appendixFormInput.getActive());
        assertNotEquals(allowedFileTypes, appendixFormInput.getAllowedFileTypes());
        assertNotEquals(guidanceAnswer, appendixFormInput.getGuidanceAnswer());
    }

    @Test
    public void update_shouldResetAppendixOptionsFormInputWhenItsNotSelected() {
        setMocksForSuccessfulUpdate();
        CompetitionSetupQuestionResource resource = createValidQuestionResourceWithoutAppendixOptions();

        FileTypeCategory allowedFileTypes = FileTypeCategory.fromDisplayName("PDF");

        resource.setAppendix(false);
        resource.setAllowedFileTypes(asSet(PDF));
        resource.setAppendixGuidance(fileUploadGuidance);

        FormInput appendixFormInput = newFormInput()
                .withActive(true)
                .withGuidanceAnswer("Only excel files with spaghetti VB macros allowed")
                .withAllowedFileTypes(asSet(allowedFileTypes))
                .build();

        //Override repository response set in prerequisites test prep function
        when(formInputRepository.findByQuestionIdAndScopeAndType(
                1L,
                FormInputScope.APPLICATION,
                FormInputType.FILEUPLOAD
        )).thenReturn(appendixFormInput);

        ServiceResult<CompetitionSetupQuestionResource> result = service.update(resource);

        assertEquals(true, result.isSuccess());
        assertFalse(appendixFormInput.getActive());
        assertNull(appendixFormInput.getAllowedFileTypes());
        assertNull(appendixFormInput.getGuidanceAnswer());
    }

    @Test
    public void update_shouldSetAppendixOptionsFormInputWhenSelected() {
        setMocksForSuccessfulUpdate();
        CompetitionSetupQuestionResource resource = createValidQuestionResourceWithoutAppendixOptions();

        resource.setAppendix(true);
        resource.setAllowedFileTypes(asSet(PDF));
        resource.setAppendixGuidance(fileUploadGuidance);

        FormInput appendixFormInput = newFormInput().build();
        //Override repository response set in prerequisites test prep function
        when(formInputRepository.findByQuestionIdAndScopeAndType(
                1L,
                FormInputScope.APPLICATION,
                FormInputType.FILEUPLOAD
        )).thenReturn(appendixFormInput);

        ServiceResult<CompetitionSetupQuestionResource> result = service.update(resource);

        assertEquals(true, result.isSuccess());
        assertTrue(appendixFormInput.getActive());
        assertEquals(asSet(PDF), appendixFormInput.getAllowedFileTypes());
        assertEquals(fileUploadGuidance, appendixFormInput.getGuidanceAnswer());
    }

    @Test
    public void update_shouldAppendFileTypeSeparatedByComma() {
        Long questionId = 1L;

        setMocksForSuccessfulUpdate();
        CompetitionSetupQuestionResource resource = createValidQuestionResourceWithoutAppendixOptions();

        resource.setAppendix(true);
        resource.setAllowedFileTypes(newLinkedHashSet(asSet(PDF, SPREADSHEET)));
        resource.setAppendixGuidance(fileUploadGuidance);

        FormInput appendixFormInput = newFormInput().build();
        //Override repository response set in prerequisites test prep function
        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.APPLICATION, FormInputType.FILEUPLOAD)).thenReturn(appendixFormInput);

        service.update(resource);

        assertTrue(appendixFormInput.getAllowedFileTypes().contains(PDF));
        assertTrue(appendixFormInput.getAllowedFileTypes().contains(SPREADSHEET));
    }

    @Test
    public void update_shouldNotUpdateApplicationDetailsHeading() {
        long questionId = 1L;
        String oldShortTitle = "Application details";

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
                .withType(QuestionSetupType.APPLICATION_DETAILS)
                .withAssessmentGuidance(assessmentGuidanceAnswer)
                .withAssessmentGuidanceTitle(assessmentGuidanceTitle)
                .withAssessmentMaxWords(assessmentMaxWords)
                .withGuidanceRows(guidanceRows)
                .withScored(true)
                .withScoreTotal(scoreTotal)
                .withWrittenFeedback(true)
                .build();

        Question question = newQuestion().
                withShortName(oldShortTitle)
                .withQuestionSetupType(QuestionSetupType.APPLICATION_DETAILS).build();

        FormInput questionFormInput = newFormInput().build();
        FormInput appendixFormInput = newFormInput().build();
        FormInput researchCategoryQuestionFormInput = newFormInput().build();
        FormInput scopeQuestionFormInput = newFormInput().build();
        FormInput scoredQuestionFormInput = newFormInput().build();
        FormInput writtenFeedbackFormInput = newFormInput()
                .withGuidanceRows(newFormInputGuidanceRow().build(2))
                .build();

        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.APPLICATION, FormInputType.TEXTAREA)).thenReturn(questionFormInput);
        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.APPLICATION, FormInputType.FILEUPLOAD)).thenReturn(appendixFormInput);
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.ASSESSMENT, FormInputType.ASSESSOR_RESEARCH_CATEGORY)).thenReturn(researchCategoryQuestionFormInput);
        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.ASSESSMENT, FormInputType.ASSESSOR_APPLICATION_IN_SCOPE)).thenReturn(scopeQuestionFormInput);
        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.ASSESSMENT, FormInputType.ASSESSOR_SCORE)).thenReturn(scoredQuestionFormInput);
        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.ASSESSMENT, FormInputType.TEXTAREA)).thenReturn(writtenFeedbackFormInput);

        doNothing().when(guidanceRowRepository).deleteAll(writtenFeedbackFormInput.getGuidanceRows());
        when(guidanceRowRepository.saveAll(writtenFeedbackFormInput.getGuidanceRows())).thenReturn(writtenFeedbackFormInput.getGuidanceRows());

        ServiceResult<CompetitionSetupQuestionResource> result = service.update(resource);

        assertTrue(result.isSuccess());
        assertNotEquals(question.getQuestionNumber(), number);
        assertEquals(question.getShortName(), oldShortTitle);
    }

    private void setMocksForSuccessfulUpdate() {
        when(guidanceRowMapper.mapToDomain(anyList())).thenReturn(new ArrayList<>());

        Question question = newQuestion().
                withShortName(QuestionSetupType.SCOPE.getShortName()).build();

        FormInput questionFormInput = newFormInput().build();
        FormInput appendixFormInput = newFormInput().build();
        FormInput researchCategoryQuestionFormInput = newFormInput().build();
        FormInput scopeQuestionFormInput = newFormInput().build();
        FormInput scoredQuestionFormInput = newFormInput().build();
        FormInput writtenFeedbackFormInput = newFormInput()
                .withGuidanceRows(newFormInputGuidanceRow().build(2))
                .build();

        when(formInputRepository.findByQuestionIdAndScopeAndType(question.getId(), FormInputScope.APPLICATION, FormInputType
                .TEXTAREA)).thenReturn(questionFormInput);
        when(formInputRepository.findByQuestionIdAndScopeAndType(question.getId(), FormInputScope.APPLICATION, FormInputType.FILEUPLOAD)).thenReturn(appendixFormInput);
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        when(formInputRepository.findByQuestionIdAndScopeAndType(question.getId(), FormInputScope.ASSESSMENT, FormInputType.ASSESSOR_RESEARCH_CATEGORY)).thenReturn(researchCategoryQuestionFormInput);
        when(formInputRepository.findByQuestionIdAndScopeAndType(question.getId(), FormInputScope.ASSESSMENT, FormInputType.ASSESSOR_APPLICATION_IN_SCOPE)).thenReturn(scopeQuestionFormInput);
        when(formInputRepository.findByQuestionIdAndScopeAndType(question.getId(), FormInputScope.ASSESSMENT, FormInputType.ASSESSOR_SCORE)).thenReturn(scoredQuestionFormInput);
        when(formInputRepository.findByQuestionIdAndScopeAndType(question.getId(), FormInputScope.ASSESSMENT, FormInputType.TEXTAREA)).thenReturn(writtenFeedbackFormInput);

        doNothing().when(guidanceRowRepository).deleteAll(writtenFeedbackFormInput.getGuidanceRows());
        when(guidanceRowRepository.saveAll(writtenFeedbackFormInput.getGuidanceRows())).thenReturn(writtenFeedbackFormInput.getGuidanceRows());
    }

    @Test
    public void delete() {
        long questionId = 1L;

        when(questionSetupTemplateService.deleteQuestionInCompetition(questionId)).thenReturn(serviceSuccess());
        assertTrue(service.delete(questionId).isSuccess());
    }

    @Test
    public void createByCompetitionId() {
        Competition competition = newCompetition().build();
        Question newlyCreatedQuestion = newQuestion().build();
        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(questionSetupTemplateService.addDefaultAssessedQuestionToCompetition(competition)).thenReturn(serviceSuccess(newlyCreatedQuestion));
        when(questionRepository.findById(newlyCreatedQuestion.getId())).thenReturn(Optional.of(newlyCreatedQuestion));

        ServiceResult<CompetitionSetupQuestionResource> result = service.createByCompetitionId(competition.getId());
        assertTrue(result.isSuccess());

        CompetitionSetupQuestionResource resource = result.getSuccess();
        assertEquals(newlyCreatedQuestion.getId(), resource.getQuestionId());
    }

    @Test
    public void createByCompetitionId_withNonExistentCompId() {
        Long competitionId = 22L;
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(Optional.empty());

        ServiceResult<CompetitionSetupQuestionResource> result = service.createByCompetitionId(competitionId);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonErrors.notFoundError(Competition.class, competitionId)));
    }

    @Test
    public void createByCompetitionId_whenDefaultCreationFails() {
        Competition competition = newCompetition().build();
        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(questionSetupTemplateService.addDefaultAssessedQuestionToCompetition(competition)).thenReturn(serviceFailure(COMPETITION_NOT_EDITABLE));

        ServiceResult<CompetitionSetupQuestionResource> result = service.createByCompetitionId(competition.getId());
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(COMPETITION_NOT_EDITABLE));
    }

    @Test
    public void addResearchCategoryQuestionToCompetition() {
        Competition competition = newCompetition().build();
        Section section = newSection().build();
        Question createdQuestion = newQuestion().build();

        when(competitionRepositoryMock.findById(competition.getId())).thenReturn(Optional.of(competition));
        when(sectionRepository.findFirstByCompetitionIdAndName(competition.getId(), PROJECT_DETAILS.getName()))
                .thenReturn(section);
        when(questionRepository.save(createResearchCategoryQuestionExpectations(competition, section)))
                .thenReturn(createdQuestion);

        ServiceResult<Void> result = service.addResearchCategoryQuestionToCompetition(competition.getId());

        assertTrue(result.isSuccess());

        verify(competitionRepositoryMock).findById(competition.getId());
        verify(sectionRepository).findFirstByCompetitionIdAndName(competition.getId(), PROJECT_DETAILS.getName());
        verify(questionRepository).save(createResearchCategoryQuestionExpectations(competition, section));
        verify(questionPriorityOrderService).prioritiseResearchCategoryQuestionAfterCreation(createdQuestion);
    }

    private CompetitionSetupQuestionResource createValidQuestionResourceWithoutAppendixOptions() {
        return newCompetitionSetupQuestionResource()
                .withAppendix(false)
                .withGuidance(guidance)
                .withGuidanceTitle(guidanceTitle)
                .withMaxWords(maxWords)
                .withNumber(number)
                .withTitle(title)
                .withShortTitle(newShortTitle)
                .withSubTitle(subTitle)
                .withQuestionId(1L)
                .withAssessmentGuidance(assessmentGuidanceAnswer)
                .withAssessmentGuidanceTitle(assessmentGuidanceTitle)
                .withAssessmentMaxWords(assessmentMaxWords)
                .withGuidanceRows(newFormInputGuidanceRowResourceBuilder().build(1))
                .withScored(true)
                .withScoreTotal(scoreTotal)
                .withWrittenFeedback(true)
                .build();
    }

    private Question createResearchCategoryQuestionExpectations(Competition competition, Section section) {
        return createLambdaMatcher(question -> {
            assertNull(question.getId());
            assertFalse(question.getAssignEnabled());
            assertEquals("Description not used", question.getDescription());
            assertTrue(question.getMarkAsCompletedEnabled());
            assertEquals("Research category", question.getName());
            assertEquals("Research category", question.getShortName());
            assertEquals(competition, question.getCompetition());
            assertEquals(section, question.getSection());
            assertEquals(LEAD_ONLY, question.getType());
            assertEquals(RESEARCH_CATEGORY, question.getQuestionSetupType());
        });
    }
}
