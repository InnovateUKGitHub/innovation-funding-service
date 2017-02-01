package org.innovateuk.ifs.application.transactional;

import java.math.BigDecimal;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.springframework.security.access.prepost.PreAuthorize;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;

public interface ApplicationSummarisationService {

	@SecuredBySpring(value = "TODO", description = "TODO")
	@PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
	ServiceResult<BigDecimal> getTotalProjectCost(Application application);

	@SecuredBySpring(value = "TODO", description = "TODO")
	@PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
	ServiceResult<BigDecimal> getFundingSought(Application application);
}
