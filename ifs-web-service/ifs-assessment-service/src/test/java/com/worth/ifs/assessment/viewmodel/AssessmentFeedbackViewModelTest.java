package com.worth.ifs.assessment.viewmodel;

import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.form.resource.FormInputResource;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.assessment.builder.AssessorFormInputResponseResourceBuilder.newAssessorFormInputResponseResource;
import static com.worth.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static com.worth.ifs.util.CollectionFunctions.asListOfPairs;
import static com.worth.ifs.util.CollectionFunctions.simpleToMap;
import static com.worth.ifs.util.MapFunctions.asMap;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class AssessmentFeedbackViewModelTest {

    @Test
    public void testGetAppendixFileDescription() throws Exception {
        String questionShortName = "Technical approach";

        AssessmentFeedbackViewModel assessmentFeedbackViewModel = new AssessmentFeedbackViewModel(0L, 0L, null, null, null, null, questionShortName, null, null, null, null, null, false, false);

        assertEquals("View technical approach appendix", assessmentFeedbackViewModel.getAppendixFileDescription());
    }

    @Test
    public void testGetWordsRemaining() throws Exception {
        Long formInputId = 1L;
        Integer maxWordCount = 100;

        AssessmentFeedbackViewModel assessmentFeedbackViewModel = setupViewModelWithFormInputsAndResponses(maxWordCount, asListOfPairs(formInputId, "This value is made up of eight words."));

        assertEquals(Integer.valueOf(92), assessmentFeedbackViewModel.getWordsRemaining(formInputId, false, ""));
    }

    @Test
    public void testGetWordsRemaining_valueWithHtml() throws Exception {
        Long formInputId = 1L;
        Integer maxWordCount = 100;

        AssessmentFeedbackViewModel assessmentFeedbackViewModel = setupViewModelWithFormInputsAndResponses(maxWordCount, asListOfPairs(formInputId, "<td><p style=\"font-variant: small-caps\">This value is made up of fifteen words even though it is wrapped within HTML.</p></td>"));

        assertEquals(Integer.valueOf(85), assessmentFeedbackViewModel.getWordsRemaining(formInputId, false, ""));
    }

    @Test
    public void testGetWordsRemaining_noMaxWords() throws Exception {
        Long formInputId = 1L;
        Integer maxWordCount = null;

        AssessmentFeedbackViewModel assessmentFeedbackViewModel = setupViewModelWithFormInputsAndResponses(maxWordCount, asListOfPairs(formInputId, "No word limit imposed here."));

        // Peeking into the behaviour of com.worth.ifs.form.resource.FormInputResource.getWordCount() reveals it treats no maximum word count as 0
        assertEquals(Integer.valueOf(-5), assessmentFeedbackViewModel.getWordsRemaining(formInputId, false, ""));
    }

    @Test
    public void testGetWordsRemaining_valueLengthExceedingMaxWords() throws Exception {
        Long formInputId = 1L;
        Integer maxWordCount = 5;

        AssessmentFeedbackViewModel assessmentFeedbackViewModel = setupViewModelWithFormInputsAndResponses(maxWordCount, asListOfPairs(formInputId, "Value of ten words here, exceeding the max word count."));

        assertEquals(Integer.valueOf(-5), assessmentFeedbackViewModel.getWordsRemaining(formInputId, false, ""));
    }

    @Test
    public void testGetWordsRemaining_noFormInput() throws Exception {
        Long formInputId = 1L;
        Long otherFormInputId = 2L;
        Integer maxWordCount = 100;

        AssessmentFeedbackViewModel assessmentFeedbackViewModel = setupViewModelWithFormInputsAndResponses(maxWordCount, asListOfPairs(formInputId, "Not the form input under test."));

        assertNull(assessmentFeedbackViewModel.getWordsRemaining(otherFormInputId, false, ""));
    }


    @Test
    public void testGetWordsRemaining_noResponse() throws Exception {
        Long formInputId = 1L;
        Long otherFormInputId = 2L;

        List<FormInputResource> assessmentFormInputs = newFormInputResource()
                .with(id(formInputId))
                .withWordCount(100)
                .build(1);

        Map<Long, AssessorFormInputResponseResource> assessorResponses = asMap(otherFormInputId, newAssessorFormInputResponseResource()
                .withFormInput(otherFormInputId)
                .withValue("Not the form input under test.")
                .build()
        );

        AssessmentFeedbackViewModel assessmentFeedbackViewModel = new AssessmentFeedbackViewModel(0L, 0L, null, null, null, null, null, null, null, null, assessmentFormInputs, assessorResponses, false, false);

        assertNull(assessmentFeedbackViewModel.getWordsRemaining(formInputId, false, ""));
    }

    @Test
    public void testGetWordsRemaining_noResponseValue() throws Exception {
        Long formInputId = 1L;
        Integer maxWordCount = 100;

        AssessmentFeedbackViewModel assessmentFeedbackViewModel = setupViewModelWithFormInputsAndResponses(maxWordCount, asListOfPairs(formInputId, null));

        assertEquals(Integer.valueOf(100), assessmentFeedbackViewModel.getWordsRemaining(formInputId, false, ""));
    }

    @Test
    public void testGetWordsRemaining_withInvalidContent() throws Exception {
        String[] feedbackArray = new String[120];
        Arrays.fill(feedbackArray, "feedback ");
        String feedback = Arrays.toString(feedbackArray);
        Long formInputId = 1L;
        Integer maxWordCount = 100;

        AssessmentFeedbackViewModel assessmentFeedbackViewModel = setupViewModelWithFormInputsAndResponses(maxWordCount, asListOfPairs(formInputId, null));

        assertEquals(Integer.valueOf(-20), assessmentFeedbackViewModel.getWordsRemaining(formInputId, true, feedback.substring(1, feedback.length()-1).replaceAll(",", "")));
    }

    private AssessmentFeedbackViewModel setupViewModelWithFormInputsAndResponses(Integer maxWordCount, List<Pair<Long, String>> idAndValuePairs) {
        List<FormInputResource> assessmentFormInputs = idAndValuePairs.stream().map(idAndValuePair -> newFormInputResource()
                .with(id(idAndValuePair.getLeft()))
                .withWordCount(maxWordCount)
                .build()
        ).collect(toList());

        Map<Long, AssessorFormInputResponseResource> assessorResponses = simpleToMap(idAndValuePairs, Pair::getLeft, idAndValuePair -> newAssessorFormInputResponseResource()
                .withFormInput(idAndValuePair.getLeft())
                .withValue(idAndValuePair.getRight())
                .build()
        );

        return new AssessmentFeedbackViewModel(0L, 0L, null, null, null, null, null, null, null, null, assessmentFormInputs, assessorResponses, false, false);
    }
}