package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentEventResource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation for public content rest calls.
 */
@Service
public class ContentEventRestServiceImpl extends BaseRestService implements ContentEventRestService {

    private static final String PUBLIC_CONTENT_EVENT_REST_URL = "/public-content/events";

    @Override
    public RestResult<Void> saveEvent(ContentEventResource event) {
        return postWithRestResult(PUBLIC_CONTENT_EVENT_REST_URL + "/save-event", event, Void.class);
    }

    @Override
    public RestResult<Void> resetAndSaveEvents(Long publicContentId, List<ContentEventResource> events) {
        return postWithRestResult(PUBLIC_CONTENT_EVENT_REST_URL + "/reset-and-save-events/" + publicContentId, events, Void.class);
    }
}
