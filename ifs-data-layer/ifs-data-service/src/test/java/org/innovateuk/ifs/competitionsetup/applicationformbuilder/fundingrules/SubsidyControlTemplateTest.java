package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingrules;

import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.featureswitch.SubsidyControlNorthernIrelandMode;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder.aQuestion;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder.aSection;

public class SubsidyControlTemplateTest {
    private SubsidyControlTemplate template;

    @Before
    public void setUp() {
        template = new SubsidyControlTemplate();
        ReflectionTestUtils.setField(template, "subsidyControlNorthernIrelandMode", SubsidyControlNorthernIrelandMode.TACTICAL);
    }

    @Test
    public void shouldInjectQuestionToProjectDetails() {
        List<SectionBuilder> sections = newArrayList(
                aSection()
                    .withName("Project details")
                    .withQuestions(newArrayList(aQuestion()
                        .withName("question1"))),
                aSection().withName("Something else"));

        sections = template.sections(sections);

        assertThat(sections).hasSize(2);
        assertThat(sections.get(0).getQuestions()).hasSize(2);
        assertThat(sections.get(0).getQuestions().get(0).getQuestionSetupType()).isEqualTo(QuestionSetupType.NORTHERN_IRELAND_DECLARATION);
    }
}
