package com.worth.ifs.application.transactional;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.worth.ifs.application.mapper.ApplicationSummaryMapper;
import com.worth.ifs.application.mapper.ApplicationSummaryPageMapper;
import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;

@Service
public class ApplicationSummaryServiceImpl extends BaseTransactionalService implements ApplicationSummaryService {

	private static final int PAGE_SIZE = 20;
	
	@Autowired
    private ApplicationSummaryMapper applicationSummaryMapper;
	
	@Autowired
	private ApplicationSummaryPageMapper applicationSummaryPageMapper;
	
	@Override
	public ServiceResult<ApplicationSummaryResource> getApplicationSummaryById(Long id) {
		return getApplication(id).andOnSuccessReturn(applicationSummaryMapper::mapToResource);
	}

	@Override
	public ServiceResult<ApplicationSummaryPageResource> getApplicationSummariesByCompetitionId(Long competitionId, int pageIndex) {
		Pageable pageable = new PageRequest(pageIndex, PAGE_SIZE);
		return find(applicationRepository.findByCompetitionId(competitionId, pageable), notFoundError(Page.class)).andOnSuccessReturn(applicationSummaryPageMapper::mapToResource);
	}

}
