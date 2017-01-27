package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

/**
 * Rest service implementation for getting public content which get wrapped in {@link org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource}
 */
public interface PublicContentItemRestService {

    RestResult<PublicContentItemPageResource> getByFilterValues(Optional<Long> innovationAreaId,
                                                                Optional<String> searchString,
                                                                Optional<Long> pageNumber,
                                                                Optional<Long> pageSize) throws UnsupportedEncodingException;

    RestResult<PublicContentItemPageResource> getByItemsCompetitionId(final Long id);
}
