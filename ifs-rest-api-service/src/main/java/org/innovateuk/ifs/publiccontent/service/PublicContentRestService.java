package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;

/**
 * Rest service for public content.
 */
public interface PublicContentRestService {

    RestResult<PublicContentResource> getByCompetitionId(final Long id);

    RestResult<Void> publishByCompetitionId(Long competitionId);
}
