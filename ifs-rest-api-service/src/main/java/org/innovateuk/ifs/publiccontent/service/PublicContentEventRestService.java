package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentEventResource;

import java.util.List;

/**
 * Rest service for public content event.
 */
public interface PublicContentEventRestService {

    RestResult<Void> saveEvent(PublicContentEventResource event);

    RestResult<Void> resetAndSaveEvents(Long publicContentId, List<PublicContentEventResource> events);
}
