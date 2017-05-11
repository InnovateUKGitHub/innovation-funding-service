package org.innovateuk.ifs.validator.util;

import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.validator.AcademicJesValidator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;

import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.validator.ValidatorTestUtil.getBindingResult;
import static org.junit.Assert.assertTrue;

public class AcademicJesValidatorValidatorTest {
    private AcademicJesValidator validator;
    private FormInputResponse formInputResponse;
    private BindingResult bindingResult;
    private static final Long FORM_INPUT_ID = 1086L;
    private static final Long ORGANISATION_ID = 123L;

    @Before
    public void setUp() {
        validator = new AcademicJesValidator();
        formInputResponse = newFormInputResponse()
                .withFormInputs(newFormInput()
                        .withId(FORM_INPUT_ID)
                        .withType(FormInputType.FINANCE_UPLOAD).build())
                .with(response -> {
                        response.setApplication(newApplication()
                                .with(application -> application.setApplicationFinances(
                                        newApplicationFinance()
                                                .withOrganisation(newOrganisation()
                                                        .withId(ORGANISATION_ID)
                                                        .build())
                                                .with(
                                                        applicationFinance -> applicationFinance.setFinanceFileEntry(
                                                                newFileEntry().build())
                                                )
                                                .build(1)
                                ))
                                .build());
                        response.setUpdatedBy(newProcessRole().withOrganisationId(ORGANISATION_ID).build());
                    }
                )
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

    @Test
    public void testValidate_applicationFinanceNoEntry() throws Exception {
        formInputResponse.setApplication(newApplication()
                .with(application -> application.setApplicationFinances(
                        newApplicationFinance()
                                .withOrganisation(newOrganisation()
                                        .withId(ORGANISATION_ID)
                                        .build())
                                .with(applicationFinance -> applicationFinance.setFinanceFileEntry(null))
                                .build(1)
                ))
                .build());

        validator.validate(formInputResponse, bindingResult);

        assertTrue(bindingResult.hasErrors());
    }

    @Test
    public void testValidate_NoApplicationFinance() throws Exception {
        formInputResponse.setApplication(newApplication()
                .with(application -> application.setApplicationFinances(null))
                .build());

        validator.validate(formInputResponse, bindingResult);

        assertTrue(bindingResult.hasErrors());
    }

    @Test
    public void testValidate_NoApplicationFinanceWithSameOrg() throws Exception {
        formInputResponse.setUpdatedBy(newProcessRole().withOrganisationId(88L).build());
        validator.validate(formInputResponse, bindingResult);

        assertTrue(bindingResult.hasErrors());
    }
}