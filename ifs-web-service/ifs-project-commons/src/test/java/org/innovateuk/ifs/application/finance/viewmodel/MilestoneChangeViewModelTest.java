package org.innovateuk.ifs.application.finance.viewmodel;

import org.junit.Test;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class MilestoneChangeViewModelTest {
    private MilestoneChangeViewModel viewModel;

    @Test
    public void shouldFormatPaymentVarianceCorrectly() {
        viewModel = new MilestoneChangeViewModel();
        viewModel.setType(MilestoneChangeViewModel.MilestoneChangeType.UPDATED);
        viewModel.setPaymentSubmitted(new BigInteger("10"));
        viewModel.setPaymentUpdated(new BigInteger("10000"));

        String result = viewModel.getPaymentVariance();

        assertThat(result).isEqualTo("+ 9,990");
    }
}
