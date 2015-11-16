package com.worth.ifs.form.builder;

import com.worth.ifs.form.domain.FormInput;
import org.junit.Test;

import static com.worth.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.junit.Assert.assertEquals;

/**
 * Testing that the FormInput builder performs as expected.
 */
public class FormInputBuilderTest {

    @Test
    public void test_buildOne_defaultValuesAsExpected() {

        FormInput formInput = newFormInput().build();
        assertEquals(Long.valueOf(1), formInput.getId());
        assertEquals("Description 1", formInput.getDescription());
    }
}
