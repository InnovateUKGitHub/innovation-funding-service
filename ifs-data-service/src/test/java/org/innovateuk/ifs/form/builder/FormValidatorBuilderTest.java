package org.innovateuk.ifs.form.builder;

import org.innovateuk.ifs.form.domain.FormValidator;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.form.builder.FormValidatorBuilder.newFormValidator;
import static org.junit.Assert.assertEquals;

public class FormValidatorBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        String expectedTitle = "title";
        String expectedClazzName = "clazzName";

        FormValidator formValidator = newFormValidator()
                .withId(expectedId)
                .withTitle(expectedTitle)
                .withClazzName(expectedClazzName)
                .build();

        assertEquals(expectedId, formValidator.getId());
        assertEquals(expectedTitle, formValidator.getTitle());
        assertEquals(expectedClazzName, formValidator.getClazzName());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        String[] expectedTitles = {"title 1", "title 2"};
        String[] expectedClazzNames = {"class 1", "class 2"};

        List<FormValidator> formValidators = newFormValidator()
                .withId(expectedIds)
                .withTitle(expectedTitles)
                .withClazzName(expectedClazzNames)
                .build(2);

        FormValidator first = formValidators.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedTitles[0], first.getTitle());
        assertEquals(expectedClazzNames[0], first.getClazzName());

        FormValidator second = formValidators.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedTitles[1], second.getTitle());
        assertEquals(expectedClazzNames[1], second.getClazzName());
    }

}
