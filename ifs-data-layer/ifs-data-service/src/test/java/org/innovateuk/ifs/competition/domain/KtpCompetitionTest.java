package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP_AKT;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class KtpCompetitionTest {

    private final FundingType fundingType;

    @Parameterized.Parameters(name = "{index}: FundingType->{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {KTP}, {KTP_AKT}
        });
    }

    public KtpCompetitionTest(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    @Test
    public void isKtp() {
        Competition competition = newCompetition()
                .withFundingType(fundingType)
                .build();

        assertTrue(competition.isKtp());
    }
}