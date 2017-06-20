package org.innovateuk.ifs.address.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.PostcodeWebAddress;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapping results retrieved from the postcode web API to address resources.
 */
public class PostcodeWebMapper {

    public List<AddressResource> mapJsonToResources(JsonNode addresses) {
        List<AddressResource> addressResources = new ArrayList<>();
        addresses.forEach(a -> addressResources.add(mapJsonToResource(a)));
        return addressResources;
    }

    public List<AddressResource> toResources(PostcodeWebAddress[] postcodeWebAddresses) {
        List<AddressResource> addressResources = new ArrayList<>();

        if(postcodeWebAddresses.length > 0) {
            for(PostcodeWebAddress postcodeWebAddress : postcodeWebAddresses) {
                addressResources.add(toResource(postcodeWebAddress));
            }
        }
        return addressResources;
    }

    public AddressResource toResource(PostcodeWebAddress postcodeWebAddress) {
            return new AddressResource(
                    postcodeWebAddress.getAddressline1(),
                    postcodeWebAddress.getAddressline2(),
                    postcodeWebAddress.getAddressline3(),
                    postcodeWebAddress.getPosttown(),
                    postcodeWebAddress.getCounty(),
                    postcodeWebAddress.getPostcode()
            );
    }

    public AddressResource mapJsonToResource(JsonNode address) {
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
