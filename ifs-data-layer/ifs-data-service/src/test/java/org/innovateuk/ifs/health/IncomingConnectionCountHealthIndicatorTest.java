package org.innovateuk.ifs.health;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.filter.ConnectionCountFilter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.boot.actuate.health.Health;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IncomingConnectionCountHealthIndicatorTest extends BaseUnitTestMocksTest {

    @Mock
    private ConnectionCountFilter filter;

    private IncomingConnectionCountHealthIndicator indicator;

    @Before
    public void setup() {
        indicator = new IncomingConnectionCountHealthIndicator(filter);
    }

    @Test
    public void testFilterReportsCanAcceptConnections() {

        when(filter.canAcceptConnection()).thenReturn(true);
        assertEquals(Health.up().build(), indicator.health());
        verify(filter).canAcceptConnection();
    }

    @Test
    public void testFilterReportsCannotAcceptConnections() {

        when(filter.canAcceptConnection()).thenReturn(false);
        assertEquals(Health.outOfService().build(), indicator.health());
        verify(filter).canAcceptConnection();
    }
}
