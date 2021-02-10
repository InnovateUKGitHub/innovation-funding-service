package org.innovateuk.ifs.commons.test;

import org.junit.Before;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.clearUniqueIds;

/**
 * The absolute base class of all tests
 */
public abstract class BaseTest {

    @Before
    public void resetBuilderIds() {
        clearUniqueIds();
    }
}
