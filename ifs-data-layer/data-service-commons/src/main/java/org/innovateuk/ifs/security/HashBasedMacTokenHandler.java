package org.innovateuk.ifs.security;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
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

    public String calculateHash(String key, String data) throws InvalidKeyException {
        return calculateHash(key, data.getBytes());
    }

    public String calculateHash(String key, byte[] data) throws InvalidKeyException {
        mac.reset();
        mac.init(new SecretKeySpec(key.getBytes(), ALGORITHM));
        return Hex.encodeHexString(mac.doFinal(data));
    }
}
