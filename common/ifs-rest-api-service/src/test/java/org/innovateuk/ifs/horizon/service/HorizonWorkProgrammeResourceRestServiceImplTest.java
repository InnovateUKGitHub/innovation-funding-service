package org.innovateuk.ifs.horizon.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgrammeResource;
import org.junit.Test;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

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

        String baseUrl = format("%s/%s/%s", REST_URL, "update-work-programmes", applicationId);
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("workProgrammes", simpleJoiner(programmes, ","));

        setupPostWithRestResultExpectations(builder.toUriString(), OK);

        RestResult<Void> voidRestResult = service.updateWorkProgrammeForApplication(programmes, applicationId);
        assertTrue(voidRestResult.isSuccess());
    }
}