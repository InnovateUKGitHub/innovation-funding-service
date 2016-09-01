package com.worth.ifs.controller;

import com.worth.ifs.commons.error.Error;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import static com.worth.ifs.commons.error.CommonFailureKeys.BANK_DETAILS_CANNOT_BE_SUBMITTED_BEFORE_PROJECT_DETAILS;
import static com.worth.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE;
import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.*;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class ValidationHandlerTest {

    @Test
    public void testNewValidationHandler() {

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new TestForm(), "targetName");
        ValidationHandler validationHandler = ValidationHandler.newBindingResultHandler(bindingResult);
        assertFalse(validationHandler.hasErrors());
        assertTrue(validationHandler.getAllErrors().isEmpty());
    }

    @Test
    public void testNewValidationHandlerWrappingBindingResultsWithExistingErrors() {

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new TestForm(), "targetName");
        bindingResult.addError(new ObjectError("form", "A global error message"));
        bindingResult.rejectValue("formField", "Code1", "A field error 1");
        bindingResult.rejectValue("formField", "Code2", "A field error 2");

        ValidationHandler validationHandler = ValidationHandler.newBindingResultHandler(bindingResult);
        assertTrue(validationHandler.hasErrors());
        assertEquals(3, validationHandler.getAllErrors().size());
        assertEquals("A global error message", validationHandler.getAllErrors().get(0).getDefaultMessage());
        assertFalse(validationHandler.getAllErrors().get(0) instanceof FieldError);
        assertEquals("A field error 1", validationHandler.getAllErrors().get(1).getDefaultMessage());
        assertEquals("A field error 2", validationHandler.getAllErrors().get(2).getDefaultMessage());
        assertTrue(validationHandler.getAllErrors().get(1) instanceof FieldError);
        assertTrue(validationHandler.getAllErrors().get(2) instanceof FieldError);
    }

    @Test
    public void testAddingErrorsToValidationHandlerAsFieldErrorsUpdatesTheOverallErrorCount() {

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new TestForm(), "targetName");
        bindingResult.addError(new ObjectError("form", "A global error message"));
        bindingResult.rejectValue("formField", "Code1", "A field error 1");

        ValidationHandler validationHandler = ValidationHandler.newBindingResultHandler(bindingResult);

        validationHandler.addAnyErrors(serviceFailure(asList(
                new Error(PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE),
                new Error(BANK_DETAILS_CANNOT_BE_SUBMITTED_BEFORE_PROJECT_DETAILS))), toField("formField"));

        assertTrue(validationHandler.hasErrors());
        assertEquals(4, validationHandler.getAllErrors().size());
        assertEquals("A global error message", validationHandler.getAllErrors().get(0).getDefaultMessage());
        assertEquals("A field error 1", validationHandler.getAllErrors().get(1).getDefaultMessage());
        // TODO INFUND-4774
        //assertEquals("The Project Start Date must start in the future", validationHandler.getAllErrors().get(2).getDefaultMessage());
        // TODO INFUND-4774
        //assertEquals("Project details must be submitted before bank details", validationHandler.getAllErrors().get(3).getDefaultMessage());
        assertEquals("formField", ((FieldError) validationHandler.getAllErrors().get(1)).getField());
        assertEquals("formField", ((FieldError) validationHandler.getAllErrors().get(2)).getField());
        assertEquals("formField", ((FieldError) validationHandler.getAllErrors().get(3)).getField());
    }

    @Test
    public void testAddingErrorsToValidationHandlerAsGlobalErrorsUpdatesTheOverallErrorCount() {

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new TestForm(), "targetName");
        bindingResult.addError(new ObjectError("form", "A global error message"));
        bindingResult.rejectValue("formField", "Code1", "A field error 1");

        ValidationHandler validationHandler = ValidationHandler.newBindingResultHandler(bindingResult);
        validationHandler.addAnyErrors(serviceFailure(asList(
                new Error(PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE),
                new Error(BANK_DETAILS_CANNOT_BE_SUBMITTED_BEFORE_PROJECT_DETAILS))), asGlobalErrors());

        assertTrue(validationHandler.hasErrors());
        assertEquals(4, validationHandler.getAllErrors().size());
        assertEquals("A global error message", validationHandler.getAllErrors().get(0).getDefaultMessage());
        assertEquals("A field error 1", validationHandler.getAllErrors().get(1).getDefaultMessage());
        assertEquals("The Project Start Date must start in the future", validationHandler.getAllErrors().get(2).getDefaultMessage());
        assertEquals("Project details must be submitted before bank details", validationHandler.getAllErrors().get(3).getDefaultMessage());
        assertFalse(validationHandler.getAllErrors().get(0) instanceof FieldError);
        assertTrue(validationHandler.getAllErrors().get(1) instanceof FieldError);
        assertFalse(validationHandler.getAllErrors().get(2) instanceof FieldError);
        assertFalse(validationHandler.getAllErrors().get(3) instanceof FieldError);
    }

    @Test
    public void testAddingErrorsToValidationHandlerAsFieldErrorsForSpecificFields() {

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new TestForm(), "targetName");
        bindingResult.addError(new ObjectError("form", "A global error message"));
        bindingResult.rejectValue("formField", "Code1", "A field error 1");

        ValidationHandler validationHandler = ValidationHandler.newBindingResultHandler(bindingResult);

        validationHandler.addAnyErrors(serviceFailure(asList(
                new Error(PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE),
                new Error(BANK_DETAILS_CANNOT_BE_SUBMITTED_BEFORE_PROJECT_DETAILS))),
                mappingErrorKeyToField(PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE, "formField2"),
                mappingErrorKeyToField(BANK_DETAILS_CANNOT_BE_SUBMITTED_BEFORE_PROJECT_DETAILS, "formField"));

        assertTrue(validationHandler.hasErrors());
        assertEquals(4, validationHandler.getAllErrors().size());
        assertEquals("A global error message", validationHandler.getAllErrors().get(0).getDefaultMessage());
        assertEquals("A field error 1", validationHandler.getAllErrors().get(1).getDefaultMessage());
        // TODO INFUND-4774
        // assertEquals("The Project Start Date must start in the future", validationHandler.getAllErrors().get(1).getDefaultMessage());
        // TODO INFUND-4774
        // assertEquals("Project details must be submitted before bank details", validationHandler.getAllErrors().get(3).getDefaultMessage());
        assertEquals("formField", ((FieldError) validationHandler.getAllErrors().get(1)).getField());
        assertEquals("formField2", ((FieldError) validationHandler.getAllErrors().get(2)).getField());
        assertEquals("formField", ((FieldError) validationHandler.getAllErrors().get(3)).getField());
    }

    @Test
    public void testFailOnError() {

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new TestForm(), "targetName");
        bindingResult.addError(new ObjectError("form", "A global error message"));
        bindingResult.rejectValue("formField", "Code1", "A field error 1");

        ValidationHandler validationHandler = ValidationHandler.newBindingResultHandler(bindingResult);

        String result = validationHandler.failNowOrSucceedWith(() -> "failure", () -> "success");
        assertEquals("failure", result);
    }

    @Test
    public void testSucceedOnNoErrors() {

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new TestForm(), "targetName");
        ValidationHandler validationHandler = ValidationHandler.newBindingResultHandler(bindingResult);

        String result = validationHandler.failNowOrSucceedWith(() -> "failure", () -> "success");
        assertEquals("success", result);
    }

    @Test
    public void testFailOnErrorAddsErrorsToBindingResultTargetIfSpecified() {

        TestForm testForm = new TestForm();

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(testForm, "targetName");
        bindingResult.addError(new ObjectError("form", "A global error message"));
        bindingResult.rejectValue("formField", "Code1", "A field error 1");

        ValidationHandler validationHandler = ValidationHandler.newBindingResultHandler(bindingResult);
        validationHandler.setBindingResultTarget(testForm);

        assertNull(testForm.getBindingResult());
        assertTrue(testForm.getObjectErrors().isEmpty());

        String result = validationHandler.failNowOrSucceedWith(() -> "failure", () -> "success");
        assertEquals("failure", result);
        assertEquals(bindingResult, testForm.getBindingResult());

        assertEquals(2, testForm.getObjectErrors().size());
        assertEquals("A global error message", testForm.getObjectErrors().get(0).getDefaultMessage());
        assertFalse(testForm.getObjectErrors().get(0) instanceof FieldError);
        assertEquals("A field error 1", testForm.getObjectErrors().get(1).getDefaultMessage());
        assertTrue(testForm.getObjectErrors().get(1) instanceof FieldError);
    }

    private class TestForm extends BaseBindingResultTarget {

        private String formField;
        private String formField2;

        @SuppressWarnings("unused")
        public String getFormField() {
            return formField;
        }

        @SuppressWarnings("unused")
        public void setFormField(String formField) {
            this.formField = formField;
        }

        @SuppressWarnings("unused")
        public String getFormField2() {
            return formField2;
        }

        @SuppressWarnings("unused")
        public void setFormField2(String formField2) {
            this.formField2 = formField2;
        }
    }
}
