package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentEventResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;

import java.util.List;

/**
 * Service for handling public content events
 */
public interface ContentEventService {
    ServiceResult<Void> resetAndSaveEvents(PublicContentResource resource, List<ContentEventResource> events);
}
