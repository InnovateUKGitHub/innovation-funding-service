package org.innovateuk.ifs.publiccontent.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

public interface PublicContentItemService {

    @PreAuthorize("hasAuthority('system_registrar')")
    ServiceResult<PublicContentItemPageResource> findFilteredItems(Optional<Long> innovationAreaId, Optional<String> searchString, Optional<Integer> pageNumber, Integer pageSize);

    @PreAuthorize("hasAuthority('system_registrar')")
    ServiceResult<PublicContentItemResource> byCompetitionId(Long id);
}
