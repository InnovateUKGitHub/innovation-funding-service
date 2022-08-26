package org.innovateuk.ifs.management.assessor.viewmodel;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import java.util.Arrays;
import java.util.Collection;

import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP_AKT;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class InviteAssessorsViewModelTest {

    private final FundingType fundingType;

    @Parameterized.Parameters(name = "{index}: FundingType->{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[] [] {
                {KTP}, {KTP_AKT}
        });
    }

    public InviteAssessorsViewModelTest(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    @Test
    public void isKtp() {
        InviteAssessorsViewModel inviteAssessorsViewModel = Mockito.mock(InviteAssessorsViewModel.class,
                InvocationOnMock::callRealMethod);

        inviteAssessorsViewModel.setFundingType(fundingType);

        assertTrue(inviteAssessorsViewModel.isKtp());
    }
}
