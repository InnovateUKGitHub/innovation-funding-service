package org.innovateuk.ifs.validator.util;

import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.validator.AcademicJesValidator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;

import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.validator.ValidatorTestUtil.getBindingResult;
import static org.junit.Assert.assertTrue;

public class AcademicJesValidatorValidatorTest {
    private AcademicJesValidator validator;
    private FormInputResponse formInputResponse;
    private BindingResult bindingResult;
    private static Long formInputId = 1086L;

    @Before
    public void setUp() {
        validator = new AcademicJesValidator();
        formInputResponse = newFormInputResponse()
                .withFormInputs(newFormInput()
                        .withId(formInputId)
                        .withType(FormInputType.FINANCE_UPLOAD).build())
                .build();
        bindingResult = getBindingResult(formInputResponse);
    }

    @Test
    public void testValidate_formInputWithoutResponseShouldBeInvalid() throws Exception {
        formInputResponse.setValue("");
        validator.validate(formInputResponse, bindingResult);

        assertTrue(bindingResult.hasErrors());

        formInputResponse.setValue(" ");
        validator.validate(formInputResponse, bindingResult);

        assertTrue(bindingResult.hasErrors());
    }

    @Test
    public void testValidate_formInputWithResponseShouldBeValid() throws Exception {
        formInputResponse.setValue("SomeFileUpload.pdf");

        validator.validate(formInputResponse, bindingResult);

        assertFalse(bindingResult.hasErrors());
    }

    @Test
    public void testValidate_formInputWithDifferentTypeShouldBeValid() throws Exception {
        formInputResponse.setFormInput(newFormInput().withType(FormInputType.FILEUPLOAD).build());

        validator.validate(formInputResponse, bindingResult);

        assertFalse(bindingResult.hasErrors());
    }
}