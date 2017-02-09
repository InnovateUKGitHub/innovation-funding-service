package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentEventResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Web service for public content events.
 */
@Service
public class ContentEventServiceImpl implements ContentEventService {

    @Autowired
    private ContentEventRestService contentEventRestService;

    @Override
    public ServiceResult<Void> updateEvent(ContentEventResource event) {
        return contentEventRestService.saveEvent(event).toServiceResult();
    }

    @Override
    public ServiceResult<Void> resetAndSaveEvents(PublicContentResource resource, List<ContentEventResource> events) {
        return contentEventRestService.resetAndSaveEvents(resource.getId(), events).toServiceResult();
    }
}
