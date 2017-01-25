package org.innovateuk.ifs.publiccontent.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;

public interface PublicContentService {

    ServiceResult<PublicContentResource> getCompetitionById(final Long id);

    ServiceResult<Void> initialiseForCompetitionId(Long id);

    ServiceResult<Void> publishByCompetitionId(Long id);
}
