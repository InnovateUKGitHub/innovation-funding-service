package com.worth.ifs.form.builder;

import com.worth.ifs.form.domain.FormInput;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.clearUniqueIds;
import static com.worth.ifs.form.builder.FormInputBuilder.newFormInput;
import static com.worth.ifs.form.resource.FormInputScope.APPLICATION;
import static com.worth.ifs.form.resource.FormInputScope.ASSESSMENT;
import static org.junit.Assert.assertEquals;

/**
 * Testing that the FormInput builder performs as expected.
 */
public class FormInputBuilderTest {

    @Before
    public void setup() {
        clearUniqueIds();
    }

    @Test
    public void test_buildOne_defaultValuesAsExpected() {
        FormInput formInput = newFormInput()
                .withPriority(0)
                .withScope(APPLICATION)
                .build();
        assertEquals(Long.valueOf(1), formInput.getId());
        assertEquals("Description 1", formInput.getDescription());
        assertEquals(Integer.valueOf(0), formInput.getPriority());
        assertEquals(APPLICATION, formInput.getScope());
    }

    @Test
    public void test_buildMany_defaultValuesAsExpected() {
        List<FormInput> formInputs = newFormInput()
                .withPriority(0, 1, 2)
                .withScope(APPLICATION, ASSESSMENT, APPLICATION)
                .build(3);

        assertEquals(Long.valueOf(1), formInputs.get(0).getId());
        assertEquals("Description 1", formInputs.get(0).getDescription());
        assertEquals(Integer.valueOf(0), formInputs.get(0).getPriority());
        assertEquals(APPLICATION, formInputs.get(0).getScope());

        assertEquals(Long.valueOf(2), formInputs.get(1).getId());
        assertEquals("Description 2", formInputs.get(1).getDescription());
        assertEquals(Integer.valueOf(1), formInputs.get(1).getPriority());
        assertEquals(ASSESSMENT, formInputs.get(1).getScope());

        assertEquals(Long.valueOf(3), formInputs.get(2).getId());
        assertEquals("Description 3", formInputs.get(2).getDescription());
        assertEquals(Integer.valueOf(2), formInputs.get(2).getPriority());
        assertEquals(APPLICATION, formInputs.get(2).getScope());
    }
}
