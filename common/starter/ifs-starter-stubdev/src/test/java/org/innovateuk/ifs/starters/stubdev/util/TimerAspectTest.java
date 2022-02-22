package org.innovateuk.ifs.starters.stubdev.util;

import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.starters.stubdev.service.TestService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

public class TimerAspectTest {

    @Test
    public void testTimerAspect() {
        try (MockedStatic<LoggerFactory> logFactory = mockStatic(LoggerFactory.class)) {
            final Logger aspectLogger = mock(Logger.class);
            logFactory.when(() -> LoggerFactory.getLogger(TimerAspect.class)).thenReturn(aspectLogger);
            logFactory.when(() -> LogFactory.getLog(anyString())).thenReturn(mock(Logger.class));

            AspectJProxyFactory factory = new AspectJProxyFactory(new TestService());
            factory.addAspect(new TimerAspect());
            TestService proxy = factory.getProxy();
            proxy.testMethod();

            ArgumentCaptor<String> logCapture = ArgumentCaptor.forClass(String.class);
            verify(aspectLogger, times(1)).info(logCapture.capture());
            assertThat(logCapture.getValue().startsWith("StopWatch"), equalTo(true));
            assertThat(logCapture.getValue().contains(TestService.class.getSimpleName()), equalTo(true));
        }
    }

}