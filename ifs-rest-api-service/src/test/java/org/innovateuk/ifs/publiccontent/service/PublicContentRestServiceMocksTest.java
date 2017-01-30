
package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.junit.Assert.*;

public class PublicContentRestServiceMocksTest extends BaseRestServiceUnitTest<PublicContentRestServiceImpl> {

    private static final String PUBLIC_CONTENT_URL = "/public-content";
    private static final Long COMPETITION_ID = 1L;

    @Override
    protected PublicContentRestServiceImpl registerRestServiceUnderTest() {
        PublicContentRestServiceImpl publicContentRestServiceImpl = new PublicContentRestServiceImpl();
        return publicContentRestServiceImpl;
    }

    @Test
    public void test_getByCompetitionId() {
        PublicContentResource expectedResponse = newPublicContentResource().build();
        setupGetWithRestResultExpectations(PUBLIC_CONTENT_URL + "/find-by-competition-id/" + COMPETITION_ID, PublicContentResource.class, expectedResponse);
        PublicContentResource response = service.getByCompetitionId(COMPETITION_ID).getSuccessObjectOrThrowException();
        assertThat(response, equalTo(expectedResponse));
    }

    @Test
    public void test_publishByCompetitionId() {
        setupPostWithRestResultExpectations(PUBLIC_CONTENT_URL + "/publish-by-competition-id/" + COMPETITION_ID, HttpStatus.OK);
        RestResult<Void> response = service.publishByCompetitionId(COMPETITION_ID);
        assertTrue(response.isSuccess());
    }

}