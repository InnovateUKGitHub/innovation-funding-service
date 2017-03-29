package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.mapper.ApplicationCountSummaryPageMapper;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.ApplicationStatisticsRepository;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.SUBMITTED_STATUS_IDS;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ApplicationCountSummaryServiceImpl extends BaseTransactionalService implements ApplicationCountSummaryService {

    @Autowired
    private ApplicationCountSummaryPageMapper applicationCountSummaryPageMapper;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private OrganisationRepository processRoleRepository;

    @Autowired
    private ApplicationStatisticsRepository applicationStatisticsRepository;

    @Override
    public ServiceResult<ApplicationCountSummaryPageResource> getApplicationCountSummariesByCompetitionId(Long competitionId, int pageIndex, int pageSize, Optional<String> filter) {

        String filterStr = filter.map(String::trim).orElse("");
        Pageable pageable = new PageRequest(pageIndex, pageSize);
        Page<ApplicationStatistics> applicationStatistics = applicationStatisticsRepository.findByCompetitionAndApplicationStatusIdIn(competitionId, SUBMITTED_STATUS_IDS, filterStr, pageable);

        return find(applicationStatistics, notFoundError(Page.class)).andOnSuccessReturn(stats -> applicationCountSummaryPageMapper.mapToResource(stats));
    }
}
