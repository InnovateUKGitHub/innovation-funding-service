package org.innovateuk.ifs.publiccontent.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemPageResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;

/**
 * Service for operations around the usage and processing of Competitions
 */
@Service
public class PublicContentItemServiceImpl extends BaseTransactionalService implements PublicContentItemService {

    @Override
    public ServiceResult<PublicContentItemPageResource> findFilteredItems(Optional<Long> innovationAreaId, Optional<String> searchString, Optional<Long> pageNumber, Optional<Long> pageSize) {
        //TODO : Implement Method
        return null;
    }

    @Override
    public ServiceResult<PublicContentItemResource> byCompetitionId(Long id) {
        //TODO : Implement Method
        return null;
    }

    private List<String> separateSearchStringToList(String searchString) {
        return asList(searchString.split(" ");
    }
}
