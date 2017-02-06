package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentEventResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Web service for public content.
 */
@Service
public class PublicContentEventServiceImpl implements PublicContentEventService {

    @Autowired
    private PublicContentRestService publicContentRestService;

    @Override
    public ServiceResult<Void> updateEvent(PublicContentEventResource event) {
        return null;
    }

    @Override
    public ServiceResult<Void> resetAndSaveEvents(PublicContentResource resource, List<PublicContentEventResource> events) {
        return null;
    }
}
