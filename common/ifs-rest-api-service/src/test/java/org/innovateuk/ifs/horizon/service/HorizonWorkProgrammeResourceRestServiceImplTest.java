package org.innovateuk.ifs.horizon.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgrammeResource;
import org.junit.Test;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleJoiner;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class HorizonWorkProgrammeResourceRestServiceImplTest extends BaseRestServiceUnitTest<HorizonWorkProgrammeRestServiceImpl> {

    private static final String REST_URL = "/horizon-work-programme";

    @Override
    protected HorizonWorkProgrammeRestServiceImpl registerRestServiceUnderTest() {
        return new HorizonWorkProgrammeRestServiceImpl();
    }

    @Test
    public void updateWorkProgrammeForApplication() {
        long applicationId = 1L;
        List<HorizonWorkProgrammeResource> programmes = new ArrayList();
        HorizonWorkProgrammeResource workProgramme = new HorizonWorkProgrammeResource(1, "CL2", true);
        HorizonWorkProgrammeResource callerId = new HorizonWorkProgrammeResource(15, "HORIZON-CL2-2021-DEMOCRACY-01", workProgramme, true);
        programmes.add(workProgramme);
        programmes.add(callerId);

        String baseUrl = format("%s/%s/%s", REST_URL, "update-work-programmes", applicationId);
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("workProgrammeIds",programmes.stream().map(p -> Long.toString(p.getId())).collect(Collectors.joining(",")));

        setupPostWithRestResultExpectations(builder.toUriString(), OK);

        RestResult<Void> voidRestResult = service.updateWorkProgrammeForApplication(programmes, applicationId);
        assertTrue(voidRestResult.isSuccess());
    }
}