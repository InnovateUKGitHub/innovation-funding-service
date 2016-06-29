package com.worth.ifs.bitbucket.plugin.hook;

import com.atlassian.bitbucket.scm.pull.MergeRequest;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

public class FlywayToFromVersionCallBackTest {
    @Test
    public void testNoPatchingError() {
        final MergeRequest request = mock(MergeRequest.class);
        final FlywayToFromVersionCallBack callback = new FlywayToFromVersionCallBack(request);
        callback.onTo(asList(asList(1, 2, 3)));
        callback.onFrom(asList(asList(1, 2, 4)));
        verify(request, never()).veto(isA(String.class), isA(String.class));
    }

    @Test
    public void testPatchingErrorSameLevel() {
        final MergeRequest request = mock(MergeRequest.class);
        final FlywayToFromVersionCallBack callback = new FlywayToFromVersionCallBack(request);
        callback.onTo(asList(asList(1, 2, 3)));
        callback.onFrom(asList(asList(1, 2, 3)));
        verify(request, times(1)).veto(isA(String.class), isA(String.class));
    }

    @Test
    public void testPatchingErrorLowerThanCurrent() {
        final MergeRequest request = mock(MergeRequest.class);
        final FlywayToFromVersionCallBack callback = new FlywayToFromVersionCallBack(request);
        callback.onTo(asList(asList(1, 2, 3)));
        callback.onFrom(asList(asList(1, 2, 2)));
        verify(request, times(1)).veto(isA(String.class), isA(String.class));
    }

    @Test
    public void testNoPatchingErroBecauserEmpty() {
        final MergeRequest request = mock(MergeRequest.class);
        final FlywayToFromVersionCallBack callback = new FlywayToFromVersionCallBack(request);
        callback.onTo(asList(asList(1, 2, 3)));
        callback.onFrom(asList());
        verify(request, never()).veto(isA(String.class), isA(String.class));
    }
}
