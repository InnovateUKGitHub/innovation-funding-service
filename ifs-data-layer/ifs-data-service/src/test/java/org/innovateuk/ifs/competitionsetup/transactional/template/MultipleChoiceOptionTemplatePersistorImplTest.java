package org.innovateuk.ifs.competitionsetup.transactional.template;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.MultipleChoiceOption;
import org.innovateuk.ifs.form.repository.MultipleChoiceOptionRepository;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import javax.persistence.EntityManager;
import java.util.List;

import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.MultipleChoiceOptionBuilder.newMultipleChoiceOption;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.when;

public class MultipleChoiceOptionTemplatePersistorImplTest extends BaseServiceUnitTest<MultipleChoiceOptionTemplatePersistorImpl> {
    public MultipleChoiceOptionTemplatePersistorImpl supplyServiceUnderTest() {
        return new MultipleChoiceOptionTemplatePersistorImpl();
    }

    @Mock
    private EntityManager entityManagerMock;

    @Mock
    private MultipleChoiceOptionRepository multipleChoiceOptionRepository;

    @Test
    public void persistByParentEntity_detachesAndSavesEachRowInFormInput() throws Exception {
        List<MultipleChoiceOption> multipleChoiceOptions = newMultipleChoiceOption()
                .withText("subject 1", "subject 2")
                .withId(1L, 2L)
                .build(2);
        FormInput formInput = newFormInput().withMultipleChoiceOptions(multipleChoiceOptions).build();

        List<MultipleChoiceOption> result = service.persistByParentEntity(formInput);

        MultipleChoiceOption expectedGuidanceRow1 = newMultipleChoiceOption()
                .withId()
                .withText("subject 1")
                .withFormInput(formInput)
                .build();

        MultipleChoiceOption expectedGuidanceRow2 = newMultipleChoiceOption()
                .withId()
                .withText("subject 2")
                .withFormInput(formInput)
                .build();

        Mockito.verify(entityManagerMock).detach(multipleChoiceOptions.get(0));
        Mockito.verify(entityManagerMock).detach(multipleChoiceOptions.get(1));

        Mockito.verify(multipleChoiceOptionRepository).save(refEq(expectedGuidanceRow1));
        Mockito.verify(multipleChoiceOptionRepository).save(refEq(expectedGuidanceRow2));

        assertTrue(result.size() == 2);
    }

    @Test
    public void persistByParentEntity_returnsSavedObjects() throws Exception {
        List<MultipleChoiceOption> multipleChoiceOptions = newMultipleChoiceOption()
                .withText("subject 1", "subject 2")
                .withId(1L, 2L)
                .build(2);
        FormInput formInput = newFormInput().withMultipleChoiceOptions(multipleChoiceOptions).build();

        List<MultipleChoiceOption> initializedGuidanceRows = newMultipleChoiceOption()
                .withId()
                .withText("subject 1", "subject 2")
                .withFormInput(formInput)
                .build(2);

        List<MultipleChoiceOption> expectedGuidanceRows = newMultipleChoiceOption()
                .withId(4L, 5L)
                .withText("subject 1", "subject 2")
                .withFormInput(formInput)
                .build(2);

        when(multipleChoiceOptionRepository.save(initializedGuidanceRows.get(0))).thenReturn(expectedGuidanceRows.get(0));
        when(multipleChoiceOptionRepository.save(initializedGuidanceRows.get(1))).thenReturn(expectedGuidanceRows.get(1));

        List<MultipleChoiceOption> result = service.persistByParentEntity(formInput);

        assertThat(result, new BaseMatcher<List<MultipleChoiceOption>>() {
            @Override
            public boolean matches(Object item) {
                return new ReflectionEquals(expectedGuidanceRows).matches(item);
            }
            @Override
            public void describeTo(Description description) {
            }
        });
    }

    @Test
    public void persistByParentEntity_noGuidanceRowsResultsInEmptyList() throws Exception {
        FormInput formInput = newFormInput().build();

        List<MultipleChoiceOption> result = service.persistByParentEntity(formInput);

        assertTrue(result.isEmpty());
    }

    @Test
    public void cleanForParentEntity_detachesAndDeletesEachRowInFormInput() throws Exception {
        List<MultipleChoiceOption> multipleChoiceOptions = newMultipleChoiceOption().build(2);
        FormInput formInput = newFormInput().withMultipleChoiceOptions(multipleChoiceOptions).build();

        service.cleanForParentEntity(formInput);

        Mockito.verify(entityManagerMock).detach(multipleChoiceOptions.get(0));
        Mockito.verify(entityManagerMock).detach(multipleChoiceOptions.get(1));
        Mockito.verify(multipleChoiceOptionRepository).deleteAll(multipleChoiceOptions);
    }

}