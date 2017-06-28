package org.innovateuk.ifs.validator;

import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import static org.innovateuk.ifs.application.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.validator.ValidatorTestUtil.getBindingResult;
import static org.junit.Assert.*;

public class AssessorScoreValidatorTest {

    private Validator validator;

    private FormInputResponse formInputResponse;
    private BindingResult bindingResult;

    @Before
    public void setUp() {
        validator = new AssessorScoreValidator();

        formInputResponse = newFormInputResponse()
                .withFormInputs(newFormInput()
                    .withType(FormInputType.ASSESSOR_SCORE)
                    .withQuestion(newQuestion()
                        .withAssessorMaximumScore(10)
                        .build())
                    .build())
                .build();
        bindingResult = getBindingResult(formInputResponse);
    }

    @Test
    public void validate() {
        formInputResponse.setValue("7");
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }

    @Test
    public void validate_decimal() {
        formInputResponse.setValue("1.1");
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(1, bindingResult.getAllErrors().size());
        assertEquals("validation.assessor.score.notAnInteger", bindingResult.getAllErrors().get(0).getDefaultMessage());
    }

    @Test
    public void validate_negative() {
        formInputResponse.setValue("-1");
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(1, bindingResult.getAllErrors().size());
        assertEquals("validation.assessor.score.betweenZeroAndMax", bindingResult.getAllErrors().get(0).getDefaultMessage());
    }

    @Test
    public void validate_greaterThanMax() {
        formInputResponse.setValue("11");
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(1, bindingResult.getAllErrors().size());
        assertEquals("validation.assessor.score.betweenZeroAndMax", bindingResult.getAllErrors().get(0).getDefaultMessage());
    }

    @Test
    public void validate_greaterThanIntMax() {
        formInputResponse.setValue("999999999999");
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(1, bindingResult.getAllErrors().size());
        assertEquals("validation.assessor.score.notAnInteger", bindingResult.getAllErrors().get(0).getDefaultMessage());
    }

    @Test
    public void validate_null() {
        formInputResponse.setValue(null);
        validator.validate(formInputResponse, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(1, bindingResult.getAllErrors().size());
        assertEquals("validation.assessor.score.notAnInteger", bindingResult.getAllErrors().get(0).getDefaultMessage());
    }

    @Test
    public void validate_wrongQuestionType() {
        formInputResponse.getFormInput().setType(FormInputType.ASSESSOR_RESEARCH_CATEGORY);
        formInputResponse.setValue("2");
        validator.validate(formInputResponse, bindingResult);
        assertFalse(bindingResult.hasErrors());
    }
}
