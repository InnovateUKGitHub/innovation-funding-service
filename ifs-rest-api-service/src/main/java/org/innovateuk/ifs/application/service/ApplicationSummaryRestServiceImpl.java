package org.innovateuk.ifs.application.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.web.util.UriComponentsBuilder;

import static java.util.Collections.singletonList;

@Service
public class ApplicationSummaryRestServiceImpl extends BaseRestService implements ApplicationSummaryRestService {

    private String applicationSummaryRestUrl = "/applicationSummary";

	private String applicationRestUrl = "/application";

	@Override
	public RestResult<ApplicationSummaryPageResource> getAllApplications(long competitionId, String sortField, Integer pageNumber, Integer pageSize, String filter) {
		String baseUrl = applicationSummaryRestUrl + "/findByCompetition/" + competitionId;
		return getApplicationSummaryPage(baseUrl, pageNumber, pageSize, sortField, filter);
	}

	@Override
	public RestResult<ApplicationSummaryPageResource> getSubmittedApplications(long competitionId, String sortField, Integer pageNumber, Integer pageSize, String filter) {
		String baseUrl = applicationSummaryRestUrl + "/findByCompetition/" + competitionId + "/submitted";
		return getApplicationSummaryPage(baseUrl, pageNumber, pageSize, sortField, filter);
	}

	@Override
	public RestResult<ApplicationSummaryPageResource> getNonSubmittedApplications(long competitionId, String sortField, Integer pageNumber, Integer pageSize, String filter) {
		String baseUrl = applicationSummaryRestUrl + "/findByCompetition/" + competitionId + "/not-submitted";
		return getApplicationSummaryPage(baseUrl, pageNumber, pageSize, sortField, filter);
	}
	
	@Override
	public RestResult<ApplicationSummaryPageResource> getFeedbackRequiredApplications(long competitionId, String sortField, Integer pageNumber, Integer pageSize, String filter) {
		String baseUrl = applicationSummaryRestUrl + "/findByCompetition/" + competitionId + "/feedback-required";
		return getApplicationSummaryPage(baseUrl, pageNumber, pageSize, sortField, filter);
	}

	@Override
	public RestResult<ApplicationSummaryPageResource> getWithFundingDecisionApplications(Long competitionId, String sortField, Integer pageNumber, Integer pageSize, String filter) {
		String baseUrl = applicationSummaryRestUrl + "/findByCompetition/" + competitionId + "/with-funding-decision";
		return getApplicationSummaryPage(baseUrl, pageNumber, pageSize, sortField, filter);
	}
	
	private RestResult<ApplicationSummaryPageResource> getApplicationSummaryPage(String url, Integer pageNumber, Integer pageSize, String sortField, String filter) {

		String urlWithParams = addSort(url, sortField, pageNumber, pageSize,filter);
		return getWithRestResult(urlWithParams, ApplicationSummaryPageResource.class);
	}

	@Override
	public RestResult<ByteArrayResource> downloadByCompetition(long competitionId) {
		String url = applicationRestUrl + "/download/downloadByCompetition/" + competitionId;
		return getWithRestResult(url, ByteArrayResource.class);
	}



	@Override
	public RestResult<CompetitionSummaryResource> getCompetitionSummary(long competitionId) {
		return getWithRestResult(applicationSummaryRestUrl + "/getCompetitionSummary/" + competitionId, CompetitionSummaryResource.class);
	}

	public void setApplicationSummaryRestUrl(String applicationSummaryRestUrl) {
		this.applicationSummaryRestUrl = applicationSummaryRestUrl;
	}

	protected String addSort(String url, String sortField, Integer pageNumber, Integer pageSize, String filter) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		if(pageNumber != null) {
			params.put("page", singletonList(pageNumber.toString()));
		}
		if(pageSize != null) {
			params.put("size", singletonList(pageSize.toString()));
		}
		if(!StringUtils.isEmpty(sortField)){
			params.put("sort", singletonList(sortField));
		}
		if (filter != null) {
			params.put("filter", singletonList(filter));
		}
		return UriComponentsBuilder.fromPath(url).queryParams(params).toUriString();
	}

}
