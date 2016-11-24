package com.worth.ifs.form.builder;

import com.worth.ifs.form.domain.FormInputType;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.form.builder.FormInputTypeBuilder.newFormInputType;
import static org.junit.Assert.assertEquals;

public class FormInputTypeBuilderTest {

    @Test
    public void buildOne() throws Exception {
        Long expectedId = 1L;
        String expectedTitle = "title";

        FormInputType formInputType = newFormInputType()
                .withId(expectedId)
                .withTitle(expectedTitle)
                .build();

        assertEquals(expectedId, formInputType.getId());
        assertEquals(expectedTitle, formInputType.getTitle());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        String[] expectedTitles = {"title 1", "title 2"};

        List<FormInputType> formInputTypes = newFormInputType()
                .withId(expectedIds)
                .withTitle(expectedTitles)
                .build(2);

        FormInputType first = formInputTypes.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedTitles[0], first.getTitle());

        FormInputType second = formInputTypes.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedTitles[1], second.getTitle());
    }
}