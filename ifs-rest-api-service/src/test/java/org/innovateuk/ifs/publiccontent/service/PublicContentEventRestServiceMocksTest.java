
package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentEventResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.innovateuk.ifs.publiccontent.builder.PublicContentEventResourceBuilder.newPublicContentEventResource;
import static org.junit.Assert.assertTrue;

public class PublicContentEventRestServiceMocksTest extends BaseRestServiceUnitTest<PublicContentEventRestServiceImpl> {

    private static final String PUBLIC_CONTENT_EVENT_REST_URL = "/public-content/events";

    @Override
    protected PublicContentEventRestServiceImpl registerRestServiceUnderTest() {
        PublicContentEventRestServiceImpl publicContentEventRestService = new PublicContentEventRestServiceImpl();
        return publicContentEventRestService;
    }

    @Test
    public void test_saveEvent() {
        PublicContentEventResource event = newPublicContentEventResource().build();
        setupPostWithRestResultExpectations(PUBLIC_CONTENT_EVENT_REST_URL + "/save-event", event, HttpStatus.OK);
        RestResult<Void> response = service.saveEvent(event);
        assertTrue(response.isSuccess());
    }

    @Test
    public void test_resetAndSaveEvents() {
        List<PublicContentEventResource> events = newPublicContentEventResource().build(1);
        setupPostWithRestResultExpectations(PUBLIC_CONTENT_EVENT_REST_URL + "/reset-and-save-events?id=1", events, HttpStatus.OK);
        RestResult<Void> response = service.resetAndSaveEvents(1L, events);
        assertTrue(response.isSuccess());
    }

}