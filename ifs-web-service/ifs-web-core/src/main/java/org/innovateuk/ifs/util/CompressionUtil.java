package org.innovateuk.ifs.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static java.nio.charset.Charset.defaultCharset;
import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;
import static org.apache.commons.io.IOUtils.closeQuietly;

public class CompressionUtil {

    private static final Log LOG = LogFactory.getLog(MessageUtil.class);

    public static String getCompressedString(String rawString) {
        String compressedString = "";

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream zos = new GZIPOutputStream(baos);
            zos.write(rawString.getBytes());
            closeQuietly(zos);
            compressedString = encodeBase64String(baos.toByteArray());
        } catch (IOException e) {
            LOG.error(e);
        }
        return compressedString;
    }

    public static String getDecompressedString(String compressedString) {
        GZIPInputStream zis = null;
        String decompressedString = "";

        try {
            byte[] bytes = decodeBase64(compressedString);
            zis = new GZIPInputStream(new ByteArrayInputStream(bytes));
            decompressedString = IOUtils.toString(zis, defaultCharset());
        } catch (IOException e) {
            LOG.error(e);
        } finally {
            closeQuietly(zis);
        }
        return decompressedString;
    }
}
