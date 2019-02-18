package org.innovateuk.ifs.form.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.file.resource.FileTypeCategory;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormValidator;
import org.innovateuk.ifs.form.domain.GuidanceRow;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.PDF;
import static org.innovateuk.ifs.file.resource.FileTypeCategory.SPREADSHEET;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.FormValidatorBuilder.newFormValidator;
import static org.innovateuk.ifs.form.builder.GuidanceRowBuilder.newFormInputGuidanceRow;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.form.resource.FormInputType.FILEUPLOAD;
import static org.junit.Assert.*;
import static org.springframework.test.util.ReflectionTestUtils.getField;

/**
 * Repository Integration tests for Form Inputs.
 */
public class FormInputRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<FormInputRepository> {

    private static final Long INACTIVE_FORM_INPUT_ID = 200L;

    @Autowired
    private FormInputRepository repository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Override
    @Autowired
    protected void setRepository(FormInputRepository repository) {
        this.repository = repository;
    }


    @Test
    public void test_findOne() {

        FormInput input = repository.findById(1L).get();
        assertEquals(Long.valueOf(1), input.getId());
        assertEquals(Integer.valueOf(400), input.getWordCount());
        assertEquals(FormInputType.TEXTAREA, input.getType());
        assertEquals(Long.valueOf(1L), ((Competition) getField(input, "competition")).getId());
        assertTrue(input.isIncludedInApplicationSummary());
        assertEquals("1. What is the business opportunity that your project addresses?", input.getDescription());
    }

    @Test
    public void test_findOne_nonExistentInput() {
        assertFalse(repository.findById(Long.MAX_VALUE).isPresent());
    }

    @Test
    public void test_findByCompetitionId() {
        List<FormInput> competitionInputs = repository.findByCompetitionIdAndActiveTrueOrderByPriorityAsc(1L);
        assertEquals(61, competitionInputs.size());

        FormInput first = competitionInputs.get(0);
        assertEquals(Long.valueOf(1), first.getId());

        FormInput last = competitionInputs.get(competitionInputs.size() - 1);
        assertEquals(Long.valueOf(186), last.getId());

        //Assert that inactive form input is not returned.
        FormInput inactive = repository.findById(INACTIVE_FORM_INPUT_ID).get();
        assertThat(inactive, notNullValue());
        assertThat(competitionInputs.contains(inactive), is(false));
        //Assert that inactive form input is linked to the competition
        assertThat(inactive.getCompetition().getId(), is(equalTo(1L)));
    }

    @Test
    public void test_findByCompetitionId_nonExistentCompetition() {
        List<FormInput> competitionInputs = repository.findByCompetitionIdAndActiveTrueOrderByPriorityAsc(999L);
        assertEquals(0, competitionInputs.size());
    }

    @Test
    public void test_findByCompetitionIdAndScope() {
        List<FormInput> competitionInputs = repository.findByCompetitionIdAndScopeAndActiveTrueOrderByPriorityAsc(1L, APPLICATION);
        assertEquals(38, competitionInputs.size());

        FormInput first = competitionInputs.get(0);
        assertEquals(Long.valueOf(1), first.getId());

        FormInput last = competitionInputs.get(competitionInputs.size() - 1);
        assertEquals(Long.valueOf(18), last.getId());

        //Assert that inactive form input is not returned.
        FormInput inactive = repository.findById(INACTIVE_FORM_INPUT_ID).get();
        assertThat(inactive, notNullValue());
        assertThat(competitionInputs.contains(inactive), is(false));
        //Assert that inactive form input is linked to the competition and has correct scope.
        assertThat(inactive.getCompetition().getId(), is(equalTo(1L)));
        assertThat(inactive.getScope(), is(equalTo(APPLICATION)));
    }

    @Test
    public void test_findByCompetitionIdAndScope_nonExistingCompetition() {
        List<FormInput> competitionInputs = repository.findByCompetitionIdAndScopeAndActiveTrueOrderByPriorityAsc(999L, APPLICATION);
        assertTrue(competitionInputs.isEmpty());
    }

