package org.innovateuk.ifs.security;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * Responsible for providing hash based message authentication codes using SHA-256 a given input in combination
 * with a secret key.
 */
@Component
public class HashBasedMacTokenHandler {

    private static final String ALGORITHM = "HmacSHA256";

    public String calculateHash(String key, String input) throws InvalidKeyException {
        Mac mac;
        try {
            mac = Mac.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to initialise MAC object: " + e.getMessage(), e);
        }
        mac.init(getKey(key));
        return Hex.encodeHexString(mac.doFinal(getInputAsByteArray(input)));
    }

    private Key getKey(String key) {
        byte[] keyAsBytes = key != null ? key.getBytes() : null;
        return new SecretKeySpec(keyAsBytes, ALGORITHM);
    }

    private byte[] getInputAsByteArray(String input) {
        return input != null ? input.getBytes() : null;
    }
}
