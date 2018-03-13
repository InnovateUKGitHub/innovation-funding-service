package org.innovateuk.ifs.security;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

@Component
public class HashBasedMacTokenHandler {

    private Mac mac;

    private static final String ALGORITHM = "HmacSHA256";

    public HashBasedMacTokenHandler() {
        try {
            mac = Mac.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to initialise MAC object: " + e.getMessage(), e);
        }
    }

    public String calculateHash(String key, String input) throws InvalidKeyException {
        mac.reset();
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
