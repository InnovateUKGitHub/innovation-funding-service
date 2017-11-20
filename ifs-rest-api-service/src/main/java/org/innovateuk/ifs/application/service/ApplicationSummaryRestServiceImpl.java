package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.applicationSummaryResourceListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.competitionSummaryResourceListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.longsListType;

@Service
public class ApplicationSummaryRestServiceImpl extends BaseRestService implements ApplicationSummaryRestService {

    private String applicationSummaryRestUrl = "/applicationSummary";

	private String applicationRestUrl = "/application";

	@Override
	public RestResult<ApplicationSummaryPageResource> getAllApplications(long competitionId,
                                                                         String sortField,
                                                                         int pageNumber,
                                                                         int pageSize,
                                                                         Optional<String> filter) {
		String baseUrl = applicationSummaryRestUrl + "/findByCompetition/" + competitionId;
		return getApplicationSummaryPage(baseUrl, pageNumber, pageSize, sortField, filter);
	}

	@Override
	public RestResult<List<Long>> getAllSubmittedApplicationIds(long competitionId,
                                                                Optional<String> filter,
                                                                Optional<FundingDecision> fundingFilter) {
		String baseUrl = applicationSummaryRestUrl + "/findByCompetition/" + competitionId + "/all-submitted";
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

		filter.ifPresent(f -> params.set("filter", f));
		fundingFilter.ifPresent(f -> params.set("fundingFilter", f.toString()));

		String uri = UriComponentsBuilder.fromPath(baseUrl).queryParams(params).build().encode().toUriString();

		return getWithRestResult(uri, longsListType());
	}

	@Override
	public RestResult<ApplicationSummaryPageResource> getSubmittedApplications(long competitionId,
                                                                               String sortField,
                                                                               int pageNumber,
                                                                               int pageSize,
                                                                               Optional<String> filter,
                                                                               Optional<FundingDecision> fundingFilter) {
		return getSubmittedApplicationsWithPanelStatus(competitionId, sortField, pageNumber, pageSize, filter, fundingFilter, Optional.empty());

	}

	@Override
	public RestResult<ApplicationSummaryPageResource> getSubmittedApplicationsWithPanelStatus(long competitionId,
																							  String sortField,
																							  int pageNumber,
																							  int pageSize,
																							  Optional<String> filter,
																							  Optional<FundingDecision> fundingFilter,
																							  Optional<Boolean> inAssessmentPanel) {
		String baseUrl = applicationSummaryRestUrl + "/findByCompetition/" + competitionId + "/submitted";
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

		filter.ifPresent(f -> params.set("filter", f));
		fundingFilter.ifPresent(f -> params.set("fundingFilter", f.toString()));
		inAssessmentPanel.ifPresent(f -> params.set("inAssessmentPanel", f.toString()));

		String uriWithParams = buildPaginationUri(baseUrl, pageNumber, pageSize, sortField, params);
		return getWithRestResult(uriWithParams, ApplicationSummaryPageResource.class);
	}

	@Override
	public RestResult<ApplicationSummaryPageResource> getNonSubmittedApplications(long competitionId,
                                                                                  String sortField,
                                                                                  int pageNumber,
                                                                                  int pageSize,
                                                                                  Optional<String> filter) {
		String baseUrl = applicationSummaryRestUrl + "/findByCompetition/" + competitionId + "/not-submitted";
		return getApplicationSummaryPage(baseUrl, pageNumber, pageSize, sortField, filter);
	}

	@Override
	public RestResult<ApplicationSummaryPageResource> getIneligibleApplications(long competitionId,
                                                                                String sortField,
                                                                                int pageNumber,
                                                                                int pageSize,
                                                                                Optional<String> filter,
                                                                                Optional<Boolean> informFilter) {
		String baseUrl = applicationSummaryRestUrl + "/findByCompetition/" + competitionId + "/ineligible";
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		filter.ifPresent(f -> params.set("filter", f));
		informFilter.ifPresent(f -> params.set("informFilter", f.toString()));
		String uriWithParams = buildPaginationUri(baseUrl, pageNumber, pageSize, sortField, params);
		return getWithRestResult(uriWithParams, ApplicationSummaryPageResource.class);
	}

	@Override
	public RestResult<ApplicationSummaryPageResource> getWithFundingDecisionApplications(Long competitionId,
																						 String sortField,
																						 int pageNumber,
																						 int pageSize,
																						 Optional<String> filter,
																						 Optional<Boolean> sendFilter,
																						 Optional<FundingDecision> fundingFilter) {
		String baseUrl = applicationSummaryRestUrl + "/findByCompetition/" + competitionId + "/with-funding-decision";
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

		filter.ifPresent(f -> params.set("filter", f));
		sendFilter.ifPresent(f -> params.set("sendFilter", f.toString()));
		fundingFilter.ifPresent(f -> params.set("fundingFilter", f.toString()));
		String uriWithParams = buildPaginationUri(baseUrl, pageNumber, pageSize, sortField, params);
		return getWithRestResult(uriWithParams, ApplicationSummaryPageResource.class);
	}

    @Override
    public RestResult<List<Long>> getWithFundingDecisionIsChangeableApplicationIdsByCompetitionId(Long competitionId,
                                                                                                  Optional<String> filter,
                                                                                                  Optional<Boolean> sendFilter,
                                                                                                  Optional<FundingDecision> fundingFilter) {
		String baseUrl = format("%s/%s/%s/%s", applicationSummaryRestUrl, "findByCompetition", competitionId, "with-funding-decision");
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromPath(baseUrl)
				.queryParam("all");

		filter.ifPresent(f -> builder.queryParam("filter", f));
		sendFilter.ifPresent(f -> builder.queryParam("sendFilter", f.toString()));
		fundingFilter.ifPresent(f -> builder.queryParam("fundingFilter", f.toString()));
		return getWithRestResult(builder.toUriString(), longsListType());
    }

    private RestResult<ApplicationSummaryPageResource> getApplicationSummaryPage(String url,
                                                                                 int pageNumber,
                                                                                 int pageSize,
                                                                                 String sortField,
                                                                                 Optional<String> filter) {
		String urlWithParams = buildUri(url, sortField, pageNumber, pageSize, filter);
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

	@Override
	public RestResult<ApplicationTeamResource> getApplicationTeam(long applicationId) {
		return getWithRestResult(applicationSummaryRestUrl + "/applicationTeam/" + applicationId, ApplicationTeamResource.class);
	}

	public void setApplicationSummaryRestUrl(String applicationSummaryRestUrl) {
		this.applicationSummaryRestUrl = applicationSummaryRestUrl;
	}

	protected String buildUri(String url, String sortField, int pageNumber, int pageSize, Optional<String> filter) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		filter.ifPresent(f -> params.set("filter", f));
		return buildPaginationUri(url, pageNumber, pageSize, sortField, params);
	}
}
