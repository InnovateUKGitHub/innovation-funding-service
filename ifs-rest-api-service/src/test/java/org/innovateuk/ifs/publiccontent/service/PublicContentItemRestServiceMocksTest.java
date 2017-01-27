
package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class PublicContentItemRestServiceMocksTest extends BaseRestServiceUnitTest<PublicContentItemRestServiceImpl> {

    private static final String PUBLIC_CONTENT_ITEM_REST_URL = "/public-content/items/";
    private static final Long COMPETITION_ID = 1L;

    @Override
    protected PublicContentItemRestServiceImpl registerRestServiceUnderTest() {
        PublicContentItemRestServiceImpl publicContentItemRestServiceImpl = new PublicContentItemRestServiceImpl();
        return publicContentItemRestServiceImpl;
    }

    @Test
    public void test_getByFilterValues() {
        PublicContentItemPageResource expectedResponse = new PublicContentItemPageResource();
        expectedResponse.setSize(20);
        expectedResponse.setNumber(32);
        expectedResponse.setTotalElements(23293L);
        expectedResponse.setTotalPages(123);
        setupGetWithRestResultExpectations(PUBLIC_CONTENT_ITEM_REST_URL + "find-by-filter?innovationAreaId=12&searchString=Search%20my%20competition&pageNumber=32&pageSize=20", PublicContentItemPageResource.class, expectedResponse);
        PublicContentItemPageResource response = service.getByFilterValues(Optional.of(12L), Optional.of("Search my competition"), Optional.of(32L), Optional.of(20L)).getSuccessObjectOrThrowException();
        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    public void test_getByFilterValuesEmpty() {
        PublicContentItemPageResource expectedResponse = new PublicContentItemPageResource();
        expectedResponse.setSize(20);
        expectedResponse.setNumber(32);
        expectedResponse.setTotalElements(23293L);
        expectedResponse.setTotalPages(123);
        setupGetWithRestResultExpectations(PUBLIC_CONTENT_ITEM_REST_URL + "find-by-filter?", PublicContentItemPageResource.class, expectedResponse);
        PublicContentItemPageResource response = service.getByFilterValues(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()).getSuccessObjectOrThrowException();
        assertThat(response, equalTo(expectedResponse));
    }


    @Test
    public void test_getByFilterValuesFewEmpty() {
        PublicContentItemPageResource expectedResponse = new PublicContentItemPageResource();
        expectedResponse.setSize(20);
        expectedResponse.setNumber(32);
        expectedResponse.setTotalElements(23293L);
        expectedResponse.setTotalPages(123);
        setupGetWithRestResultExpectations(PUBLIC_CONTENT_ITEM_REST_URL + "find-by-filter?pageNumber=32&pageSize=20", PublicContentItemPageResource.class, expectedResponse);
        PublicContentItemPageResource response = service.getByFilterValues(Optional.empty(), Optional.empty(), Optional.of(32L), Optional.of(20L)).getSuccessObjectOrThrowException();
        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    public void test_getByItemsCompetitionId() {
        PublicContentItemResource expectedResponse = new PublicContentItemResource();
        setupGetWithRestResultExpectations(PUBLIC_CONTENT_ITEM_REST_URL + "all-by-competition-id/" + COMPETITION_ID, PublicContentItemResource.class, expectedResponse);
        RestResult<PublicContentItemResource> response = service.getItemByCompetitionId(COMPETITION_ID);
        assertTrue(response.isSuccess());
    }

}