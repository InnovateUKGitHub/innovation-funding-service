package org.innovateuk.ifs.competitionsetup.transactional.template;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.validator.NotEmptyValidator;
import org.innovateuk.ifs.application.validator.RequiredMultipleChoiceValidator;
import org.innovateuk.ifs.application.validator.WordCountValidator;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormValidator;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.repository.FormValidatorRepository;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.question.transactional.template.DefaultApplicationQuestionCreator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.form.builder.FormValidatorBuilder.newFormValidator;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class DefaultApplicationQuestionCreatorTest extends BaseServiceUnitTest<DefaultApplicationQuestionCreator> {
    private static final Integer ASSESSOR_MAXIMUM_DEFAULT = 10;

    private Competition competition;

    private FormValidator notEmptyValidator;
    private FormValidator wordCountValidator;
    private FormValidator multipleChoiceValidator;

    @Mock
    private FormValidatorRepository formValidatorRepositoryMock;

    @Before
    public void setup() {
        competition = newCompetition().build();

        notEmptyValidator = newFormValidator().withClazzName(NotEmptyValidator.class.getName()).build();
        wordCountValidator = newFormValidator().withClazzName(WordCountValidator.class.getName()).build();
        multipleChoiceValidator = newFormValidator().withClazzName(RequiredMultipleChoiceValidator.class.getName()).build();

        when(formValidatorRepositoryMock.findByClazzName(NotEmptyValidator.class.getName()))
                .thenReturn(notEmptyValidator);
        when(formValidatorRepositoryMock.findByClazzName(WordCountValidator.class.getName()))
                .thenReturn(wordCountValidator);
        when(formValidatorRepositoryMock.findByClazzName(RequiredMultipleChoiceValidator.class.getName()))
                .thenReturn(multipleChoiceValidator);
    }

    public DefaultApplicationQuestionCreator supplyServiceUnderTest() {
        return new DefaultApplicationQuestionCreator();
    }

    @Test
    public void buildQuestion_markCompleteAsEnabledSetToTrue() {
        Question defaultQuestion = service.buildQuestion(competition);

        assertThat(defaultQuestion.getMarkAsCompletedEnabled()).isTrue();
    }

    @Test
    public void buildQuestion_createdQuestionShouldUseValidatorsFromRepository() throws Exception {
        Question defaultQuestion = service.buildQuestion(competition);

        FormInput maxWordCountInput = defaultQuestion.getFormInputs().get(0);
        FormInput questionScoreInput = defaultQuestion.getFormInputs().get(0);
        FormInput feedbackInput = defaultQuestion.getFormInputs().get(0);

        assertTrue(maxWordCountInput.getFormValidators().contains(notEmptyValidator));
        assertTrue(maxWordCountInput.getFormValidators().contains(wordCountValidator));

        assertTrue(questionScoreInput.getFormValidators().contains(notEmptyValidator));

        assertTrue(feedbackInput.getFormValidators().contains(notEmptyValidator));
        assertTrue(feedbackInput.getFormValidators().contains(wordCountValidator));
    }

    @Test
    public void buildQuestion_createQuestionShouldContainTheCorrectNumberOfChildrenEntities() throws Exception {
        Question defaultQuestion = service.buildQuestion(competition);
        FormInput feedbackInput = defaultQuestion.getFormInputs().get(3);

        assertEquals(defaultQuestion.getFormInputs().size(), 6);
        assertEquals(feedbackInput.getGuidanceRows().size(), 5);
    }

    @Test
    public void buildQuestion_createQuestionShouldBeInitializedWithCompetition() throws Exception {
        Question defaultQuestion = service.buildQuestion(competition);

        assertEquals(defaultQuestion.getCompetition(), competition);
        assertEquals(defaultQuestion.getAssessorMaximumScore(), ASSESSOR_MAXIMUM_DEFAULT);
    }

    @Test
    public void buildQuestion_createQuestionShouldNotContainTheDefaultAllowedFileTypeAndGuidanceForFileUpload() throws Exception {
        Question defaultQuestion = service.buildQuestion(competition);
        FormInput fileUploadFormInput = defaultQuestion.getFormInputs().get(4);
        FormInput templateDoc = defaultQuestion.getFormInputs().get(5);
        FormInput multipleChoice = defaultQuestion.getFormInputs().get(1);

        assertEquals(defaultQuestion.getFormInputs().size(), 6);
        assertEquals(fileUploadFormInput.getAllowedFileTypes(), emptySet());
        assertEquals(templateDoc.getAllowedFileTypes(), emptySet());
        assertEquals(FormInputType.FILEUPLOAD, fileUploadFormInput.getType());
        assertEquals(FormInputType.TEMPLATE_DOCUMENT, templateDoc.getType());
        assertEquals(FormInputType.MULTIPLE_CHOICE, multipleChoice.getType());
        assertNull(fileUploadFormInput.getGuidanceAnswer());
    }
}