package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;

/**
 * TODO: Add description
 */
public class ApplicationInnovationAreaRestServiceMocksTest extends BaseRestServiceUnitTest<ApplicationInnovationAreaRestServiceImpl> {
    private static final String applicationInnovationAreaRestUrl = "/applicationInnovationArea";

    @Override
    protected ApplicationInnovationAreaRestServiceImpl registerRestServiceUnderTest() {
        ApplicationInnovationAreaRestServiceImpl applicationInnovationAreaRestServiceImpl = new ApplicationInnovationAreaRestServiceImpl();
        return applicationInnovationAreaRestServiceImpl;
    }

    @Test
    public void testSaveApplicationInnovationAreaChoice() {
        Long innovationAreaId = 123L;
        Long applicationId = 321L;
        String expectedUrl = applicationInnovationAreaRestUrl + "/innovationArea/" + 321;

        ApplicationResource applicationResource = newApplicationResource().build();

        setupPostWithRestResultExpectations(expectedUrl, ApplicationResource.class, innovationAreaId, applicationResource, HttpStatus.OK);

        RestResult<ApplicationResource> result = service.saveApplicationInnovationAreaChoice(applicationId, innovationAreaId);
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void setSetApplicationInnovationAreaNotApplicable() {
        Long applicationId = 321L;
        String expectedUrl = applicationInnovationAreaRestUrl + "/noInnovationAreaApplicable/" + 321;

        ApplicationResource applicationResource = newApplicationResource().build();

        setupPostWithRestResultExpectations(expectedUrl, ApplicationResource.class, null, applicationResource, HttpStatus.OK);

        RestResult<ApplicationResource> result = service.setApplicationInnovationAreaToNotApplicable(applicationId);
    }

    @Test
    public void testGetAvailableInnovationAreas() {
        Long applicationId = 321L;
        String expectedUrl = applicationInnovationAreaRestUrl + "/availableInnovationAreas/" + 321;

        List<InnovationAreaResource> innovationAreaResourceList = newInnovationAreaResource().build(5);

        setupGetWithRestResultExpectations(expectedUrl, ParameterizedTypeReferences.innovationAreaResourceListType(), innovationAreaResourceList, HttpStatus.OK);

        RestResult<List<InnovationAreaResource>> result = service.getAvailableInnovationAreasForApplication(applicationId);
    }
}
