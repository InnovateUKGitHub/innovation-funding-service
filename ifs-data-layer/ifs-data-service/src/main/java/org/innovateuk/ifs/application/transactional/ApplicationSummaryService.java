package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.FundingDecisionStatus;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationTeamResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface ApplicationSummaryService {

	@PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance', 'support')")
	@SecuredBySpring(value = "READ", description = "Internal users, apart from innovation lead,  can see all Application Summaries across the whole system", securedType = ApplicationSummaryPageResource.class)
	ServiceResult<ApplicationSummaryPageResource> getApplicationSummariesByCompetitionId(long competitionId,
																						 String sortBy,
																						 int pageIndex,
																						 int pageSize,
																						 Optional<String> filter);

	@PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance', 'support', 'innovation_lead')")
	@SecuredBySpring(value = "READ", description = "Internal users can see all submitted Application Summaries across the whole system", securedType = ApplicationSummaryPageResource.class)
	ServiceResult<ApplicationSummaryPageResource> getSubmittedApplicationSummariesByCompetitionId(long competitionId,
																								  String sortBy,
																								  int pageIndex,
																								  int pageSize,
																								  Optional<String> filter,
																								  Optional<FundingDecisionStatus> fundingFilter,
																								  Optional<Boolean> inAssessmentPanel);

	@PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance', 'support', 'innovation_lead')")
	@SecuredBySpring(value = "READ", description = "Internal users can see all submitted Application ids across the whole system", securedType = ApplicationSummaryPageResource.class)
	ServiceResult<List<Long>> getAllSubmittedApplicationIdsByCompetitionId(long competitionId,
																		   Optional<String> filter,
																		   Optional<FundingDecisionStatus> fundingFilter);

	@PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance', 'support', 'innovation_lead')")
    @SecuredBySpring(value = "READ", description = "Internal users can see all not-yet submitted Application Summaries across the whole system", securedType = ApplicationSummaryPageResource.class)
	ServiceResult<ApplicationSummaryPageResource> getNotSubmittedApplicationSummariesByCompetitionId(long competitionId,
																									 String sortBy,
																									 int pageIndex,
																									 int pageSize);

	@PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance', 'support', 'innovation_lead')")
	@SecuredBySpring(value = "READ", description = "Internal users can see all Application Summaries with funding decisions across the whole system", securedType = ApplicationSummaryPageResource.class)
	ServiceResult<ApplicationSummaryPageResource> getWithFundingDecisionApplicationSummariesByCompetitionId(long competitionId,
																											String sortBy,
																											int pageIndex,
																											int pageSize,
																											Optional<String> filter,
																											Optional<Boolean> sendFilter,
																											Optional<FundingDecisionStatus> fundingFilter);

	@PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance', 'support', 'innovation_lead')")
	@SecuredBySpring(value = "READ", description = "Internal users can see all Application Ids with funding decisions across the whole system", securedType = ApplicationSummaryResource.class)
	ServiceResult<List<Long>> getWithFundingDecisionIsChangeableApplicationIdsByCompetitionId(long competitionId,
																						Optional<String> filter,
																						Optional<Boolean> sendFilter,
																						Optional<FundingDecisionStatus> fundingFilter);

	@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead')")
	@SecuredBySpring(value = "READ", description = "Internal users can see all Ineligable Application Summaries across the whole system", securedType = ApplicationSummaryPageResource.class)
	ServiceResult<ApplicationSummaryPageResource> getIneligibleApplicationSummariesByCompetitionId(long competitionId,
																								   String sortBy,
																								   int pageIndex,
																								   int pageSize,
																								   Optional<String> filter,
																								   Optional<Boolean> informFilter);

	@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead')")
	@SecuredBySpring(value = "READ", description = "Internal users can access application team contacts", securedType = ApplicationTeamResource.class)
	ServiceResult<ApplicationTeamResource> getApplicationTeamByApplicationId(long applicationId);
}
