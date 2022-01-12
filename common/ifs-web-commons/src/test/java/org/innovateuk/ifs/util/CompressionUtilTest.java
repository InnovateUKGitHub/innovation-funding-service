package org.innovateuk.ifs.util;

import org.junit.Test;

import java.io.IOException;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CompressionUtilTest {

    private static final Random random = new Random();

    @Test
    public void testCompression() throws IOException {
        String generatedString = random.ints(1212L)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
        String compressed = CompressionUtil.getCompressedString(generatedString);
        String reversed = CompressionUtil.getDecompressedString(compressed);
        assertThat(generatedString, equalTo(reversed));
    }

}