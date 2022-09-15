package org.innovateuk.ifs.project.finance.resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.innovateuk.ifs.project.finance.builder.FinanceCheckSummaryResourceBuilder.newFinanceCheckSummaryResource;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class KtpFinanceCheckSummaryResourceTest {

    private final boolean ktp;
    private final boolean expected;

    @Parameterized.Parameters(name = "{index}: ktp->{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[] [] {
                {true, true}, {false, false}
        });
    }

    public KtpFinanceCheckSummaryResourceTest(boolean ktp, boolean expected) {
        this.ktp = ktp;
        this.expected = expected;
    }

    @Test
    public void isKtp() {
        FinanceCheckSummaryResource resource = newFinanceCheckSummaryResource()
                .withKtp(ktp)
                .build();

        assertEquals(expected, resource.isKtp());
    }
}
