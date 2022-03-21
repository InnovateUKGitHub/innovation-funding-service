package org.innovateuk.ifs.applicant.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.heukar.resource.ApplicationHeukarLocationResource;
import org.innovateuk.ifs.heukar.resource.HeukarLocation;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.applicationHeukarLocationResourceListType;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleJoiner;

@Service
public class ApplicationHeukarLocationRestServiceImpl extends BaseRestService implements ApplicationHeukarLocationRestService {

    private static final String APPLICATION_HEUKAR_LOCATION_REST_URL = "/heukar-project-location";

    @Override
    public RestResult<Void> updateLocationsForApplication(List<HeukarLocation> selectedLocations, long applicationId) {
        String baseUrl = format("%s/%s/%s", APPLICATION_HEUKAR_LOCATION_REST_URL, "update-locations", applicationId);
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("locations", simpleJoiner(selectedLocations, ","));
        return postWithRestResult(builder.toUriString(), Void.class);
    }

    @Override
    public RestResult<List<ApplicationHeukarLocationResource>> findAllWithApplicationId(long applicationId) {
        return getWithRestResult(APPLICATION_HEUKAR_LOCATION_REST_URL + "/find-selected/" + applicationId,
                applicationHeukarLocationResourceListType());
    }
}
