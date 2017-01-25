package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;

/**
 * Created by luke.harper on 25/01/2017.
 */
public interface PublicContentRestService {

    RestResult<PublicContentResource> getCompetitionById(final Long id);

    RestResult<Void> publishByCompetitionId(Long competitionId);
}
