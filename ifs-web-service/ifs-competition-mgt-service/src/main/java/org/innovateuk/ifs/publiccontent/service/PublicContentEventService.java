package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentEventResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;

import java.util.List;

/**
 * Service for
 */
public interface PublicContentEventService {
    ServiceResult<Void> updateEvent(PublicContentEventResource event);
    ServiceResult<Void> resetAndSaveEvents(PublicContentResource resource, List<PublicContentEventResource> events);
}
