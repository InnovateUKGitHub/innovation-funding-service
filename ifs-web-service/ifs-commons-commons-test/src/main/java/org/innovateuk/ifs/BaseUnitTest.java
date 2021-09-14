package org.innovateuk.ifs;

import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.junit.Before;
import org.mockito.MockitoAnnotations;

public abstract class BaseUnitTest {

    @Before
    public void setup() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        // start with fresh ids when using builders
        BaseBuilderAmendFunctions.clearUniqueIds();
    }
}
