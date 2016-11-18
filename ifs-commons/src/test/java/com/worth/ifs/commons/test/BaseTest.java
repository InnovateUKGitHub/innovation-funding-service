package com.worth.ifs.commons.test;

import org.junit.Before;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.clearUniqueIds;

/**
 * The absolute base class of all tests
 */
public class BaseTest {

    @Before
    public void resetBuilderIds() {
        clearUniqueIds();
    }
}
