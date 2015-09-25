package com.worth.ifs.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Created by dwatson on 25/09/15.
 */
public class AutosaveElementException extends RuntimeException {

    private String inputIdentifier;
    private String value;
    private Long applicationId;

    public AutosaveElementException(String inputIdentifier, String value, Long applicationId) {
        this.inputIdentifier = inputIdentifier;
        this.value = value;
        this.applicationId = applicationId;
    }

    ObjectNode createJsonResponse() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("success", "false");
        node.put("inputIdentifier", inputIdentifier);
        node.put("value", value);
        node.put("applicationId", applicationId);
        return node;
    }


}
