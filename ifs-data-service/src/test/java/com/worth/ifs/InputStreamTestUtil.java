package com.worth.ifs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
public class InputStreamTestUtil {

    public static boolean assertInputStreamContents(InputStream inputStream, String expectedContents) {
        try (InputStream retrievedInputStream = inputStream) {
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(retrievedInputStream))) {
                assertEquals(expectedContents, buffer.readLine());
            }
        } catch (IOException e) {
            fail("Error whilst validating input stream - " + e);
        }

        return true;
    }
}
