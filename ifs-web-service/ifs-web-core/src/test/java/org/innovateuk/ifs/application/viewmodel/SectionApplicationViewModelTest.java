package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationStatus;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class SectionApplicationViewModelTest {

    private SectionApplicationViewModel viewModel;

    private Long sectionId = 23456L;
    private CompetitionResource currentCompetition;
    private ApplicationResource currentApplication;


    @Before
    public void setup() {
        currentCompetition = newCompetitionResource().build();
        currentApplication = newApplicationResource().build();
        currentCompetition.setCompetitionStatus(CompetitionStatus.CLOSED);
        currentApplication.setApplicationStatus(ApplicationStatus.SUBMITTED);

        viewModel = new SectionApplicationViewModel();
        viewModel.setCurrentCompetition(currentCompetition);
        viewModel.setCurrentApplication(currentApplication);

        viewModel.setCurrentCompetition(currentCompetition);
    }

    @Test
    public void testGetApplicationIsReadOnly() {
        assertEquals(Boolean.TRUE, viewModel.getApplicationIsReadOnly());
    }

    @Test
    public void testGetApplicationIsClosed() {
        assertEquals(Boolean.TRUE, viewModel.getApplicationIsClosed());
    }
}
