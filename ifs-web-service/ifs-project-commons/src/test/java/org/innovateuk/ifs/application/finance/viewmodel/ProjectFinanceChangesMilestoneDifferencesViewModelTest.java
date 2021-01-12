package org.innovateuk.ifs.application.finance.viewmodel;

import org.junit.Test;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectFinanceChangesMilestoneDifferencesViewModelTest {

    private ProjectFinanceChangesMilestoneDifferencesViewModel viewModel;

    @Test
    public void shouldFormatTotalVariance() {
        viewModel = new ProjectFinanceChangesMilestoneDifferencesViewModel(null, new BigInteger("10"), new BigInteger("100000"));

        String result = viewModel.getTotalVariance();

        assertThat(result).isEqualTo("+ £99,990");
    }
}
