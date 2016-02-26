package com.worth.ifs.address.transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.address.mapper.PostcodeWebMapper;
import com.worth.ifs.address.resource.AddressResource;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Postcode web API implementation
 */
@Service
public class  PostcoderWeb extends BaseRestService implements AddressLookupService {

    @Value("${ifs.data.postcode-lookup.url}")
    private final String POSTCODE_LOOKUP_URL = null;

    @Value("${ifs.data.postcode-lookup.key}")
    private final String POSTCODE_LOOKUP_KEY = null;

    @Value("${ifs.data.postcode-lookup.lines}")
    private final String POSTCODE_LOOKUP_LINES = "3";

    @Value("${ifs.data.postcode-lookup.format}")
    private final String POSTCODE_LOOKUP_FORMAT = "json";

    @Value("${ifs.data.postcode-lookup.level}")
    private final String POSTCODE_LOOKUP_LEVEL = "address";

    @Value("${ifs.data.postcode-lookup.country}")
    private final String POSTCODE_LOOKUP_COUNTRY = "uk";

    private PostcodeWebMapper mapper = new PostcodeWebMapper();

    @Override
    public ServiceResult<List<AddressResource>> doLookup(String lookup) {

        if(StringUtils.isEmpty(lookup)) {
            return ServiceResult.serviceSuccess(new ArrayList<>());
        } else if(StringUtils.isEmpty(POSTCODE_LOOKUP_URL) || StringUtils.isEmpty(POSTCODE_LOOKUP_KEY)) {
            return ServiceResult.serviceSuccess(exampleAddresses());
        } else {
            return doAPILookup(lookup);
        }
    }

    private ServiceResult<List<AddressResource>> doAPILookup(String lookup) {
        try {
            String lookupURL = getLookupURL(lookup);
            setDataRestServiceUrl(lookupURL);
            return getWithRestResult("", JsonNode.class).toServiceResult().andOnSuccessReturn(mapper::mapToResources);
        } catch (URISyntaxException e) {
            return ServiceResult.serviceFailure(new Error(e.getReason(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    private <R> R mapToAddressResource(JsonNode jsonNode) {
        return null;
    }

    private String getLookupURL(String lookup) throws URISyntaxException {
        URIBuilder uriBuilder = null;
        uriBuilder = new URIBuilder(POSTCODE_LOOKUP_URL);
        uriBuilder.setPath(uriBuilder.getPath() + "/" + POSTCODE_LOOKUP_KEY + "/" + POSTCODE_LOOKUP_LEVEL + "/" + POSTCODE_LOOKUP_COUNTRY + "/" + lookup);
        uriBuilder.addParameter("format", POSTCODE_LOOKUP_FORMAT);
        uriBuilder.addParameter("lines", POSTCODE_LOOKUP_LINES);
        return uriBuilder.build().toString();
    }

    private List<AddressResource> exampleAddresses() {
        List<AddressResource> addresses = new ArrayList<>();
        addresses.add(new AddressResource(
                "Montrose House 1",
                "Clayhill Park",
                "Cheshire West and Chester",
                "Neston",
                "Cheshire",
                "CH64 3RU"
        ));
        addresses.add(new AddressResource(
                "Montrose House",
                "Clayhill Park",
                "Cheshire West and Chester",
                "Neston",
                "Cheshire",
                "CH64 3RU"
        ));
        return addresses;
    }
}
