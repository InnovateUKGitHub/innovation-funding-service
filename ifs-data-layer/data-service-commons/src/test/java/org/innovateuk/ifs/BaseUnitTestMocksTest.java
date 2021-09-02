package org.innovateuk.ifs;

import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.junit.Before;
import org.mockito.MockitoAnnotations;

/**
 * This is a convenience subclass that initialises mocks for all tests that require it.
 */
public abstract class BaseUnitTestMocksTest {

    @Before
    public void setupMockInjection() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
        BaseBuilderAmendFunctions.clearUniqueIds();
    }
}
