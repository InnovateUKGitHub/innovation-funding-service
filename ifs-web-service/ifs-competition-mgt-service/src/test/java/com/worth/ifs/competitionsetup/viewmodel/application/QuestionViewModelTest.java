package com.worth.ifs.competitionsetup.viewmodel.application;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.form.resource.FormInputResource;
import org.junit.Test;

import static com.worth.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static com.worth.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.junit.Assert.assertEquals;

public class QuestionViewModelTest {

    @Test
    public void testQuestionViewModel() throws Exception {
        QuestionResource questionResource = newQuestionResource().withShortName("Section").build();
        FormInputResource formInputResource = newFormInputResource()
                .with(formInputResource1 -> {
                    formInputResource1.setGuidanceQuestion("Please fill this");
                    formInputResource1.setGuidanceAnswer("Otherwise this is breaking us apart");
                })
                .withQuestion(questionResource.getId()).withWordCount(0).build();
        Boolean appendix = Boolean.TRUE;
        Boolean scored = Boolean.FALSE;
        QuestionViewModel questionViewModel = new QuestionViewModel(questionResource, formInputResource, appendix, scored);

        assertEquals(Integer.valueOf(400), questionViewModel.getMaxWords());
        assertEquals(questionResource.getShortName(), questionViewModel.getShortTitle());
        assertEquals(formInputResource.getGuidanceQuestion(), questionViewModel.getGuidanceTitle());
        assertEquals(formInputResource.getGuidanceAnswer(), questionViewModel.getGuidance());
        assertEquals(appendix, questionViewModel.getAppendix());
        assertEquals(scored, questionViewModel.getScored());
    }
}