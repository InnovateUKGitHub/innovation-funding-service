package com.worth.ifs.application.transactional;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;

public interface ApplicationSummaryService {

	@NotSecured("TODO")
    ServiceResult<ApplicationSummaryResource> getApplicationSummaryById(Long id);

	@NotSecured("TODO")
	ServiceResult<ApplicationSummaryPageResource> getApplicationSummariesByCompetitionId(Long competitionId, int pageIndex);

}
