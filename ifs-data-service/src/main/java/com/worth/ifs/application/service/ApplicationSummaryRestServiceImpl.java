package com.worth.ifs.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
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
		return getApplicationSummaryPage(urlWithoutSortField, sortField);
	}

	@Override
	public RestResult<ApplicationSummaryPageResource> getSubmittedApplicationSummariesByCompetitionId(Long competitionId, int pageNumber, String sortField) {
		String urlWithoutSortField = applicationSummaryRestUrl + "/findByCompetition/" + competitionId + "/submitted?page=" + Integer.toString(pageNumber);
		return getApplicationSummaryPage(urlWithoutSortField, sortField);
	}

	@Override
	public RestResult<ApplicationSummaryPageResource> getNotSubmittedApplicationSummariesByCompetitionId(Long competitionId, int pageNumber, String sortField) {
		String urlWithoutSortField = applicationSummaryRestUrl + "/findByCompetition/" + competitionId + "/not-submitted?page=" + Integer.toString(pageNumber);
		return getApplicationSummaryPage(urlWithoutSortField, sortField);
	}
	
	private RestResult<ApplicationSummaryPageResource> getApplicationSummaryPage(String urlWithoutSort, String sortField) {
		String url;
		if(StringUtils.isEmpty(sortField)){
			url = urlWithoutSort;
		} else {
			url = urlWithoutSort + "&sort=" + sortField;
		}
		return getWithRestResult(url, ApplicationSummaryPageResource.class);
	}

	@Override
	public RestResult<ByteArrayResource> downloadByCompetition(long competitionId) {
		String url = applicationRestUrl + "/download/downloadByCompetition/" + competitionId;
		return getWithRestResult(url, ByteArrayResource.class);
	}
	
	@Override
	public RestResult<CompetitionSummaryResource> getCompetitionSummaryByCompetitionId(Long competitionId) {
		return getWithRestResult(applicationSummaryRestUrl + "/getCompetitionSummary/" + competitionId, CompetitionSummaryResource.class);
	}

	public void setApplicationSummaryRestUrl(String applicationSummaryRestUrl) {
		this.applicationSummaryRestUrl = applicationSummaryRestUrl;
	}

}
