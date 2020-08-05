package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationDetailsReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings.defaultSettings;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.PROCUREMENT;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationDetailsReadOnlyViewModelPopulatorTest {

    @InjectMocks
    private ApplicationDetailsReadOnlyViewModelPopulator populator;

    @Test
    public void populate() {
        LocalDate startDate = LocalDate.now();
        ApplicationResource application = newApplicationResource()
                .withName("Application name")
                .withStartDate(startDate)
                .withDurationInMonths(2L)
                .withResubmission(true)
                .withPreviousApplicationNumber("1234")
                .withPreviousApplicationTitle("Previous")
                .withInnovationArea(newInnovationAreaResource().withName("Innovation area").build())
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withName("Competition name")
                .withInnovationAreas(asSet(1L, 2L))
                .withFundingType(PROCUREMENT)
                .build();
        QuestionResource question = newQuestionResource()
                .withShortName("Application details")
                .build();

        ApplicationReadOnlyData data = new ApplicationReadOnlyData(application, competition, newUserResource().build(), empty(), emptyList(), emptyList(), emptyList(), emptyList(),  emptyList());

        ApplicationDetailsReadOnlyViewModel viewModel = populator.populate(competition, question, data, defaultSettings());

        assertEquals("Application name", viewModel.getApplicationName());
        assertEquals("Competition name", viewModel.getCompetitionName());
        assertEquals(startDate, viewModel.getStartDate());
        assertEquals(2L, (long) viewModel.getDuration());
        assertTrue(viewModel.getResubmission());
        assertEquals("1234", viewModel.getPreviousApplicationNumber());
        assertEquals("Previous", viewModel.getPreviousApplicationTitle());
        assertTrue(viewModel.isCanSelectInnovationArea());
        assertEquals("Innovation area", viewModel.getInnovationAreaName());

        assertEquals("Application details", viewModel.getName());
        assertEquals(application.getId(), (Long) viewModel.getApplicationId());
        assertEquals(question.getId(), (Long) viewModel.getQuestionId());
        assertFalse(viewModel.isComplete());
        assertFalse(viewModel.isLead());
        assertTrue(viewModel.isProcurementCompetition());
    }
}
