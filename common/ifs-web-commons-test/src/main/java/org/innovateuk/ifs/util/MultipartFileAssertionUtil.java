package org.innovateuk.ifs.util;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MultipartFileAssertionUtil {

    /**
     * The mock and https response data types vary depending on spring versions.
     * Looks like a spring mock/test bug in 2.4.1 as it isn't in 2.4.3
     */
    public static void assertMultipartFile(MockMultipartFile expected, MultipartFile multipartFile) throws IOException {
        // this is ok for some versions
        // assertEquals(expected, multipartFile);
        // other versions you need to verify the contents
        assertEquals(expected.getOriginalFilename(), multipartFile.getOriginalFilename());
        assertEquals(expected.getName(), multipartFile.getName());
        assertEquals(expected.getSize(), multipartFile.getSize());
        assertTrue(Arrays.equals(expected.getBytes(), multipartFile.getBytes()));
        assertEquals(expected.getContentType(), multipartFile.getContentType());
    }

}
