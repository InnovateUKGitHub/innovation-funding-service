package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class QuestionApplicationViewModelTest {

    private QuestionApplicationViewModel viewModel;

    @Before
    public void setup(){
        ApplicationResource currentApplication = newApplicationResource().withApplicationState(ApplicationState.SUBMITTED).build();
        CompetitionResource competitionResource = newCompetitionResource().withCompetitionStatus(CompetitionStatus.CLOSED).build();
        OrganisationResource userOrganisation = newOrganisationResource().build();

        viewModel = new QuestionApplicationViewModel(new HashSet<>(asList(0L)), Boolean.FALSE, currentApplication, competitionResource, userOrganisation);
    }

    @Test
    public void testGetApplicationIsReadOnly() {
        assertEquals(Boolean.TRUE, viewModel.getApplicationIsReadOnly());
    }

    @Test
    public void testGetApplicationIsClosed() {
        assertEquals(Boolean.TRUE, viewModel.getApplicationIsClosed());
    }

    @Test
    public void testResearchCategoryHasBeenSelected_nullResearchCategoryShouldResultInNoResearchCategorySelected() {
        viewModel.setSelectedResearchCategoryName(null);

        assertFalse(viewModel.researchCategoryHasBeenSelected());
    }

    @Test
    public void testResearchCategoryHasBeenSelected_researchCategoryShouldResultInResearchCategorySelected() {
        viewModel.setSelectedResearchCategoryName("A category name");

        assertTrue(viewModel.researchCategoryHasBeenSelected());
    }

    @Test
    public void testInnovationAreaHasBeenSelected_nullInnovationAreaAndApplicableShouldResultInNoInnovationAreaSelected() {
        viewModel.setNoInnovationAreaApplicable(false);

        assertFalse(viewModel.innovationAreaHasBeenSelected());
    }

    @Test
    public void testInnovationAreaHasBeenSelected_nullInnovationAreaAndNotApplicableShouldResultInInnovationAreaSelected() {
        viewModel.setNoInnovationAreaApplicable(true);

        assertTrue(viewModel.innovationAreaHasBeenSelected());
    }

    @Test
    public void testInnovationAreaHasBeenSelected_innovationAreaAndApplicableShouldResultInInnovationAreaSelected() {
        viewModel.setNoInnovationAreaApplicable(false);
        viewModel.setSelectedInnovationAreaName("An innovation area name");

        assertTrue(viewModel.innovationAreaHasBeenSelected());
    }

    @Test
    public void testInnovationAreaHasBeenSelected_innovationAreaAndNotApplicableShouldResultInInnovationAreaSelected() {
        viewModel.setNoInnovationAreaApplicable(true);
        viewModel.setSelectedInnovationAreaName("An innovation area name");

        assertTrue(viewModel.innovationAreaHasBeenSelected());
    }
}
