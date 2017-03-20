package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;

import java.util.Optional;

import static java.util.Collections.singletonList;

@Service
public class ApplicationSummaryRestServiceImpl extends BaseRestService implements ApplicationSummaryRestService {

    private String applicationSummaryRestUrl = "/applicationSummary";

	private String applicationRestUrl = "/application";

	@Override
	public RestResult<ApplicationSummaryPageResource> getAllApplications(long competitionId, String sortField, int pageNumber, int pageSize, String filter) {
		String baseUrl = applicationSummaryRestUrl + "/findByCompetition/" + competitionId;
		return getApplicationSummaryPage(baseUrl, pageNumber, pageSize, sortField, filter);
	}

	@Override
	public RestResult<ApplicationSummaryPageResource> getSubmittedApplications(long competitionId, String sortField, int pageNumber, int pageSize, String filter, Optional<FundingDecision> fundingFilter) {
		String baseUrl = applicationSummaryRestUrl + "/findByCompetition/" + competitionId + "/submitted";
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		if (filter != null) {
			params.put("filter", singletonList(filter));
		}
		fundingFilter.ifPresent(f -> params.put("fundingFilter", singletonList(f.toString())));
		String uriWithParams = buildPaginationUri(baseUrl, pageNumber, pageSize, sortField, params);
		return getWithRestResult(uriWithParams, ApplicationSummaryPageResource.class);
	}

	@Override
	public RestResult<ApplicationSummaryPageResource> getNonSubmittedApplications(long competitionId, String sortField, int pageNumber, int pageSize, String filter) {
		String baseUrl = applicationSummaryRestUrl + "/findByCompetition/" + competitionId + "/not-submitted";
		return getApplicationSummaryPage(baseUrl, pageNumber, pageSize, sortField, filter);
	}
	
	@Override
	public RestResult<ApplicationSummaryPageResource> getFeedbackRequiredApplications(long competitionId, String sortField, int pageNumber, int pageSize, String filter) {
		String baseUrl = applicationSummaryRestUrl + "/findByCompetition/" + competitionId + "/feedback-required";
		return getApplicationSummaryPage(baseUrl, pageNumber, pageSize, sortField, filter);
	}

	@Override
	public RestResult<ApplicationSummaryPageResource> getWithFundingDecisionApplications(Long competitionId,
																						 String sortField,
																						 int pageNumber,
																						 int pageSize,
																						 String filter,
																						 Optional<Boolean> sendFilter,
																						 Optional<FundingDecision> fundingFilter) {
		String baseUrl = applicationSummaryRestUrl + "/findByCompetition/" + competitionId + "/with-funding-decision";
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		if (filter != null) {
			params.put("filter", singletonList(filter));
		}
		sendFilter.ifPresent(f -> params.put("sendFilter", singletonList(f.toString())));
		fundingFilter.ifPresent(f -> params.put("fundingFilter", singletonList(f.toString())));
		String uriWithParams = buildPaginationUri(baseUrl, pageNumber, pageSize, sortField, params);
		return getWithRestResult(uriWithParams, ApplicationSummaryPageResource.class);
	}
	
	private RestResult<ApplicationSummaryPageResource> getApplicationSummaryPage(String url, int pageNumber, int pageSize, String sortField, String filter) {

		String urlWithParams = buildUri(url, sortField, pageNumber, pageSize,filter);
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

	protected String buildUri(String url, String sortField, int pageNumber, int pageSize, String filter) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		if (filter != null) {
			params.put("filter", singletonList(filter));
		}
		return buildPaginationUri(url, pageNumber, pageSize, sortField, params);
	}

}
