package org.innovateuk.ifs.application.forms.questions.generic.form;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GenericQuestionApplicationFormTest {

    GenericQuestionApplicationForm genericQuestionApplicationForm;

    @Before
    public void setup() {
        genericQuestionApplicationForm = new GenericQuestionApplicationForm();
    }

    @Test
    public void getAnswerForTextArea() {
        genericQuestionApplicationForm.setAnswer(null);
        genericQuestionApplicationForm.setTextAreaActive(true);
        genericQuestionApplicationForm.setMultipleChoiceOptionsActive(false);

        assertNull(genericQuestionApplicationForm.getAnswer());
    }

    @Test
    public void getAnswerForMultipleChoiceOptions() {
        genericQuestionApplicationForm.setAnswer(null);
        genericQuestionApplicationForm.setTextAreaActive(false);
        genericQuestionApplicationForm.setMultipleChoiceOptionsActive(true);

        assertEquals("" , genericQuestionApplicationForm.getAnswer());
    }
}
