package org.innovateuk.ifs.assessment.overview.viewmodel;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP_AKT;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class AssessmentFinancesSummaryViewModelTest {

    private final FundingType fundingType;

    @Parameterized.Parameters(name = "{index}: FundingType->{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[] [] {
                {KTP}, {KTP_AKT}
        });
    }

    public AssessmentFinancesSummaryViewModelTest(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    @Test
    public void isKtpCompetition() {
        int assessmentId = 1;
        int applicationId = 2;
        int daysLeft = 3;
        int daysLeftPercentage = 4;
        String applicationName = "applicationName";

        AssessmentFinancesSummaryViewModel viewModel = new AssessmentFinancesSummaryViewModel(assessmentId, applicationId,
                applicationName, daysLeft, daysLeftPercentage, fundingType, null, null,
                null,false, false);

        assertTrue(viewModel.isKtpCompetition());
    }
}
