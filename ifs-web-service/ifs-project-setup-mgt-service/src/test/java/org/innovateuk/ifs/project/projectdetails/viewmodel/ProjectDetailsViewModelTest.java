package org.innovateuk.ifs.project.projectdetails.viewmodel;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProjectDetailsViewModelTest {

    @Test
    public void testKtpCompetition() {
        ProjectDetailsViewModel viewModel = new ProjectDetailsViewModel(null, null, null,
                null, null, false, null, null,
                null, null, false, true);

        assertTrue(viewModel.isKtpCompetition());
    }

    @Test
    public void testNonKtpCompetition() {
        ProjectDetailsViewModel viewModel = new ProjectDetailsViewModel(null, null, null,
                null, null, false, null, null,
                null, null, false, false);

        assertFalse(viewModel.isKtpCompetition());
    }
}
