package org.innovateuk.ifs.monitoring;

import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Random;

@Component
public class ReadinessEndpoint implements Endpoint<ResponseEntity<Map<String, String>>> {

    @Override
    public String getId() {
        return "ready";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isSensitive() {
        return false;
    }

    private boolean isReady() {
        // TODO: return false if the service should not receive more requests at this time
        return true;
    }

    @Override
    public ResponseEntity<Map<String, String>> invoke() {
        if (!isReady()) {
            return new ResponseEntity<Map<String, String>>(
                    Collections.singletonMap("message", "Service unavailable"),
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity<Map<String, String>>(
                Collections.singletonMap("message", "OK"),
                HttpStatus.OK);
    }
}
