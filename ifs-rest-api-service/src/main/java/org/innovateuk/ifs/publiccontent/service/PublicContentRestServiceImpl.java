package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.springframework.stereotype.Service;

/**
 * Implementation for public content rest calls.
 */
@Service
public class PublicContentRestServiceImpl extends BaseRestService implements PublicContentRestService {

    private static final String PUBLIC_CONTENT_REST_URL = "/public-content/";

    @Override
    public RestResult<PublicContentResource> getByCompetitionId(Long id) {
        return getWithRestResult(PUBLIC_CONTENT_REST_URL + "find-by-competition-id/" + id, PublicContentResource.class);
    }

    @Override
    public RestResult<Void> publishByCompetitionId(Long id) {
        return postWithRestResult(PUBLIC_CONTENT_REST_URL + "publish-by-competition-id/" + id, Void.class);
    }
}
