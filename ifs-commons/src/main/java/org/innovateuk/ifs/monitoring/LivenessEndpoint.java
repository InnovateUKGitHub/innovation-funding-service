package org.innovateuk.ifs.monitoring;

import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class LivenessEndpoint implements Endpoint<ResponseEntity<Map<String, String>>> {

    @Override
    public String getId() {
        return "live";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isSensitive() {
        return false;
    }

    @Override
    public ResponseEntity<Map<String, String>> invoke() {
        return new ResponseEntity<Map<String, String>>(
                Collections.singletonMap("message", "OK"),
                HttpStatus.OK);
    }
}
