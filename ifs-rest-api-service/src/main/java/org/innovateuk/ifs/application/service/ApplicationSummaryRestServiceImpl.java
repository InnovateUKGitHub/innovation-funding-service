package org.innovateuk.ifs.application.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;

@Service
public class ApplicationSummaryRestServiceImpl extends BaseRestService implements ApplicationSummaryRestService {

    private String applicationSummaryRestUrl = "/applicationSummary";

	private String applicationRestUrl = "/application";

	@Override
	public RestResult<ApplicationSummaryPageResource> getAllApplications(Long competitionId, String sortField, Integer pageNumber, Integer pageSize) {
		String baseUrl = applicationSummaryRestUrl + "/findByCompetition/" + competitionId;
		return getApplicationSummaryPage(baseUrl, pageNumber, pageSize, sortField);
	}

	@Override
	public RestResult<ApplicationSummaryPageResource> getSubmittedApplications(Long competitionId, String sortField, Integer pageNumber, Integer pageSize) {
		String baseUrl = applicationSummaryRestUrl + "/findByCompetition/" + competitionId + "/submitted";
		return getApplicationSummaryPage(baseUrl, pageNumber, pageSize, sortField);
	}

	@Override
	public RestResult<ApplicationSummaryPageResource> getNonSubmittedApplications(Long competitionId, String sortField, Integer pageNumber, Integer pageSize) {
		String baseUrl = applicationSummaryRestUrl + "/findByCompetition/" + competitionId + "/not-submitted";
		return getApplicationSummaryPage(baseUrl, pageNumber, pageSize, sortField);
	}
	
	@Override
	public RestResult<ApplicationSummaryPageResource> getFeedbackRequiredApplications(Long competitionId, String sortField, Integer pageNumber, Integer pageSize) {
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
	public RestResult<CompetitionSummaryResource> getCompetitionSummary(Long competitionId) {
		return getWithRestResult(applicationSummaryRestUrl + "/getCompetitionSummary/" + competitionId, CompetitionSummaryResource.class);
	}

	public void setApplicationSummaryRestUrl(String applicationSummaryRestUrl) {
		this.applicationSummaryRestUrl = applicationSummaryRestUrl;
	}

	

}
