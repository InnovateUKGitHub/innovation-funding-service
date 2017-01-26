package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSection;

/**
 * Created by luke.harper on 25/01/2017.
 */
public interface PublicContentService {

    ServiceResult<PublicContentResource> getCompetitionById(final Long competitionId);

    ServiceResult<Void> publishByCompetitionId(Long publicContentId);

    ServiceResult<Void> updateSection(PublicContentResource resource, PublicContentSection section);
}
