package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentEventResource;

import java.util.List;

/**
 * Rest service for public content events.
 */
public interface ContentEventRestService {

    RestResult<Void> resetAndSaveEvents(Long publicContentId, List<ContentEventResource> events);
}
