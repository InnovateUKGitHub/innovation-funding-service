package org.innovateuk.ifs.competitionsetup.applicationformbuilder.template;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AssessmentOnlyTemplateTest {

    private AssessmentOnlyTemplate assessmentOnlyTemplate;

    private Competition competition;

    @Before
    public void setup() {
        assessmentOnlyTemplate = new AssessmentOnlyTemplate();
        competition = newCompetition().build();
    }

    @Test
    public void initialiseOrganisationConfig() {
        Competition competitionConfig = assessmentOnlyTemplate.initialiseOrganisationConfig(competition);

        assertNotNull(competitionConfig.getCompetitionOrganisationConfig());
    }

    @Test
    public void initialiseApplicationConfig() {
        Competition competitionConfig = assessmentOnlyTemplate.initialiseApplicationConfig(competition);

        assertNotNull(competitionConfig.getApplicationConfiguration());
    }

    @Test
    public void sections() {
        List<SectionBuilder> sections = assessmentOnlyTemplate.sections();

        assertEquals(2, sections.size());

        assertEquals(SectionType.PROJECT_DETAILS, sections.get(0).getType());
        assertEquals(0, sections.get(0).getChildSections().size());
        assertEquals(1, sections.get(0).getQuestions().size());
        assertEquals(QuestionSetupType.APPLICATION_DETAILS, sections.get(0).getQuestions().get(0).getQuestionSetupType());

        assertEquals(SectionType.APPLICATION_QUESTIONS, sections.get(1).getType());
        assertEquals(0, sections.get(1).getChildSections().size());
        assertEquals(0, sections.get(1).getQuestions().size());
    }
}
