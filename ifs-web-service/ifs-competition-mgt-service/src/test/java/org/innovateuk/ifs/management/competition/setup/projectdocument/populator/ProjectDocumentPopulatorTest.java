package org.innovateuk.ifs.management.competition.setup.projectdocument.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.projectdocument.viewmodel.ProjectDocumentViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ProjectDocumentPopulatorTest {

    private static final Long COMPETITION_ID = 8L;

    @InjectMocks
    private ProjectDocumentPopulator populator;

    @Test
    public void testPopulateModel() {
        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withName("name")
                .build();

        GeneralSetupViewModel generalSetupViewModel = getBasicGeneralSetupView(competitionResource);
        ProjectDocumentViewModel viewModel = (ProjectDocumentViewModel) populator.populateModel(generalSetupViewModel, competitionResource);

        assertEquals(generalSetupViewModel, viewModel.getGeneral());
        assertEquals(CompetitionSetupSection.PROJECT_DOCUMENT, viewModel.getGeneral().getCurrentSection());
    }

    private GeneralSetupViewModel getBasicGeneralSetupView(CompetitionResource competitionResource) {
        return new GeneralSetupViewModel(false, competitionResource, CompetitionSetupSection.PROJECT_DOCUMENT, CompetitionSetupSection.values(), true, false);
    }
}

