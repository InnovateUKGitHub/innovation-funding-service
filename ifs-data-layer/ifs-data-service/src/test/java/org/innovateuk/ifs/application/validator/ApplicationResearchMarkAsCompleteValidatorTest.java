package org.innovateuk.ifs.application.validator;

import org.innovateuk.ifs.application.builder.ApplicationBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import static org.innovateuk.ifs.category.builder.ResearchCategoryBuilder.newResearchCategory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Mark as complete validator test class for application research category section
 */
public class ApplicationResearchMarkAsCompleteValidatorTest {

    private Validator validator;

    @Before
    public void setUp() {
        validator = new ApplicationResearchMarkAsCompleteValidator();
    }

    @Test
    public void validate() {

        Application application = ApplicationBuilder
                .newApplication()
                .withResearchCategory(newResearchCategory().build())
                .build();

        DataBinder binder = new DataBinder(application);
        BindingResult bindingResult = binder.getBindingResult();
        validator.validate(application, bindingResult);

        assertFalse(bindingResult.hasErrors());
    }

    @Test
    public void validate_nullResearchCategory() {

        Application application = ApplicationBuilder
                .newApplication()
                .withResearchCategory(null)
                .build();

        DataBinder binder = new DataBinder(application);
        BindingResult bindingResult = binder.getBindingResult();
        validator.validate(application, bindingResult);

        assertTrue(bindingResult.hasErrors());
        assertEquals(bindingResult.getFieldError("researchCategory").getDefaultMessage(), "validation.application.research.category.required");
    }

}
