package org.innovateuk.ifs.registration.viewmodel;

import com.google.common.collect.ImmutableSet;
import org.innovateuk.ifs.organisation.viewmodel.OrganisationSelectionChoiceViewModel;
import org.innovateuk.ifs.organisation.viewmodel.OrganisationSelectionViewModel;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

public class OrganisationSelectionViewModelTest {

    @Test
    public void construct() {
        OrganisationSelectionChoiceViewModel choice = mock(OrganisationSelectionChoiceViewModel.class);

        OrganisationSelectionViewModel viewModel = new OrganisationSelectionViewModel(ImmutableSet.of(choice),
                false,
                false,
                false,
                "url"
                );

        assertFalse(viewModel.canSelectOrganisation());
        assertFalse(viewModel.isApplicantJourney());
        assertFalse(viewModel.isInternationalJourney());
        assertEquals(viewModel.onlyOrganisation(), choice);
    }
}
