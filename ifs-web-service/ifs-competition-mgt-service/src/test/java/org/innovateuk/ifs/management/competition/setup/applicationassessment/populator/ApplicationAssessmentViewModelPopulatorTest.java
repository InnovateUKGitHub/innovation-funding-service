package org.innovateuk.ifs.management.competition.setup.applicationassessment.populator;

import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.applicationassessment.viewmodel.ApplicationAssessmentViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;

public class ApplicationAssessmentViewModelPopulatorTest {

    private ApplicationAssessmentViewModelPopulator applicationAssessmentViewModelPopulator;

    @Before
    public void setup() {
        applicationAssessmentViewModelPopulator = new ApplicationAssessmentViewModelPopulator();
    }

    @Test
    public void populateModel() {

        CompetitionResource competition = newCompetitionResource().
                withCompletionStage(CompetitionCompletionStage.RELEASE_FEEDBACK).
                build();

        GeneralSetupViewModel generalSetupViewModel = new GeneralSetupViewModel(false, false, competition,
                CompetitionSetupSection.APPLICATION_ASSESSMENT, null, false, false, true);

        ApplicationAssessmentViewModel viewModel = applicationAssessmentViewModelPopulator.populateModel(generalSetupViewModel, competition);

        assertEquals(CompetitionSetupSection.APPLICATION_ASSESSMENT, viewModel.getGeneral().getCurrentSection());
        assertThat(viewModel.getGeneral()).isEqualTo(generalSetupViewModel);
    }

    @Test
    public void sectionToPopulateModel() {
        assertThat(applicationAssessmentViewModelPopulator.sectionToPopulateModel()).isEqualTo(CompetitionSetupSection.APPLICATION_ASSESSMENT);
    }
}
