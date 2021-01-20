package org.innovateuk.ifs.management.competition.setup.applicationsubmission.populator;

import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.applicationsubmission.viewmodel.ApplicationSubmissionViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;

public class ApplicationSubmissionViewModelPopulatorTest {

    private ApplicationSubmissionViewModelPopulator applicationSubmissionViewModelPopulator;

    @Before
    public void setup() {
        applicationSubmissionViewModelPopulator = new ApplicationSubmissionViewModelPopulator();
    }

    @Test
    public void populateModel() {

        CompetitionResource competition = newCompetitionResource().
                withCompletionStage(CompetitionCompletionStage.RELEASE_FEEDBACK).
                build();

        GeneralSetupViewModel generalSetupViewModel = new GeneralSetupViewModel(false, false, competition,
                null, null, false, false);

        ApplicationSubmissionViewModel viewModel = applicationSubmissionViewModelPopulator.populateModel(generalSetupViewModel, competition);

        assertThat(viewModel.getGeneral()).isEqualTo(generalSetupViewModel);
    }

    @Test
    public void sectionToPopulateModel() {
        assertThat(applicationSubmissionViewModelPopulator.sectionToPopulateModel()).isEqualTo(CompetitionSetupSection.APPLICATION_SUBMISSION);
    }
}
