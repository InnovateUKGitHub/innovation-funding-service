package com.worth.ifs.form.builder;

import com.worth.ifs.application.domain.GuidanceRow;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.domain.FormValidator;
import com.worth.ifs.form.resource.FormInputScope;
import com.worth.ifs.form.resource.FormInputType;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static com.worth.ifs.application.builder.GuidanceRowBuilder.newFormInputGuidanceRow;
import static com.worth.ifs.application.builder.QuestionBuilder.newQuestion;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.form.builder.FormInputBuilder.newFormInput;
import static com.worth.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static com.worth.ifs.form.builder.FormValidatorBuilder.newFormValidator;
import static com.worth.ifs.form.resource.FormInputScope.APPLICATION;
import static com.worth.ifs.form.resource.FormInputScope.ASSESSMENT;
import static com.worth.ifs.form.resource.FormInputType.APPLICATION_DETAILS;
import static com.worth.ifs.form.resource.FormInputType.FILEUPLOAD;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * Testing that the FormInput builder performs as expected.
 */
public class FormInputBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        Integer expectedWordCount = 100;
        FormInputType expectedType = APPLICATION_DETAILS;
        List<FormInputResponse> expectedResponses = newFormInputResponse().build(2);
        Question expectedQuestion = newQuestion().build();
        Competition expectedCompetition = newCompetition().build();
        Set<FormValidator> expectedInputValidators = newFormValidator().buildSet(2);
        String expectedGuidanceQuestion = "question";
        String expectedGuidanceAnswer = "answer";
        String expectedDescription = "description";
        Boolean expectedIncludedInApplicationSummary = FALSE;
        Integer expectedPriority = 5;
        FormInputScope expectedScope = APPLICATION;
        List<GuidanceRow> expectedGuidanceRows = newFormInputGuidanceRow().build(2);
        Boolean expectedActive = FALSE;

        FormInput formInput = newFormInput()
                .withId(expectedId)
                .withWordCount(expectedWordCount)
                .withType(expectedType)
                .withResponses(expectedResponses)
                .withQuestion(expectedQuestion)
                .withCompetition(expectedCompetition)
                .withInputValidators(expectedInputValidators)
                .withGuidanceQuestion(expectedGuidanceQuestion)
                .withGuidanceAnswer(expectedGuidanceAnswer)
                .withDescription(expectedDescription)
                .withIncludedInApplicationSummary(expectedIncludedInApplicationSummary)
                .withPriority(expectedPriority)
                .withScope(expectedScope)
                .withGuidanceRows(expectedGuidanceRows)
                .withActive(expectedActive)
                .build();

        assertEquals(expectedId, formInput.getId());
        assertEquals(expectedWordCount, formInput.getWordCount());
        assertEquals(expectedType, formInput.getType());
        assertEquals(expectedResponses, formInput.getResponses());
        assertEquals(expectedQuestion, formInput.getQuestion());
        assertEquals(expectedCompetition, formInput.getCompetition());
        assertEquals(expectedInputValidators, formInput.getInputValidators());
        assertEquals(expectedGuidanceQuestion, formInput.getGuidanceQuestion());
        assertEquals(expectedGuidanceAnswer, formInput.getGuidanceAnswer());
        assertEquals(expectedDescription, formInput.getDescription());
        assertEquals(expectedIncludedInApplicationSummary, formInput.getIncludedInApplicationSummary());
        assertEquals(expectedPriority, formInput.getPriority());
        assertEquals(expectedScope, formInput.getScope());
        assertEquals(expectedGuidanceRows, formInput.getGuidanceRows());
        assertEquals(expectedActive, formInput.getActive());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        Integer[] expectedWordCounts = {100, 150};
        FormInputType[] expectedTypes = {APPLICATION_DETAILS, FILEUPLOAD};
        List<List<FormInputResponse>> expectedResponses = asList(newFormInputResponse().build(2), newFormInputResponse().build(2));
        Question[] expectedQuestions = newQuestion().buildArray(2, Question.class);
        Competition[] expectedCompetitions = newCompetition().buildArray(2, Competition.class);
        List<Set<FormValidator>> expectedInputValidators = asList(newFormValidator().buildSet(2), newFormValidator().buildSet(2));
        String[] expectedGuidanceQuestion = {"question 1", "question 2"};
        String[] expectedGuidanceAnswer = {"answer 1", "answer 2"};
        String[] expectedDescription = {"description 1", "description 2"};
        Boolean[] expectedIncludedInApplicationSummary = {FALSE, TRUE};
        Integer[] expectedPriority = {5, 6};
        FormInputScope[] expectedScopes = {APPLICATION, ASSESSMENT};
        List<List<GuidanceRow>> expectedGuidanceRows = asList(newFormInputGuidanceRow().build(2), newFormInputGuidanceRow().build(2));
        Boolean[] expectedActives = {FALSE, TRUE};

        List<FormInput> formInputs = newFormInput()
                .withId(expectedIds)
                .withWordCount(expectedWordCounts)
                .withType(expectedTypes)
                .withResponses(expectedResponses.get(0), expectedResponses.get(1))
                .withQuestion(expectedQuestions)
                .withCompetition(expectedCompetitions)
                .withInputValidators(expectedInputValidators.get(0), expectedInputValidators.get(1))
                .withGuidanceQuestion(expectedGuidanceQuestion)
                .withGuidanceAnswer(expectedGuidanceAnswer)
                .withDescription(expectedDescription)
                .withIncludedInApplicationSummary(expectedIncludedInApplicationSummary)
                .withPriority(expectedPriority)
                .withScope(expectedScopes)
                .withGuidanceRows(expectedGuidanceRows.get(0), expectedGuidanceRows.get(1))
                .withActive(expectedActives)
                .build(2);

        FormInput first = formInputs.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedWordCounts[0], first.getWordCount());
        assertEquals(expectedTypes[0], first.getType());
        assertEquals(expectedResponses.get(0), first.getResponses());
        assertEquals(expectedQuestions[0], first.getQuestion());
        assertEquals(expectedCompetitions[0], first.getCompetition());
        assertEquals(expectedInputValidators.get(0), first.getInputValidators());
        assertEquals(expectedGuidanceQuestion[0], first.getGuidanceQuestion());
        assertEquals(expectedGuidanceAnswer[0], first.getGuidanceAnswer());
        assertEquals(expectedDescription[0], first.getDescription());
        assertEquals(expectedIncludedInApplicationSummary[0], first.getIncludedInApplicationSummary());
        assertEquals(expectedPriority[0], first.getPriority());
        assertEquals(expectedScopes[0], first.getScope());
        assertEquals(expectedGuidanceRows.get(0), first.getGuidanceRows());
        assertEquals(expectedActives[0], first.getActive());

        FormInput second = formInputs.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedWordCounts[1], second.getWordCount());
        assertEquals(expectedTypes[1], second.getType());
        assertEquals(expectedResponses.get(1), second.getResponses());
        assertEquals(expectedQuestions[1], second.getQuestion());
        assertEquals(expectedCompetitions[1], second.getCompetition());
        assertEquals(expectedInputValidators.get(1), second.getInputValidators());
        assertEquals(expectedGuidanceQuestion[1], second.getGuidanceQuestion());
        assertEquals(expectedGuidanceAnswer[1], second.getGuidanceAnswer());
        assertEquals(expectedDescription[1], second.getDescription());
        assertEquals(expectedIncludedInApplicationSummary[1], second.getIncludedInApplicationSummary());
        assertEquals(expectedPriority[1], second.getPriority());
        assertEquals(expectedScopes[1], second.getScope());
        assertEquals(expectedGuidanceRows.get(1), second.getGuidanceRows());
        assertEquals(expectedActives[1], second.getActive());
    }
}
