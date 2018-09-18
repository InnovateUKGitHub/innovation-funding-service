package org.innovateuk.ifs.registration.viewmodel;

import org.junit.Test;

import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class OrganisationSelectionViewModelTest {

    @Test
    public void construct() {
        OrganisationSelectionChoiceViewModel choice = mock(OrganisationSelectionChoiceViewModel.class);

        OrganisationSelectionViewModel viewModel = new OrganisationSelectionViewModel(asSet(choice),
                false,
                "url"
                );

        assertEquals(viewModel.canSelectOrganisation(), false);
        assertEquals(viewModel.onlyOrganisation(), choice);
    }
}
