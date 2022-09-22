package org.innovateuk.ifs.management.competition.setup.applicationexpressionofinterest.populator;

import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.applicationexpressionofinterest.viewmodel.ApplicationExpressionOfInterestViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;

public class ApplicationExpressionOfInterestViewModelPopulatorTest {

    private ApplicationExpressionOfInterestViewModelPopulator applicationExpressionOfInterestViewModelPopulator;

    @Before
    public void setup() {
        applicationExpressionOfInterestViewModelPopulator = new ApplicationExpressionOfInterestViewModelPopulator();
    }

    @Test
    public void populateModel() {

        CompetitionResource competition = newCompetitionResource().
                withCompletionStage(CompetitionCompletionStage.RELEASE_FEEDBACK).
                build();

        GeneralSetupViewModel generalSetupViewModel = new GeneralSetupViewModel(false, false, competition,
                CompetitionSetupSection.APPLICATION_EXPRESSION_OF_INTEREST, null, false, false, true);

        ApplicationExpressionOfInterestViewModel viewModel = applicationExpressionOfInterestViewModelPopulator.populateModel(generalSetupViewModel, competition);

        assertEquals(CompetitionSetupSection.APPLICATION_EXPRESSION_OF_INTEREST, viewModel.getGeneral().getCurrentSection());
        assertThat(viewModel.getGeneral()).isEqualTo(generalSetupViewModel);
    }

    @Test
    public void sectionToPopulateModel() {
        assertThat(applicationExpressionOfInterestViewModelPopulator.sectionToPopulateModel()).isEqualTo(CompetitionSetupSection.APPLICATION_EXPRESSION_OF_INTEREST);
    }
}
