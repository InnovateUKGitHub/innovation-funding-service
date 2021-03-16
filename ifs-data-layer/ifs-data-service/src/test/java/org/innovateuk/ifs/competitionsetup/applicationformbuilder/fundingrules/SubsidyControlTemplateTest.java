package org.innovateuk.ifs.competitionsetup.applicationformbuilder.fundingrules;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder.aQuestion;
import static org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder.aSection;

public class SubsidyControlTemplateTest {
    private SubsidyControlTemplate template;
    private List<SectionBuilder> sections;

    @Before
    public void setUp() {
        template = new SubsidyControlTemplate();
        ReflectionTestUtils.setField(template, "northernIrelandSubsidyControlToggle", false);

        sections = newArrayList(
                aSection()
                        .withName("Project details")
                        .withQuestions(newArrayList(aQuestion()
                                .withName("question1"))),
                aSection().withName("Something else"));
    }

    @Test
    public void shouldInjectQuestionToProjectDetails() {
        Competition competition = newCompetition().build();

        sections = template.sections(competition, sections);

        assertThat(sections).hasSize(2);
        assertThat(sections.get(0).getQuestions()).hasSize(2);
        assertThat(sections.get(0).getQuestions().get(0).getQuestionSetupType()).isEqualTo(QuestionSetupType.NORTHERN_IRELAND_DECLARATION);
    }

    @Test
    public void shouldNotInjectQuestionToProjectDetailsIfPrincesTrustComp() {
        Competition competition = newCompetition().withCompetitionType(
                newCompetitionType()
                    .withName(CompetitionTypeEnum.THE_PRINCES_TRUST.getText())
                    .build())
                .build();

        sections = template.sections(competition, sections);

        assertThat(sections).hasSize(2);
        assertThat(sections.get(0).getQuestions()).hasSize(1);
        assertThat(sections.get(0).getQuestions().get(0).getName()).isEqualTo("question1");
    }

    @Test
    public void shouldNotInjectQuestionToProjectDetailsIfExpressionOfInterestComp() {
        Competition competition = newCompetition().withCompetitionType(
                newCompetitionType()
                        .withName(CompetitionTypeEnum.EXPRESSION_OF_INTEREST.getText())
                        .build())
                .build();

        sections = template.sections(competition, sections);

        assertThat(sections).hasSize(2);
        assertThat(sections.get(0).getQuestions()).hasSize(1);
        assertThat(sections.get(0).getQuestions().get(0).getName()).isEqualTo("question1");
    }
}
