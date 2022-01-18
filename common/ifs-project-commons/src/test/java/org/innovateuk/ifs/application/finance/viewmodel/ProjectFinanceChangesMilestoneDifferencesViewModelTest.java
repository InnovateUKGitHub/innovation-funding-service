package org.innovateuk.ifs.application.finance.viewmodel;

import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectFinanceChangesMilestoneDifferencesViewModelTest {

    private ProjectFinanceChangesMilestoneDifferencesViewModel viewModel;

    @Test
    public void shouldFormatTotalVariance() {
        viewModel = new ProjectFinanceChangesMilestoneDifferencesViewModel(null, new BigInteger("10"), new BigInteger("100000"));

        String result = viewModel.getTotalVariance();

        assertThat(result).isEqualTo("+ £99,990");
    }

    @Test
    public void shouldFormatTotalVarianceZero() {
        viewModel = new ProjectFinanceChangesMilestoneDifferencesViewModel(null, new BigInteger("10"), new BigInteger("10"));

        String result = viewModel.getTotalVariance();

        assertThat(result).isEqualTo("£0");
    }

    @Test
    public void shouldNotIndicateChangesIfAllMilestoneDifferencesAreSame() {

        MilestoneChangeViewModel change1 = new MilestoneChangeViewModel();
        change1.setType(MilestoneChangeViewModel.MilestoneChangeType.SAME);
        MilestoneChangeViewModel change2 = new MilestoneChangeViewModel();
        change2.setType(MilestoneChangeViewModel.MilestoneChangeType.SAME);
        List<MilestoneChangeViewModel> changeList = Arrays.asList(change1, change2);

        viewModel = new ProjectFinanceChangesMilestoneDifferencesViewModel(changeList, new BigInteger("10"), new BigInteger("10"));

        boolean result = viewModel.hasChanges();

        assertThat(result).isFalse();
    }

    @Test
    public void shouldIndicateChangesIfAMilestoneDifferencesIsNotSame() {

        MilestoneChangeViewModel change1 = new MilestoneChangeViewModel();
        change1.setType(MilestoneChangeViewModel.MilestoneChangeType.SAME);
        MilestoneChangeViewModel change2 = new MilestoneChangeViewModel();
        change2.setType(MilestoneChangeViewModel.MilestoneChangeType.UPDATED);
        List<MilestoneChangeViewModel> changeList = Arrays.asList(change1, change2);

        viewModel = new ProjectFinanceChangesMilestoneDifferencesViewModel(changeList, new BigInteger("10"), new BigInteger("10"));

        boolean result = viewModel.hasChanges();

        assertThat(result).isTrue();
    }
}
