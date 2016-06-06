package com.worth.ifs.application.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;

@Service
public class ApplicationSummaryRestServiceImpl extends BaseRestService implements ApplicationSummaryRestService {

    private String applicationSummaryRestUrl = "/applicationSummary";

	private String applicationRestUrl = "/application";

	@Override
	public RestResult<ApplicationSummaryPageResource> findByCompetitionId(Long competitionId, String sortField, Integer pageNumber, Integer pageSize) {
		String baseUrl = applicationSummaryRestUrl + "/findByCompetition/" + competitionId;
		return getApplicationSummaryPage(baseUrl, pageNumber, pageSize, sortField);
	}

	@Override
	public RestResult<ApplicationSummaryPageResource> getSubmittedApplicationSummariesByCompetitionId(Long competitionId, String sortField, Integer pageNumber, Integer pageSize) {
		String baseUrl = applicationSummaryRestUrl + "/findByCompetition/" + competitionId + "/submitted";
		return getApplicationSummaryPage(baseUrl, pageNumber, pageSize, sortField);
	}

	@Override
	public RestResult<ApplicationSummaryPageResource> getNotSubmittedApplicationSummariesByCompetitionId(Long competitionId, String sortField, Integer pageNumber, Integer pageSize) {
		String baseUrl = applicationSummaryRestUrl + "/findByCompetition/" + competitionId + "/not-submitted";
		return getApplicationSummaryPage(baseUrl, pageNumber, pageSize, sortField);
	}
	
	@Override
	public RestResult<ApplicationSummaryPageResource> getFeedbackRequiredApplicationSummariesByCompetitionId(Long competitionId, String sortField, Integer pageNumber, Integer pageSize) {
		String baseUrl = applicationSummaryRestUrl + "/findByCompetition/" + competitionId + "/feedback-required";
		return getApplicationSummaryPage(baseUrl, pageNumber, pageSize, sortField);
	}
	
	private RestResult<ApplicationSummaryPageResource> getApplicationSummaryPage(String url, Integer pageNumber, Integer pageSize, String sortField) {
		
		Map<String, String> params = new LinkedHashMap<>();
		if(pageNumber != null) {
			params.put("page", pageNumber.toString());
		}
		if(pageSize != null) {
			params.put("size", pageSize.toString());
		}
		if(!StringUtils.isEmpty(sortField)){
			params.put("sort", sortField);
		}
		
		String urlWithParams;
		if(params.isEmpty()) {
			urlWithParams = url;
		} else {
			urlWithParams = url + "?" + params.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
		}
		
		return getWithRestResult(urlWithParams, ApplicationSummaryPageResource.class);
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
