package com.worth.ifs.bitbucket.plugin.hook;

import com.atlassian.bitbucket.scm.pull.MergeRequest;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.tuple.Pair.of;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

public class FlywayToFromVersionCallBackTest {
    @Test
    public void testNoPatchingError() {
        final MergeRequest request = mock(MergeRequest.class);
        final FlywayToFromVersionCallBack callback = new FlywayToFromVersionCallBack(request);
        callback.onTo(asList(of("V1_2_3__Patch.sql", asList(1, 2, 3)), of("V1_2_4__Patch.sql", asList(1, 2, 4))));
        callback.onFrom(asList(of("V1_2_3__Patch.sql", asList(1, 2, 3)), of("V1_2_4__Patch.sql", asList(1, 2, 4)), of("V1_2_5__NewPatch.sql", asList(1, 2, 5))));
        verify(request, never()).veto(isA(String.class), isA(String.class));
    }

    @Test
    public void testPatchingErrorSameLevel() {
        final MergeRequest request = mock(MergeRequest.class);
        final FlywayToFromVersionCallBack callback = new FlywayToFromVersionCallBack(request);
        callback.onTo(asList(of("V1_2_3__Patch.sql", asList(1, 2, 3)), of("V1_2_4__NewToPatch.sql", asList(1, 2, 4))));
        callback.onFrom(asList(of("V1_2_3__Patch.sql", asList(1, 2, 3)), of("V1_2_4__NewFromPatch.sql", asList(1, 2, 4))));
        verify(request, times(1)).veto(isA(String.class), isA(String.class));
    }

    @Test
    public void testPatchingErrorLowerThanCurrent() {
        final MergeRequest request = mock(MergeRequest.class);
        final FlywayToFromVersionCallBack callback = new FlywayToFromVersionCallBack(request);
        callback.onTo(asList(of("V1_2_3__Patch.sql", asList(1, 2, 3)), of("V1_2_5__NewToPatch.sql", asList(1, 2, 5))));
        callback.onFrom(asList(of("V1_2_3__Patch.sql", asList(1, 2, 3)), of("V1_2_4__NewFromPatch.sql", asList(1, 2, 4))));
        verify(request, times(1)).veto(isA(String.class), isA(String.class));
    }

}
