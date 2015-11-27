package com.worth.ifs.application.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.application.domain.Section;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.worth.ifs.application.builder.SectionBuilder.newSection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;

/**
 * Tests to check the ApplicationRestService's interaction with the RestTemplate and the processing of its results
 */
public class SectionRestServiceMocksTest extends BaseRestServiceUnitTest<SectionRestServiceImpl> {

    private static final String sectionRestUrl = "/section";

    @Override
    protected SectionRestServiceImpl registerRestServiceUnderTest() {
        SectionRestServiceImpl sectionRestService = new SectionRestServiceImpl();
        sectionRestService.sectionRestURL = sectionRestUrl;
        return sectionRestService;
    }

    @Test
    public void test_getCompletedSectionIds() {

        String expectedUrl = dataServicesUrl + sectionRestUrl + "/getCompletedSections/123/456";
        Long[] returnedResponse = new Long[] {1L, 2L, 3L};
        ResponseEntity<Long[]> entity = new ResponseEntity<>(returnedResponse, OK);

        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), Long[].class)).thenReturn(entity);

        // now run the method under test
        List<Long> response = service.getCompletedSectionIds(123L, 456L);
        assertNotNull(response);
        assertEquals(3, response.size());
        assertEquals(returnedResponse[0], response.get(0));
        assertEquals(returnedResponse[1], response.get(1));
        assertEquals(returnedResponse[2], response.get(2));
    }

    @Test
    public void test_getIncompleteSectionIds() {

        String expectedUrl = dataServicesUrl + sectionRestUrl + "/getIncompleteSections/123";
        Long[] returnedResponse = new Long[] {1L, 2L, 3L};
        ResponseEntity<Long[]> entity = new ResponseEntity<>(returnedResponse, OK);

        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), Long[].class)).thenReturn(entity);

        // now run the method under test
        List<Long> response = service.getIncompletedSectionIds(123L);
        assertNotNull(response);
        assertEquals(3, response.size());
        assertEquals(returnedResponse[0], response.get(0));
        assertEquals(returnedResponse[1], response.get(1));
        assertEquals(returnedResponse[2], response.get(2));
    }

    @Test
    public void test_getSection() {

        String expectedUrl = dataServicesUrl + sectionRestUrl + "/findByName/Section 1";
        Section returnedResponse = newSection().build();
        ResponseEntity<Section> entity = new ResponseEntity<>(returnedResponse, OK);

        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), Section.class)).thenReturn(entity);

        // now run the method under test
        Section response = service.getSection("Section 1");
        assertNotNull(response);
        assertEquals(returnedResponse, response);
    }
}
