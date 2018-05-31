package org.innovateuk.ifs;

import org.junit.Before;
import org.mockito.MockitoAnnotations;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.clearUniqueIds;

public class BaseUnitTest {

    @Before
    public void setup() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        // start with fresh ids when using builders
        clearUniqueIds();
    }
}
