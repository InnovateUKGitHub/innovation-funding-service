package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingtype;

import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.FormInputBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.FormInputBuilder.aFormInput;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder.aQuestion;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder.aSection;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class KtpAktTemplateTest {

    @InjectMocks
    private KtpAktTemplate template;

    @Test
    public void ktpAssessmentSectionDisabled() {
        List<SectionBuilder> sections = template.sections(newArrayList());

        assertNotNull(sections);
        assertFalse(sections.stream().anyMatch(section -> SectionType.KTP_ASSESSMENT == section.getType()));
    }

    @Test
    public void noOverridesToApplicationQuestionFormInputs() {
        FormInputBuilder formInputBuilder = aFormInput()
                .withScope(FormInputScope.ASSESSMENT)
                .withActive(true);

        QuestionBuilder questionBuilder = aQuestion()
                .withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION)
                .withFormInputs(Collections.singletonList(formInputBuilder));

        SectionBuilder sectionBuilder = aSection()
                .withAssessorGuidanceDescription("This should be enabled")
                .withType(SectionType.APPLICATION_QUESTIONS)
                .withQuestions(Collections.singletonList(questionBuilder));

        List<SectionBuilder> sections = template.sections(newArrayList(sectionBuilder));

        assertEquals("This should be enabled", sections.get(0).getAssessorGuidanceDescription());
        assertEquals(QuestionSetupType.ASSESSED_QUESTION, sections.get(0).getQuestions().get(0).getQuestionSetupType());
        assertEquals(FormInputScope.ASSESSMENT, sections.get(0).getQuestions().get(0).getFormInputs().get(0).getScope());
        assertTrue(sections.get(0).getQuestions().get(0).getFormInputs().get(0).isActive());
    }
}