package org.innovateuk.ifs.competitionsetup.applicationformbuilder.template;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.QuestionBuilder;
import org.innovateuk.ifs.competitionsetup.applicationformbuilder.builder.SectionBuilder;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HorizonEuropeGuaranteeTemplateTest {

    private HorizonEuropeGuaranteeTemplate horizonEuropeGuaranteeTemplate;

    private Competition competition;

    @Before
    public void setup() {
        horizonEuropeGuaranteeTemplate = new HorizonEuropeGuaranteeTemplate();
        competition = newCompetition().build();
    }

    @Test
    public void questions() {
        List<SectionBuilder> sections = horizonEuropeGuaranteeTemplate.sections();

        Optional<SectionBuilder> optionalProjectDetails = sections.stream()
                .filter(section -> section.getType() == SectionType.PROJECT_DETAILS)
                .findFirst();

        assertTrue(optionalProjectDetails.isPresent());

        SectionBuilder projectDetails = optionalProjectDetails.get();

        Optional<QuestionBuilder> optionalResearchCategory = projectDetails.getQuestions().stream()
                .filter(question -> question.getQuestionSetupType() == QuestionSetupType.RESEARCH_CATEGORY)
                .findFirst();

        assertTrue(optionalResearchCategory.isPresent());

        Optional<SectionBuilder> optionalApplicationQuestions = sections.stream()
                .filter(section -> section.getType() == SectionType.APPLICATION_QUESTIONS)
                .findFirst();

        assertTrue(optionalApplicationQuestions.isPresent());

        SectionBuilder applicationQuestions = optionalApplicationQuestions.get();

        assertEquals(3, applicationQuestions.getQuestions().size());

        QuestionBuilder organisation = applicationQuestions.getQuestions().get(0);
        assertEquals("Tell us where your organisation is based", organisation.getName());
        assertEquals("Tell us where your organisation is based", organisation.getShortName());
        assertEquals("You must be an organisation based in the UK or a British Overseas Territory to receive funding.", organisation.getDescription());
        assertEquals(QuestionSetupType.ASSESSED_QUESTION, organisation.getQuestionSetupType());

        QuestionBuilder projectRegion = applicationQuestions.getQuestions().get(1);
        assertEquals("Participating Organisation project region", projectRegion.getName());
        assertEquals("Participating Organisation project region", projectRegion.getShortName());
        assertEquals("Please type the region your project is being carried out in.", projectRegion.getDescription());
        assertEquals(QuestionSetupType.ASSESSED_QUESTION, projectRegion.getQuestionSetupType());

        QuestionBuilder horizonEuropeDefaultQuestion = applicationQuestions.getQuestions().get(2);
        assertEquals("Horizon Europe placeholder question", horizonEuropeDefaultQuestion.getName());
        assertEquals("Horizon Europe placeholder question", horizonEuropeDefaultQuestion.getShortName());
        assertEquals("Horizon Europe placeholder question description", horizonEuropeDefaultQuestion.getDescription());
        assertEquals(QuestionSetupType.ASSESSED_QUESTION, horizonEuropeDefaultQuestion.getQuestionSetupType());
    }
}
