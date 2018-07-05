package org.innovateuk.ifs.address.transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;
import org.innovateuk.ifs.address.mapper.PostcodeWebMapper;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.PostcodeWebAddress;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Postcode web API implementation
 */
@Service
public class PostcoderWeb implements AddressLookupService {
    private static final Log LOG = LogFactory.getLog(PostcoderWeb.class);

    @Value("${ifs.data.postcode-lookup.url}")
    private String postcodeLookupUrl = null;

    @Value("${ifs.data.postcode-lookup.key}")
    private String postcodeLookupKey = null;

    @Value("${ifs.data.postcode-lookup.lines}")
    private String postcodeLookupLines = "3";

    @Value("${ifs.data.postcode-lookup.format}")
    private String postcodeLookupFormat = "json";

    @Value("${ifs.data.postcode-lookup.level}")
    private String postcodeLookupLevel = "address";

    @Value("${ifs.data.postcode-lookup.validation}")
    private String postcodeLookupValidation = "validationpostcode";

    @Value("${ifs.data.postcode-lookup.country}")
    private String postcodeLookupCountry = "uk";

    private PostcodeWebMapper mapper = new PostcodeWebMapper();

    @Override
    public ServiceResult<List<AddressResource>> doLookup(String lookup) {
        if (StringUtils.isEmpty(lookup)) {
            return ServiceResult.serviceSuccess(new ArrayList<>());
        } else if (StringUtils.isEmpty(postcodeLookupUrl) || StringUtils.isEmpty(postcodeLookupKey)) {
            return ServiceResult.serviceSuccess(exampleAddresses());
        } else {
            return doAPILookup(lookup);
        }
    }

    @Override
    public ServiceResult<Boolean> validatePostcode(String postcode) {
        if (StringUtils.isEmpty(postcode)) {
            return ServiceResult.serviceSuccess(true);
        } else if (StringUtils.isEmpty(postcodeLookupUrl) || StringUtils.isEmpty(postcodeLookupKey)) {
            return ServiceResult.serviceSuccess(true);
        } else {
            return doAPIPostcodeVerification(postcode);
        }
    }

    private ServiceResult<List<AddressResource>> doAPILookup(String lookup) {
        try {
            URI lookupURL = getLookupURL(lookup);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<PostcodeWebAddress[]> responseEntity = restTemplate.getForEntity(lookupURL, PostcodeWebAddress[].class);
            if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
                return ServiceResult.serviceSuccess(mapper.toResources(responseEntity.getBody()));
            } else {
                String failure;
                if (responseEntity != null) {
                    failure = responseEntity.toString();
                } else {
                    failure = null;
                }
                LOG.error(failure);
                return ServiceResult.serviceFailure(new Error(failure, HttpStatus.INTERNAL_SERVER_ERROR));
            }
        } catch (HttpClientErrorException cle) {
            LOG.error(cle);
            return ServiceResult.serviceFailure(new Error(cle.getMessage(), cle.getStatusCode()));
        } catch (URISyntaxException e) {
            LOG.error(e);
            return ServiceResult.serviceFailure(new Error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    private ServiceResult<Boolean> doAPIPostcodeVerification(String postcode) {
        try {
            URI verificationURL = getPostcodeVerificationURL(postcode);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Boolean> responseEntity = restTemplate.getForEntity(verificationURL, Boolean.class);

            if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
                return ServiceResult.serviceSuccess(responseEntity.getBody());
            } else {
                String failure;
                if (responseEntity != null) {
                    failure = responseEntity.toString();
                } else {
                    failure = null;
                }
                LOG.error(failure);
                return ServiceResult.serviceFailure(new Error(failure, HttpStatus.INTERNAL_SERVER_ERROR));
            }
        } catch (HttpClientErrorException cle) {
            LOG.error(cle);
            return ServiceResult.serviceFailure(new Error(cle.getMessage(), cle.getStatusCode()));
        } catch (URISyntaxException e) {
            LOG.error(e);
            return ServiceResult.serviceFailure(new Error(e.getReason(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    private URI getLookupURL(String lookup) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(postcodeLookupUrl);
        uriBuilder.setPath(uriBuilder.getPath() + "/" + postcodeLookupKey + "/" + postcodeLookupLevel + "/" + postcodeLookupCountry + "/" + lookup);
        uriBuilder.addParameter("format", postcodeLookupFormat);
        uriBuilder.addParameter("lines", postcodeLookupLines);
        return uriBuilder.build();
    }

    private URI getPostcodeVerificationURL(String postcode) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(postcodeLookupUrl + postcodeLookupValidation + "/" + postcode);
        return uriBuilder.build();
    }

    private static List<AddressResource> exampleAddresses() {
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
