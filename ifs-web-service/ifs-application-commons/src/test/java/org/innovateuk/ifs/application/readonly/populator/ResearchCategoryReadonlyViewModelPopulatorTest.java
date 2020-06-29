package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.viewmodel.ResearchCategoryReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings.defaultSettings;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(MockitoJUnitRunner.class)
public class ResearchCategoryReadonlyViewModelPopulatorTest {

    @InjectMocks
    private ResearchCategoryReadOnlyViewModelPopulator populator;

    @Mock
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Test
    public void populate() {
        ApplicationResource application = newApplicationResource()
                .withResearchCategory(newResearchCategoryResource().withName("Research category").build())
                .build();
        CompetitionResource competition = newCompetitionResource()
                .build();
        QuestionResource question = newQuestionResource()
                .withShortName("Research category")
                .build();
        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, newUserResource().build(), empty(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList());

        ResearchCategoryReadOnlyViewModel viewModel = populator.populate(competition, question, data, defaultSettings());

        assertEquals("Research category", viewModel.getResearchCategory());

        assertEquals("Research category", viewModel.getName());
        assertEquals(application.getId(), (Long) viewModel.getApplicationId());
        assertEquals(question.getId(), (Long) viewModel.getQuestionId());
        assertFalse(viewModel.isComplete());
        assertFalse(viewModel.isLead());
    }
}
