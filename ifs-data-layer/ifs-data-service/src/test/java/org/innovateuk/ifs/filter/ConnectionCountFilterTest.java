package org.innovateuk.ifs.filter;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ConnectionCountFilterTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private ConnectionCountFilter filter = new ConnectionCountFilter();

    @Spy
    private AtomicInteger count;

    @Mock
    private FilterChain filterChainMock;

    private MockHttpServletRequest requestMock = new MockHttpServletRequest(new MockServletContext());
    private MockHttpServletResponse responseMock = new MockHttpServletResponse();

    @Before
    public void setup() {
        ReflectionTestUtils.setField(filter, "max", 2);
    }

    @Test
    public void testIncreasingIncomingConnectionsIncreasesConnectionCount() throws IOException, ServletException {

        filterRecursively(3, 0, 2);

        verify(filterChainMock, times(3)).doFilter(requestMock, responseMock);
    }

    /**
     * Call doFilterInternal() on the filter and block when it hits the internal filter.doFilter() call to pass the request
     * along the chain.  At this point we can inspect the current incoming connections count within the ConnectionCountFilter
     * (using our Spy).  We expect every time we block and issue a new doFilterInternal() to increase the number of incoming
     * connections.
     *
     * We also expect the number of connections to reduce after the doFilterInternal() invocation has finished.
     */
    private void filterRecursively(int numberOfIncomingConnectionsToProduce, int currentIncomingConnections,
                                   int expectedNumberOfConnectionsToBeUnhealthy) throws IOException, ServletException {

        assertIncomingConnections(currentIncomingConnections);

        // check whether or not the filter reports as being able to accept more connections
        if (currentIncomingConnections < expectedNumberOfConnectionsToBeUnhealthy) {
            assertTrue(filter.canAcceptConnection());
        } else {
            assertFalse(filter.canAcceptConnection());
        }

        doAnswer(invocation -> {

            int nextIncomingConnections = currentIncomingConnections + 1;

            if (nextIncomingConnections < numberOfIncomingConnectionsToProduce) {
                filterRecursively(numberOfIncomingConnectionsToProduce, nextIncomingConnections, expectedNumberOfConnectionsToBeUnhealthy);
            }

            return null;

        }).when(filterChainMock).doFilter(requestMock, responseMock);

        doFilter();

        assertIncomingConnections(currentIncomingConnections);
    }

    private void assertIncomingConnections(int expectedIncoming) {
        assertEquals(expectedIncoming, count.get());
    }

    private void doFilter() throws IOException, ServletException {
        filter.doFilterInternal(requestMock, responseMock, filterChainMock);
    }
}
