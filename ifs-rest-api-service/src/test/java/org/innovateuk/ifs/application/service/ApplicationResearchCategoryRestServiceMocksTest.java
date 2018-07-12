package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static java.lang.String.format;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ApplicationResearchCategoryRestServiceMocksTest extends
        BaseRestServiceUnitTest<ApplicationResearchCategoryRestServiceImpl> {

    private static final String applicationResearchCategoryRestUrl = "/applicationResearchCategory";

    @Override
    protected ApplicationResearchCategoryRestServiceImpl registerRestServiceUnderTest() {
        return new ApplicationResearchCategoryRestServiceImpl();
    }

    @Test
    public void setResearchCategory() {
        long researchCategoryId = 1L;

        ApplicationResource applicationResource = newApplicationResource().build();

        setupPostWithRestResultExpectations(format("%s/researchCategory/%s", applicationResearchCategoryRestUrl,
                applicationResource.getId()),
                ApplicationResource.class, researchCategoryId, applicationResource, HttpStatus.OK);

        RestResult<ApplicationResource> result = service.setResearchCategory(applicationResource.getId(),
                researchCategoryId);

        assertTrue(result.isSuccess());
        assertEquals(applicationResource, result.getSuccess());
    }

    @Test
    public void setResearchCategoryAndMarkAsComplete() {
        long researchCategoryId = 1L;
        long markedAsCompleteId = 2L;

        ApplicationResource applicationResource = newApplicationResource().build();

        setupPostWithRestResultExpectations(format("%s/mark-research-category-complete/%s/%s",
                applicationResearchCategoryRestUrl, applicationResource.getId(), markedAsCompleteId),
                ApplicationResource.class, researchCategoryId, applicationResource, HttpStatus.OK);

        RestResult<ApplicationResource> result = service.setResearchCategoryAndMarkAsComplete(applicationResource
                .getId(), markedAsCompleteId, researchCategoryId);

        assertTrue(result.isSuccess());
        assertEquals(applicationResource, result.getSuccess());
    }
}
