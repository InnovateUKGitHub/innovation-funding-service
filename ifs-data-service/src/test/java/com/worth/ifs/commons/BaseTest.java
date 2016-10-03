package com.worth.ifs.commons;

import org.junit.Before;

import static com.worth.ifs.BuilderAmendFunctions.clearUniqueIds;

/**
 * The absolute base class of all tests
 */
public class BaseTest {

    @Before
    public void resetBuilderIds() {
        clearUniqueIds();
    }
}
