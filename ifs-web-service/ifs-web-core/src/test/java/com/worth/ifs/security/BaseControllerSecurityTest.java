package com.worth.ifs.security;

import au.com.bytecode.opencsv.CSVWriter;
import com.worth.ifs.commons.security.BaseDocumentingSecurityTest;

import java.io.FileWriter;
import java.io.IOException;

/**
 * A base class for testing services with Spring Security integrated into them.  PermissionRules-annotated beans are
 * made available as mocks so that we can test the effects of calling service methods against the PermissionRule methods
 * that are available.
 *
 * Calls for Service methods and the associated Permission Rule methods that are called as a result are recorded and output
 * to a CSV report as a standard part of the testing process.
 *
 * Subclasses of this base class are therefore able to test the security annotations of their various methods by verifying
 * that individual PermissionRule methods are being called (on their owning mocks) and the same verifications auto-documented
 */
public abstract class BaseControllerSecurityTest<T> extends BaseDocumentingSecurityTest<T> {

    public static final String CONTROLLER_DOCUMENTATION_FILENAME = "build/controller-calls-and-permission-rules.csv";

    /**
     * A static initialization block that will ensure that we start any BaseServiceSecurityTest subclasses with a fresh
     * CSV report file to append to
     */
    static {

        try (FileWriter fileWriter = new FileWriter(CONTROLLER_DOCUMENTATION_FILENAME)) {
            CSVWriter writer = new CSVWriter(fileWriter, '\t');
            writer.writeNext(new String[]{"Controller call", "Permission rules checked"});
        } catch (IOException e) {
            throw new RuntimeException("Unable to create csv for controller documentation");
        }
    }

    @Override
    protected String getDocumentationFilename() {
        return CONTROLLER_DOCUMENTATION_FILENAME;
    }
}
