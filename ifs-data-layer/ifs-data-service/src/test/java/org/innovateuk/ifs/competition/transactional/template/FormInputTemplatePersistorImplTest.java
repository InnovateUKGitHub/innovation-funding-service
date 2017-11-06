package org.innovateuk.ifs.competition.transactional.template;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.GuidanceRow;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormValidator;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.application.builder.GuidanceRowBuilder.newFormInputGuidanceRow;
import static org.innovateuk.ifs.application.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.application.builder.SectionBuilder.newSection;
import static org.innovateuk.ifs.assessment.resource.AssessmentEvent.FEEDBACK;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.FormValidatorBuilder.newFormValidator;
import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_APPLICATION_IN_SCOPE;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

public class FormInputTemplatePersistorImplTest extends BaseServiceUnitTest<FormInputTemplatePersistorImpl> {

    public FormInputTemplatePersistorImpl supplyServiceUnderTest() {
        return new FormInputTemplatePersistorImpl();
    }

    private static final String COMPETIITON_TYPE_SECTOR_NAME = "Sector";
    private static final String SCOPE = "Scope";

    @Mock
    private GuidanceRowTemplatePersistorImpl guidanceRowTemplatePersistorMock;

    @Mock
    private EntityManager entityManagerMock;

    @Test
    public void persistByParentEntity_resultsInExpectedInitializedResult() throws Exception {
        Competition competition = newCompetition().withCompetitionType(newCompetitionType().withName(COMPETIITON_TYPE_SECTOR_NAME).build()).build();
        Set<FormValidator> formValidators = new HashSet<>(newFormValidator().build(2));

        List<FormInput> formInputsList = newFormInput()
                .withInputValidators(formValidators)
                .withDescription()
                .withGuidanceRows()
                .withId(1L,2L)
                .build(2);
        Question question = newQuestion()
                .withCompetition(competition)
                .withFormInputs(formInputsList).build();

        List<GuidanceRow> guidanceRows = newFormInputGuidanceRow().build(2);

        when(guidanceRowTemplatePersistorMock.persistByParentEntity(any())).thenReturn(guidanceRows);

        List<FormInput> result = service.persistByParentEntity(question);

        List<FormInput> expectedFormInputs = newFormInput()
                .withId()
                .withQuestion(question)
                .withDescription()
                .withCompetition(competition)
                .withInputValidators(formValidators)
                .withGuidanceRows(guidanceRows)
                .withActive(true)
                .build(2);

        assertThat(result.get(0), new ReflectionEquals(expectedFormInputs.get(0)));
        assertThat(result.get(1), new ReflectionEquals(expectedFormInputs.get(1)));
    }

    @Test
    public void persistByParentEntity_isSectorCompWithScopeQuestionShouldResultnActiveIsFalse() throws Exception {
        Competition competition = newCompetition().withCompetitionType(newCompetitionType().withName(COMPETIITON_TYPE_SECTOR_NAME).build()).build();
        Set<FormValidator> formValidators = new HashSet<>(newFormValidator().build(2));

        List<FormInput> formInputsList = newFormInput()
                .withInputValidators(formValidators)
                .withDescription()
                .withGuidanceRows()
                .withId(1L,2L)
                .withType(ASSESSOR_APPLICATION_IN_SCOPE)
                .withDescription(FEEDBACK.getType())
                .build(2);
        Question question = newQuestion()
                .withShortName(SCOPE)
                .withSection(newSection().withName(SCOPE).build())
                .withCompetition(competition)
                .withFormInputs(formInputsList).build();

        List<GuidanceRow> guidanceRows = newFormInputGuidanceRow().build(2);

        when(guidanceRowTemplatePersistorMock.persistByParentEntity(any())).thenReturn(guidanceRows);

        List<FormInput> result = service.persistByParentEntity(question);

        List<FormInput> expectedFormInputs = newFormInput()
                .withId()
                .withQuestion(question)
                .withDescription()
                .withCompetition(competition)
                .withInputValidators(formValidators)
                .withGuidanceRows(guidanceRows)
                .withType(ASSESSOR_APPLICATION_IN_SCOPE)
                .withDescription(FEEDBACK.getType())
                .withActive(false)
                .build(2);

        assertThat(result.get(0), new ReflectionEquals(expectedFormInputs.get(0)));
    }

    @Test
    public void persistByParentEntity_persistenceCallsAreMadeInOrder() throws Exception {
        Competition competition = newCompetition().withCompetitionType(newCompetitionType().withName(COMPETIITON_TYPE_SECTOR_NAME).build()).build();
        Set<FormValidator> formValidators = new HashSet<>(newFormValidator().build(2));

        List<FormInput> formInputsList = newFormInput()
                .withInputValidators(formValidators)
                .withDescription()
                .withGuidanceRows()
                .withId(1L,2L)
                .build(2);
        Question question = newQuestion()
                .withCompetition(competition)
                .withFormInputs(formInputsList).build();

        service.persistByParentEntity(question);

        InOrder inOrder = inOrder(guidanceRowTemplatePersistorMock, entityManagerMock, formInputRepositoryMock);

        List<FormInput> expectedFormInputs = newFormInput()
                .withId()
                .withQuestion(question)
                .withDescription()
                .withCompetition(competition)
                .withInputValidators(formValidators)
                .withGuidanceRows(Collections.emptyList())
                .withActive(true)
                .build(2);

        inOrder.verify(entityManagerMock).detach(formInputsList.get(0));
        inOrder.verify(formInputRepositoryMock).save(refEq(expectedFormInputs.get(0)));
        inOrder.verify(guidanceRowTemplatePersistorMock).persistByParentEntity(refEq(expectedFormInputs.get(0)));

        inOrder.verify(entityManagerMock).detach(formInputsList.get(1));
        inOrder.verify(formInputRepositoryMock).save(refEq(expectedFormInputs.get(1)));
        inOrder.verify(guidanceRowTemplatePersistorMock).persistByParentEntity(refEq(expectedFormInputs.get(1)));
    }

    @Test
    public void cleanForParentEntity() throws Exception {
        List<FormInput> formInputsList = newFormInput().withId(1L,2L).build(2);
        Question question = newQuestion().withFormInputs(formInputsList).build();

        service.cleanForParentEntity(question);

        InOrder inOrder = inOrder(guidanceRowTemplatePersistorMock, entityManagerMock, formInputRepositoryMock);
        inOrder.verify(guidanceRowTemplatePersistorMock).cleanForParentEntity(formInputsList.get(0));
        inOrder.verify(guidanceRowTemplatePersistorMock).cleanForParentEntity(formInputsList.get(1));

        inOrder.verify(entityManagerMock).detach(formInputsList.get(0));
        inOrder.verify(entityManagerMock).detach(formInputsList.get(1));

        inOrder.verify(formInputRepositoryMock).delete(formInputsList);
    }

}