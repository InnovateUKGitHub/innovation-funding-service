package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;

public interface ApplicationSummarisationService {

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin, project finance, support, innovation lead or stakeholder roles can read total project costs for an application")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance', 'support', 'innovation_lead', 'stakeholder')")
    ServiceResult<BigDecimal> getTotalProjectCost(Application application);

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin, project finance, support, innovation lead or stakeholder roles can read funding sought for an application")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance', 'support', 'innovation_lead', 'stakeholder')")
    ServiceResult<BigDecimal> getFundingSought(Application application);

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin, project finance, support, innovation lead, stakeholder or applicant roles can read total project funding sought for an application")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance', 'support', 'innovation_lead', 'stakeholder','applicant')")
    ServiceResult<BigDecimal> getProjectTotalFunding(Long appId);

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin, project finance, support, innovation lead, stakeholder or applicant roles can read project location")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance', 'support', 'innovation_lead', 'stakeholder','applicant')")
    ServiceResult<String> getProjectLocation(Long appId);

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin, project finance, support, innovation lead  stakeholder or applicant roles can read other funding sought for an application")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance', 'support', 'innovation_lead', 'stakeholder','applicant')")
    ServiceResult<BigDecimal> getProjectOtherFunding(Long appId);


}
