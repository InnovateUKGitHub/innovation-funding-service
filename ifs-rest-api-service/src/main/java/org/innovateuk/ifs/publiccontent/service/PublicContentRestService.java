package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSection;

/**
 * Created by luke.harper on 25/01/2017.
 */
public interface PublicContentRestService {

    RestResult<PublicContentResource> getByCompetitionId(final Long id);

    RestResult<Void> publishByCompetitionId(Long competitionId);

    RestResult<Void> updateSection(PublicContentResource resource, PublicContentSection section);
}
