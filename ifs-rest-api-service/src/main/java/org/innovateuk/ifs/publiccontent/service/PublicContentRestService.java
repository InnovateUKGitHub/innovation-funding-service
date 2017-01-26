package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;

import java.util.List;
import java.util.Optional;

/**
 * Created by luke.harper on 25/01/2017.
 */
public interface PublicContentRestService {

    RestResult<PublicContentResource> getByCompetitionId(final Long id);

    RestResult<Void> publishByCompetitionId(Long competitionId);

    RestResult<PublicContentItemPageResource> getByFilterValues(Optional<Long> innovationAreaId,
                                                                Optional<List<String>> keywords,
                                                                Optional<Long> pageNumber,
                                                                Optional<Long> pageSize);

    RestResult<PublicContentItemPageResource> getByItemCompetitionId(final Long id);
}
