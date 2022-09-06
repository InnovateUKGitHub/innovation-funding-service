package org.innovateuk.ifs.management.assessor.viewmodel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class InviteAssessorsViewModelTest {

    private final boolean ktp;
    private final boolean expected;

    @Parameterized.Parameters(name = "{index}: FundingType->{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[] [] {
                {true, true}, {false, false}
        });
    }

    public InviteAssessorsViewModelTest(boolean ktp, boolean expected) {
        this.ktp = ktp;
        this.expected = expected;
    }

    @Test
    public void isKtp() {
        InviteAssessorsViewModel inviteAssessorsViewModel = Mockito.mock(InviteAssessorsViewModel.class,
                InvocationOnMock::callRealMethod);

        inviteAssessorsViewModel.setKtp(ktp);

        assertEquals(expected, inviteAssessorsViewModel.isKtp());
    }
}
