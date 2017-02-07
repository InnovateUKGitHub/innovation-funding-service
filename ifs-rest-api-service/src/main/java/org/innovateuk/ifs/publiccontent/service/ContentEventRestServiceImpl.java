package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentEventResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

/**
 * Implementation for public content rest calls.
 */
@Service
public class ContentEventRestServiceImpl extends BaseRestService implements ContentEventRestService {

    private static final String PUBLIC_CONTENT_EVENT_REST_URL = "/public-content/events";

    @Override
    public RestResult<Void> saveEvent(PublicContentEventResource event) {
        return postWithRestResult(PUBLIC_CONTENT_EVENT_REST_URL + "/save-event", event, Void.class);
    }

    @Override
    public RestResult<Void> resetAndSaveEvents(Long publicContentId, List<PublicContentEventResource> events) {
        return postWithRestResult(format(PUBLIC_CONTENT_EVENT_REST_URL + "/reset-and-save-events?id=%d", publicContentId), events, Void.class);
    }
}
