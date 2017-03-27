package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.FundingDecisionStatus;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

public interface ApplicationSummaryService {

	@PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
	@SecuredBySpring(value = "READ", description = "Comp Admins can see all Application Summaries across the whole system", securedType = ApplicationSummaryPageResource.class)
	ServiceResult<ApplicationSummaryPageResource> getApplicationSummariesByCompetitionId(Long competitionId,
																						 String sortBy,
																						 int pageIndex,
																						 int pageSize,
																						 Optional<String> filter);

	@PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
	@SecuredBySpring(value = "READ", description = "Comp Admins can see all submitted Application Summaries across the whole system", securedType = ApplicationSummaryPageResource.class)
	ServiceResult<ApplicationSummaryPageResource> getSubmittedApplicationSummariesByCompetitionId(Long competitionId,
																								  String sortBy,
																								  int pageIndex,
																								  int pageSize,
																								  Optional<String> filter,
																								  Optional<FundingDecisionStatus> fundingFilter);

	@PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    @SecuredBySpring(value = "READ", description = "Comp Admins can see all not-yet submitted Application Summaries across the whole system", securedType = ApplicationSummaryPageResource.class)
	ServiceResult<ApplicationSummaryPageResource> getNotSubmittedApplicationSummariesByCompetitionId(Long competitionId,
																									 String sortBy,
																									 int pageIndex,
																									 int pageSize);

	@PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    @SecuredBySpring(value = "READ", description = "Comp Admins can see all Application Summaries requiring feedback across the whole system", securedType = ApplicationSummaryPageResource.class)
	ServiceResult<ApplicationSummaryPageResource> getFeedbackRequiredApplicationSummariesByCompetitionId(Long competitionId,
																										 String sortBy,
																										 int pageIndex,
																										 int pageSize);

	@PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
	@SecuredBySpring(value = "READ", description = "Comp Admins can see all Application Summaries with funding decisions across the whole system", securedType = ApplicationSummaryPageResource.class)
	ServiceResult<ApplicationSummaryPageResource> getWithFundingDecisionApplicationSummariesByCompetitionId(long competitionId,
																											String sortBy,
																											int pageIndex,
																											int pageSize,
																											Optional<String> filter,
																											Optional<Boolean> sendFilter,
																											Optional<FundingDecisionStatus> fundingFilter);
}
