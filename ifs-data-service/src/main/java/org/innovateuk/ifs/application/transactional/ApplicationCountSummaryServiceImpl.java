package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.mapper.ApplicationCountSummaryMapper;
import org.innovateuk.ifs.application.mapper.ApplicationSummaryPageMapper;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ApplicationCountSummaryServiceImpl extends BaseTransactionalService implements ApplicationCountSummaryService {

    @Autowired
    private ApplicationCountSummaryMapper applicationCountSummaryMapper;

    @Autowired
    private ApplicationSummaryPageMapper applicationSummaryPageMapper;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public ServiceResult<List<ApplicationCountSummaryResource>> getApplicationCountSummariesByCompetitionId(Long competitionId) {
        return serviceSuccess(simpleMap(applicationRepository.findByCompetitionId(competitionId),application -> applicationCountSummaryMapper.mapToResource(application)));
    }
}
