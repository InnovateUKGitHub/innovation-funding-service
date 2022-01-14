package org.innovateuk.ifs.util;

import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Slf4j
public final class CompressionUtil {

    private CompressionUtil() {
    }

    public static String getCompressedString(String rawString) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    GZIPOutputStream zos = new GZIPOutputStream(baos)) {
            zos.write(rawString.getBytes());
            zos.close(); // required
            return Base64.encodeBase64String(baos.toByteArray());
        }
    }

    public static String getDecompressedString(String compressedString) throws IOException {
        byte[] bytes = Base64.decodeBase64(compressedString);
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                    GZIPInputStream zis = new GZIPInputStream(bais);
                            Reader reader = new InputStreamReader(zis)) {
            return CharStreams.toString(reader);
        }
    }

}
