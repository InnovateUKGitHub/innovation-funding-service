package org.innovateuk.ifs.publiccontent.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

public interface PublicContentItemService {

    @SecuredBySpring(value = "READ", description = "Anonymous users can search through Open Competitions public content", securedType = PublicContentItemPageResource.class)
    @PreAuthorize("hasAuthority('system_registrar')")
    ServiceResult<PublicContentItemPageResource> findFilteredItems(Optional<Long> innovationAreaId, Optional<String> searchString, Optional<Integer> pageNumber, Integer pageSize);

    @PreAuthorize("hasPermission(#competitionId, 'READ_PUBLISHED')")
    ServiceResult<PublicContentItemResource> byCompetitionId(@P("competitionId") Long competitionId);
}
