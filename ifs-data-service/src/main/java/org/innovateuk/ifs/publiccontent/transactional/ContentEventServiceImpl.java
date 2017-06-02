package org.innovateuk.ifs.publiccontent.transactional;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.ContentEventResource;
import org.innovateuk.ifs.publiccontent.mapper.ContentEventMapper;
import org.innovateuk.ifs.publiccontent.repository.ContentEventRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PUBLIC_CONTENT_IDS_INCONSISTENT;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Service for operations around the usage and processing of public content events.
 */
@Service
public class ContentEventServiceImpl extends BaseTransactionalService implements ContentEventService {

    @Autowired
    private ContentEventRepository contentEventRepository;

    @Autowired
    private ContentEventMapper contentEventMapper;

    @Override
    @Transactional
    public ServiceResult<Void> resetAndSaveEvents(Long publicContentId, List<ContentEventResource> eventResources) {
        if(eventResourcesAllHaveIDOrEmpty(publicContentId, eventResources)) {
            contentEventRepository.deleteByPublicContentId(publicContentId);
            contentEventRepository.save(contentEventMapper.mapToDomain(eventResources));
            return serviceSuccess();
        }

        return serviceFailure(new Error(PUBLIC_CONTENT_IDS_INCONSISTENT));
    }

    private Boolean eventResourcesAllHaveIDOrEmpty(Long publicContentId, List<ContentEventResource> eventResources) {
        return eventResources.isEmpty() || eventResources.stream().allMatch(eventResource -> eventResource.getPublicContent().equals(publicContentId));
    }
}
