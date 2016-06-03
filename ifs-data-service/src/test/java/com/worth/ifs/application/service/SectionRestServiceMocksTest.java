package com.worth.ifs.application.service;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.longsListType;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.application.resource.SectionType;
import com.worth.ifs.application.resource.SectionResource;

/**
 * Tests to check the ApplicationRestService's interaction with the RestTemplate and the processing of its results
 */
public class SectionRestServiceMocksTest extends BaseRestServiceUnitTest<SectionRestServiceImpl> {

    private static final String sectionRestUrl = "/section";

    @Override
    protected SectionRestServiceImpl registerRestServiceUnderTest() {
        SectionRestServiceImpl sectionRestService = new SectionRestServiceImpl();
        return sectionRestService;
    }

    @Test
    public void test_getCompletedSectionIds() {

        String expectedUrl = sectionRestUrl + "/getCompletedSections/123/456";
        List<Long> returnedResponse = asList(1L, 2L, 3L);

        setupGetWithRestResultExpectations(expectedUrl, longsListType(), returnedResponse);

        // now run the method under test
        List<Long> response = service.getCompletedSectionIds(123L, 456L).getSuccessObject();
        assertEquals(returnedResponse, response);
    }

    @Test
    public void test_getIncompleteSectionIds() {

        String expectedUrl = sectionRestUrl + "/getIncompleteSections/123";
        List<Long> returnedResponse = asList(1L, 2L, 3L);

        setupGetWithRestResultExpectations(expectedUrl, longsListType(), returnedResponse);

        // now run the method under test
        List<Long> response = service.getIncompletedSectionIds(123L).getSuccessObject();
        assertEquals(returnedResponse, response);
    }
    
    @Test
    public void testGetSectionsForCompetitionByType() {

        String expectedUrl = sectionRestUrl + "/getSectionsByCompetitionIdAndType/123/FINANCE";
        List<SectionResource> returnedResponse = Arrays.asList(new SectionResource());

        setupGetWithRestResultExpectations(expectedUrl, new ParameterizedTypeReference<List<SectionResource>>() {}, returnedResponse);

        List<SectionResource> response = service.getSectionsByCompetitionIdAndType(123L, SectionType.FINANCE).getSuccessObject();
        
        assertEquals(returnedResponse, response);
    }
    

}
