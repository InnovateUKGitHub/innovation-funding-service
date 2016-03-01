package com.worth.ifs.address.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.worth.ifs.address.resource.AddressResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapping results retrieved from the postcode web API to address resources.
 */
public class PostcodeWebMapper {

    public List<AddressResource> mapToResources(JsonNode addresses) {
        List<AddressResource> addressResources = new ArrayList<>();
        addresses.forEach(a -> addressResources.add(mapToResource(a)));
        return addressResources;
    }

    public AddressResource mapToResource(JsonNode address) {
        return new AddressResource(
                getValue(address, "addressline1"),
                getValue(address, "addressline2"),
                getValue(address, "addressline3"),
                getValue(address, "posttown"),
                getValue(address, "county"),
                getValue(address, "postcode")
        );
    }

    private String getValue(JsonNode jsonNode, String key) {
        if(jsonNode.hasNonNull(key)) {
            return jsonNode.get(key).asText();
        } else {
            return "";
        }
    }
}