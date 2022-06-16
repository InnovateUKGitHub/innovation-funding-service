package org.innovateuk.ifs.horizon.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.horizon.resource.ApplicationHorizonWorkProgrammeResource;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgrammeResource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static java.lang.String.format;
import static java.lang.String.join;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.horizonWorkProgrammeResourceListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.applicationHorizonWorkProgrammeResourceListType;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleJoiner;

@Service
public class HorizonWorkProgrammeRestServiceImpl extends BaseRestService implements HorizonWorkProgrammeRestService {

    private static final String REST_URL = "/horizon-work-programme";

    @Override
    public RestResult<HorizonWorkProgrammeResource> findWorkProgramme(Long workProgrammeId) {
        String baseUrl = format("%s/%s/%s", REST_URL, "id", workProgrammeId);
        return getWithRestResult(baseUrl, HorizonWorkProgrammeResource.class);
    }

    @Override
    public RestResult<List<HorizonWorkProgrammeResource>> findRootWorkProgrammes() {
        return getWithRestResult(REST_URL + "/root",
                horizonWorkProgrammeResourceListType());
    }

    @Override
    public RestResult<List<HorizonWorkProgrammeResource>> findChildrenWorkProgrammes(Long workProgrammeId) {
        String baseUrl = format("%s/%s/%s/children", REST_URL, "id", workProgrammeId);
        return getWithRestResult(baseUrl,
                horizonWorkProgrammeResourceListType());
    }

    @Override
    public RestResult<Void> updateWorkProgrammeForApplication(List<HorizonWorkProgrammeResource> selectedProgrammes, Long applicationId) {
        String baseUrl = format("%s/%s/%s", REST_URL, "update-work-programmes", applicationId);
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("workProgrammes", simpleJoiner(selectedProgrammes, ","));
        return postWithRestResult(builder.toUriString(), Void.class);
    }

    @Override
    public RestResult<List<ApplicationHorizonWorkProgrammeResource>> findSelected(Long applicationId) {
        return getWithRestResult(REST_URL + "/find-selected/" + applicationId,
                applicationHorizonWorkProgrammeResourceListType());
    }
}
