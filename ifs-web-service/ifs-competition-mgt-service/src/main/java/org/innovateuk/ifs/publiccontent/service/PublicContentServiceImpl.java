package org.innovateuk.ifs.publiccontent.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by luke.harper on 25/01/2017.
 */
@Service
public class PublicContentServiceImpl implements PublicContentService {

    @Autowired
    private PublicContentRestService publicContentRestService;

    @Override
    public ServiceResult<PublicContentResource> getCompetitionById(Long id) {
        return publicContentRestService.getCompetitionById(id).toServiceResult();
    }

    @Override
    public ServiceResult<Void> publishByCompetitionId(Long publicContentId) {
        return publicContentRestService.publishByCompetitionId(publicContentId).toServiceResult();
    }
}