    @Test
    public void test_findByQuestionId() {
        List<FormInput> questionInputs = repository.findByQuestionIdAndActiveTrueOrderByPriorityAsc(1L);
        assertThat(questionInputs, hasSize(3));
        assertEquals(Long.valueOf(1), questionInputs.get(0).getId());
        assertEquals(Long.valueOf(168), questionInputs.get(1).getId());
        assertEquals(Long.valueOf(169), questionInputs.get(2).getId());

        //Assert that inactive form input is not returned.
        FormInput inactive = repository.findById(INACTIVE_FORM_INPUT_ID).get();
        assertThat(inactive, notNullValue());
        assertThat(questionInputs.contains(inactive), is(false));
        //Assert it is actually linked to the question we are querying for.
        assertThat(inactive.getQuestion().getId(), is(equalTo(1L)));
    }

    @Test
    public void test_findByQuestionId_nonExistingQuestion() {
        List<FormInput> formInputs = repository.findByQuestionIdAndActiveTrueOrderByPriorityAsc(999L);
        assertTrue(formInputs.isEmpty());
    }

    @Test
    public void test_findByQuestionIdAndScope() {
        List<FormInput> questionInputs = repository.findByQuestionIdAndScopeAndActiveTrueOrderByPriorityAsc(1L, APPLICATION);
        FormInput first = questionInputs.get(0);
        assertEquals(Long.valueOf(1), first.getId());

        //Assert that inactive form input is not returned.
        FormInput inactive = repository.findById(INACTIVE_FORM_INPUT_ID).get();
        assertThat(inactive, notNullValue());
        assertThat(questionInputs.contains(inactive), is(false));
        //Assert it is actually linked to the question and has correct scope.
        assertThat(inactive.getQuestion().getId(), is(equalTo(1L)));
        assertThat(inactive.getScope(), is(equalTo(APPLICATION)));
    }

    @Test
    public void test_findByQuestionIdAndScope_nonExistingQuestion() {
        List<FormInput> questionInputs = repository.findByQuestionIdAndScopeAndActiveTrueOrderByPriorityAsc(999L, APPLICATION);
        assertTrue(questionInputs.isEmpty());
    }

    @Test
    public void test_save() {
        Integer wordCount = 100;
        FormInputType formInputType = FILEUPLOAD;
        Question question = newQuestion().withId(1L).build();
        Competition competition = newCompetition().withId(1L).build();
        Set<FormValidator> inputValidators = asSet(
                newFormValidator().build(),
                newFormValidator().build()
        );
        String guidanceTitle = "Guidance title";
        String guidanceAnswer = "Guidance answer";
        String description = "Description";
        Boolean includedInApplicationSummary = false;
        Integer priority = 1;
        List<GuidanceRow> guidanceRows = newFormInputGuidanceRow().build(2);
        Boolean isActive = true;
        Set<FileTypeCategory> allowedFileTypes = asSet(PDF, SPREADSHEET);

        FormInput formInput = newFormInput()
                .withWordCount(wordCount)
                .withType(formInputType)
                .withQuestion(question)
                .withCompetition(competition)
                .withInputValidators(inputValidators)
                .withGuidanceTitle(guidanceTitle)
                .withGuidanceAnswer(guidanceAnswer)
                .withDescription(description)
                .withIncludedInApplicationSummary(includedInApplicationSummary)
                .withPriority(priority)
                .withGuidanceRows(guidanceRows)
                .withActive(isActive)
                .withAllowedFileTypes(allowedFileTypes)
                .build();

        repository.save(formInput);

        FormInput savedFormInput = repository.findById(formInput.getId()).get();

        assertEquals(savedFormInput.getWordCount(), wordCount);
        assertEquals(savedFormInput.getType(), formInputType);
        assertEquals(savedFormInput.getQuestion().getId(), question.getId());
        assertEquals(savedFormInput.getCompetition().getId(), competition.getId());
        assertEquals(savedFormInput.getInputValidators().size(), inputValidators.size());
        assertEquals(savedFormInput.getGuidanceTitle(), guidanceTitle);
        assertEquals(savedFormInput.getGuidanceAnswer(), guidanceAnswer);
        assertEquals(savedFormInput.getDescription(), description);
        assertEquals(savedFormInput.isIncludedInApplicationSummary(), includedInApplicationSummary);
        assertEquals(savedFormInput.getPriority(), priority);
        assertEquals(savedFormInput.getGuidanceRows().size(), guidanceRows.size());
        assertEquals(savedFormInput.getActive(), isActive);
        assertEquals(savedFormInput.getAllowedFileTypes(), allowedFileTypes);
    }
}
