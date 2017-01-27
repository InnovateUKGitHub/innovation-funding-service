package org.innovateuk.ifs.publiccontent.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;

import java.util.Optional;

public interface PublicContentItemService {

    @NotSecured("Visible on for everyone even not logged in users")
    ServiceResult<PublicContentItemPageResource> findFilteredItems(Optional<Long> innovationAreaId, Optional<String> searchString, Optional<Long> pageNumber, Optional<Long> pageSize);

    @NotSecured("Visible on for everyone even not logged in users")
    ServiceResult<PublicContentItemResource> byCompetitionId(Long id);
}
