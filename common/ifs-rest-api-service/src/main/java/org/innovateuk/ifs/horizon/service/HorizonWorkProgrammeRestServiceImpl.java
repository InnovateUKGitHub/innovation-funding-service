package org.innovateuk.ifs.horizon.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.horizon.resource.ApplicationHorizonWorkProgrammeResource;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.applicationHorizonWorkProgrammeResourceListType;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleJoiner;

@Service
public class HorizonWorkProgrammeRestServiceImpl extends BaseRestService implements HorizonWorkProgrammeRestService {

    private static final String REST_URL = "/horizon-work-programme";

    @Override
    public RestResult<Void> updateWorkProgrammeForApplication(List<HorizonWorkProgramme> selectedProgrammes, long applicationId) {
        String baseUrl = format("%s/%s/%s", REST_URL, "update-work-programmes", applicationId);
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("workProgrammes", simpleJoiner(selectedProgrammes, ","));
        return postWithRestResult(builder.toUriString(), Void.class);
    }

    @Override
    public RestResult<List<ApplicationHorizonWorkProgrammeResource>> findSelected(long applicationId) {
        return getWithRestResult(REST_URL + "/find-selected/" + applicationId,
                applicationHorizonWorkProgrammeResourceListType());
    }
}
