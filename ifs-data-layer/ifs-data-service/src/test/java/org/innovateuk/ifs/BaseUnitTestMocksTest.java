package org.innovateuk.ifs;


import org.innovateuk.ifs.commons.test.BaseTest;
import org.junit.Before;
import org.mockito.MockitoAnnotations;

/**
 * This is a convenience subclass that initialises mocks for all tests that require it.
 */
public abstract class BaseUnitTestMocksTest extends BaseTest {


    @Before
    public void setupMockInjection() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
    }
}
