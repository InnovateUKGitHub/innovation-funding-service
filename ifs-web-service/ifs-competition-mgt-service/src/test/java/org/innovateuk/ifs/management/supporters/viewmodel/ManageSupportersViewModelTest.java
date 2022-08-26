package org.innovateuk.ifs.management.supporters.viewmodel;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP_AKT;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class ManageSupportersViewModelTest {

    private final FundingType fundingType;

    @Parameterized.Parameters(name = "{index}: FundingType->{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[] [] {
                {KTP}, {KTP_AKT}
        });
    }

    public ManageSupportersViewModelTest(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    @Test
    public void isAllocateLinkEnabled() {
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(fundingType)
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .build();

        ManageSupportersViewModel viewModel = new ManageSupportersViewModel(competition);

        assertTrue(viewModel.isAllocateLinkEnabled());
    }
}
