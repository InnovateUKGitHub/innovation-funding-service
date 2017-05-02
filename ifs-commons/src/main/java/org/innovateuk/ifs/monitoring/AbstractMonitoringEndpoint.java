package org.innovateuk.ifs.monitoring;

import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

public abstract class AbstractMonitoringEndpoint implements Endpoint<ResponseEntity<Map<String, String>>> {

    @Override
    public String getId() {
        return "abstract-monitor";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isSensitive() {
        return false;
    }

    abstract protected boolean isReady();

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
