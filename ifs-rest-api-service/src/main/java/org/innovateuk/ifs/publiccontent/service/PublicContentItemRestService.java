package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

/**
 * Rest service implementation for getting public content which get wrapped in {@link org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource}
 */
public interface PublicContentItemRestService {

    RestResult<PublicContentItemPageResource> getByFilterValues(Optional<Long> innovationAreaId,
                                                                Optional<String> searchString,
                                                                Optional<Integer> pageNumber,
                                                                Optional<Integer> pageSize) throws UnsupportedEncodingException;

    RestResult<PublicContentItemResource> getItemByCompetitionId(final Long competitionId);
}
