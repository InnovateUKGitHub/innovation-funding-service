package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;

/**
 * Tests to check the ResearchCategoryRestService's interaction with the RestTemplate and the processing of its results
 */
public class ApplicationResearchCategoryRestServiceMocksTest extends BaseRestServiceUnitTest<ApplicationResearchCategoryRestServiceImpl> {
    private static final String applicationResearchCategoryRestUrl = "/applicationResearchCategory";

    @Override
    protected ApplicationResearchCategoryRestServiceImpl registerRestServiceUnderTest() {
        ApplicationResearchCategoryRestServiceImpl applicationResearchCategoryRestServiceImpl = new ApplicationResearchCategoryRestServiceImpl();
        return applicationResearchCategoryRestServiceImpl;
    }

    @Test
    public void testSaveApplicationInnovationAreaChoice() {
        Long researchCategoryd = 123L;
        Long applicationId = 321L;
        String expectedUrl = applicationResearchCategoryRestUrl + "/researchCategory/" + 321;

        ApplicationResource applicationResource = newApplicationResource().build();

        setupPostWithRestResultExpectations(expectedUrl, ApplicationResource.class, researchCategoryd, applicationResource, HttpStatus.OK);

        RestResult<ApplicationResource> result = service.saveApplicationResearchCategoryChoice(applicationId, researchCategoryd);
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
