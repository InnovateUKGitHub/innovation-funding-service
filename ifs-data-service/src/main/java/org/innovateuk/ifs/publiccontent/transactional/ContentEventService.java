package org.innovateuk.ifs.publiccontent.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentEventResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Interface for public content actions.
 */
public interface ContentEventService {

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "RESET_AND_SAVE_EVENTS",
            description = "The Competition Admin, or project finance user can publish the public content for a competition.")
    ServiceResult<Void> resetAndSaveEvents(Long publicContentId, List<ContentEventResource> eventResource);
}
