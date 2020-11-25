package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.ScoreAssessmentQuestionReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.assessment.builder.ApplicationAssessmentsResourceBuilder.newApplicationAssessmentResource;
import static org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings.defaultSettings;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;

public class ScoreAssessmentQuestionReadOnlyPopulatorTest {

    private ScoreAssessmentQuestionReadOnlyPopulator populator;

    @Test
    public void populate() {

        Long questionId = 1L;
        populator = new ScoreAssessmentQuestionReadOnlyPopulator();

        ApplicationResource application = newApplicationResource()
                .withResearchCategory(newResearchCategoryResource().withName("Research category").build())
                .build();
        CompetitionResource competition = newCompetitionResource()
                .build();
        QuestionResource question = newQuestionResource()
                .withId(questionId)
                .withShortName("Impact")
                .build();
        List<ApplicationAssessmentResource> assessments = newApplicationAssessmentResource()
                .withApplicationId(application.getId())
                .withAssessmentId(2L, 3L)
                .withScores(asMap(questionId, BigDecimal.ONE), asMap(questionId, BigDecimal.TEN))
                .withFeedback(asMap(questionId, "Feedback-1"), asMap(questionId, "Feedback-10"))
                .build(2);

        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, newUserResource().build(),
                emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), assessments, emptyList());

        ApplicationReadOnlySettings settings = defaultSettings().setIncludeAllAssessorFeedback(true);

        ScoreAssessmentQuestionReadOnlyViewModel viewModel = populator.populate(competition, question, data, settings);

        assertNotNull(viewModel);
        assertEquals(questionId.longValue(), viewModel.getQuestionId());
        assertEquals(asList("Feedback-1", "Feedback-10"), viewModel.getFeedback());
        assertEquals(asList(BigDecimal.ONE, BigDecimal.TEN), viewModel.getScores());
        assertEquals(BigDecimal.valueOf(5.5), viewModel.getAverageScore());
    }
}
