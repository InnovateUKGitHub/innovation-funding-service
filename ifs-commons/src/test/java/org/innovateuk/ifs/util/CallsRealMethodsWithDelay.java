package org.innovateuk.ifs.util;

import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.CallsRealMethods;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Stubber;

public class CallsRealMethodsWithDelay extends CallsRealMethods {
    private final long delay;

    public CallsRealMethodsWithDelay(long delay) {
        this.delay = delay;
    }

    public Object answer(InvocationOnMock invocation) throws Throwable {
        Thread.sleep(delay);
        return super.answer(invocation);
    }

    public static Stubber doAnswerWithRealMethodAndDelay(long delay) {
        return Mockito.doAnswer(new CallsRealMethodsWithDelay(delay));
    }
}
