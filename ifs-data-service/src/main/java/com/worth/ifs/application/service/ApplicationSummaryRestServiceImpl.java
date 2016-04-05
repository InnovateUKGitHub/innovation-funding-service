package com.worth.ifs.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;

@Service
public class ApplicationSummaryRestServiceImpl extends BaseRestService implements ApplicationSummaryRestService {

    @Value("${ifs.data.service.rest.applicationSummary}")
    private String applicationSummaryRestUrl;

	@Value("${ifs.data.service.rest.application}")
	private String applicationRestUrl;

	@Override
	public RestResult<ApplicationSummaryPageResource> findByCompetitionId(Long competitionId, int pageNumber, String sortField) {
		String urlWithoutSortField = applicationSummaryRestUrl + "/findByCompetition/" + competitionId + "?page=" + Integer.toString(pageNumber);
		String url;
		if(StringUtils.isEmpty(sortField)){
			url = urlWithoutSortField;
		} else {
			url = urlWithoutSortField + "&sort=" + sortField;
		}
		return getWithRestResult(url, ApplicationSummaryPageResource.class);
	}

	@Override
	public RestResult<ApplicationSummaryResource> getApplicationSummary(Long id) {
		return getWithRestResult(applicationSummaryRestUrl + "/" + id, ApplicationSummaryResource.class);
	}

	@Override
	public RestResult<ByteArrayResource> downloadByCompetition(long competitionId) {
		String url = applicationRestUrl + "/download/downloadByCompetition/" + competitionId;
		return getWithRestResult(url, ByteArrayResource.class);
	}

	
	public void setApplicationSummaryRestUrl(String applicationSummaryRestUrl) {
		this.applicationSummaryRestUrl = applicationSummaryRestUrl;
	}

}
