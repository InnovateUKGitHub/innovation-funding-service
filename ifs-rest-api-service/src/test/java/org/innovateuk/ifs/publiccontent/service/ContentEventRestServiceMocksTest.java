
package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentEventResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.innovateuk.ifs.publiccontent.builder.ContentEventResourceBuilder.newContentEventResource;
import static org.junit.Assert.assertTrue;

public class ContentEventRestServiceMocksTest extends BaseRestServiceUnitTest<ContentEventRestServiceImpl> {

    private static final String PUBLIC_CONTENT_EVENT_REST_URL = "/public-content/events";

    @Override
    protected ContentEventRestServiceImpl registerRestServiceUnderTest() {
        ContentEventRestServiceImpl publicContentEventRestService = new ContentEventRestServiceImpl();
        return publicContentEventRestService;
    }

    @Test
    public void test_resetAndSaveEvents() {
        List<ContentEventResource> events = newContentEventResource().build(1);
        setupPostWithRestResultExpectations(PUBLIC_CONTENT_EVENT_REST_URL + "/reset-and-save-events/1", events, HttpStatus.OK);
        RestResult<Void> response = service.resetAndSaveEvents(1L, events);
        assertTrue(response.isSuccess());
    }

}