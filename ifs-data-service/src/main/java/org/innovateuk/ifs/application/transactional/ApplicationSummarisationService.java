package org.innovateuk.ifs.application.transactional;

import java.math.BigDecimal;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.springframework.security.access.prepost.PreAuthorize;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;

public interface ApplicationSummarisationService {

	@SecuredBySpring(value = "READ", description = "Only those with either comp admin or project finance roles can read total project costs for an application")
	@PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
	ServiceResult<BigDecimal> getTotalProjectCost(Application application);

	@SecuredBySpring(value = "READ", description = "Only those with either comp admin or project finance roles can read funding sought for an application")
	@PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
	ServiceResult<BigDecimal> getFundingSought(Application application);
}
