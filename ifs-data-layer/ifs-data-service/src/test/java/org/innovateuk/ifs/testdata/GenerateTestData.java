package org.innovateuk.ifs.testdata;


import org.springframework.test.annotation.IfProfileValue;

/**
 * Generates web test data based upon csvs in /src/test/resources/testdata using data builders
 */
@IfProfileValue(name = "testGroups", values = {"generatetestdata"})
public class GenerateTestData extends BaseGenerateTestData {

    @Override
    protected boolean cleanDbFirst() {
        return true;
    }

    /**
     * We might need to fix up the database before we start generating data.
     * This can happen if for example we have pushed something out to live that would make this generation step fail.
     * Note that if we make a fix is made here it will most likely need a corresponding fix in sql script
     * VX_Y_Z__Remove_old_competition.sql
     * To repeat the fix when running up the full flyway mechanism
     */
    @Override
    public void fixUpDatabase() {
    }
}
