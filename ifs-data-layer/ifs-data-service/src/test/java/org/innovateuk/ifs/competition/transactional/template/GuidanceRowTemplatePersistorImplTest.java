package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.GuidanceRow;
import org.innovateuk.ifs.application.repository.GuidanceRowRepository;
import org.innovateuk.ifs.form.domain.FormInput;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import javax.persistence.EntityManager;
import java.util.List;

import static org.innovateuk.ifs.application.builder.GuidanceRowBuilder.newFormInputGuidanceRow;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.when;

public class GuidanceRowTemplatePersistorImplTest extends BaseServiceUnitTest<GuidanceRowTemplatePersistorImpl> {
    public GuidanceRowTemplatePersistorImpl supplyServiceUnderTest() {
        return new GuidanceRowTemplatePersistorImpl();
    }

    @Mock
    private EntityManager entityManagerMock;

    @Mock
    private GuidanceRowRepository guidanceRowRepositoryMock;

    @Test
    public void persistByParentEntity_detachesAndSavesEachRowInFormInput() throws Exception {
        List<GuidanceRow> guidanceRows = newFormInputGuidanceRow()
                .withSubject("subject 1", "subject 2")
                .withId(1L, 2L)
                .build(2);
        FormInput formInput = newFormInput().withGuidanceRows(guidanceRows).build();

        List<GuidanceRow> result = service.persistByParentEntity(formInput);

        GuidanceRow expectedGuidanceRow1 = newFormInputGuidanceRow()
                .withId()
                .withSubject("subject 1")
                .withFormInput(formInput)
                .build();

        GuidanceRow expectedGuidanceRow2 = newFormInputGuidanceRow()
                .withId()
                .withSubject("subject 2")
                .withFormInput(formInput)
                .build();

        Mockito.verify(entityManagerMock).detach(guidanceRows.get(0));
        Mockito.verify(entityManagerMock).detach(guidanceRows.get(1));

        Mockito.verify(guidanceRowRepositoryMock).save(refEq(expectedGuidanceRow1));
        Mockito.verify(guidanceRowRepositoryMock).save(refEq(expectedGuidanceRow2));

        assertTrue(result.size() == 2);
    }

    @Test
    public void persistByParentEntity_returnsSavedObjects() throws Exception {
        List<GuidanceRow> guidanceRows = newFormInputGuidanceRow()
                .withSubject("subject 1", "subject 2")
                .withId(1L, 2L)
                .build(2);
        FormInput formInput = newFormInput().withGuidanceRows(guidanceRows).build();

        List<GuidanceRow> initializedGuidanceRows = newFormInputGuidanceRow()
                .withId()
                .withSubject("subject 1", "subject 2")
                .withFormInput(formInput)
                .build(2);

        List<GuidanceRow> expectedGuidanceRows = newFormInputGuidanceRow()
                .withId(4L, 5L)
                .withSubject("subject 1", "subject 2")
                .withFormInput(formInput)
                .build(2);

        when(guidanceRowRepositoryMock.save(initializedGuidanceRows.get(0))).thenReturn(expectedGuidanceRows.get(0));
        when(guidanceRowRepositoryMock.save(initializedGuidanceRows.get(1))).thenReturn(expectedGuidanceRows.get(1));

        List<GuidanceRow> result = service.persistByParentEntity(formInput);

        assertThat(result, new ReflectionEquals(expectedGuidanceRows));
    }

    @Test
    public void persistByParentEntity_noGuidanceRowsResultsInEmptyList() throws Exception {
        FormInput formInput = newFormInput().build();

        List<GuidanceRow> result = service.persistByParentEntity(formInput);

        assertTrue(result.isEmpty());
    }

    @Test
    public void cleanForParentEntity_detachesAndDeletesEachRowInFormInput() throws Exception {
        List<GuidanceRow> guidanceRows = newFormInputGuidanceRow().build(2);
        FormInput formInput = newFormInput().withGuidanceRows(guidanceRows).build();

        service.cleanForParentEntity(formInput);

        Mockito.verify(entityManagerMock).detach(guidanceRows.get(0));
        Mockito.verify(entityManagerMock).detach(guidanceRows.get(1));
        Mockito.verify(guidanceRowRepositoryMock).delete(guidanceRows);
    }

}