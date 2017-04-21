package org.innovateuk.ifs.project.finance;

import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.service.ProjectFinanceRowRestService;
import org.innovateuk.ifs.project.finance.resource.*;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * A service for dealing with a Project's finance operations
 */
@Service
public class ProjectFinanceServiceImpl implements ProjectFinanceService {

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Autowired
    private ProjectFinanceRowRestService projectFinanceRowRestService;

    @Override
    public List<ProjectFinanceResource> getProjectFinances(Long projectId) {
        return projectFinanceRestService.getProjectFinances(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public ViabilityResource getViability(Long projectId, Long organisationId) {
        return projectFinanceRestService.getViability(projectId, organisationId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> saveViability(Long projectId, Long organisationId, Viability viability, ViabilityRagStatus viabilityRagRating) {
        return projectFinanceRestService.saveViability(projectId, organisationId, viability, viabilityRagRating).toServiceResult();
    }

    @Override
    public EligibilityResource getEligibility(Long projectId, Long organisationId) {
        return projectFinanceRestService.getEligibility(projectId, organisationId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> saveEligibility(Long projectId, Long organisationId, Eligibility eligibility, EligibilityRagStatus eligibilityRagStatus) {
        return projectFinanceRestService.saveEligibility(projectId, organisationId, eligibility, eligibilityRagStatus).toServiceResult();
    }

    @Override
    public boolean isCreditReportConfirmed(Long projectId, Long organisationId) {
        return projectFinanceRestService.isCreditReportConfirmed(projectId, organisationId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> saveCreditReportConfirmed(Long projectId, Long organisationId, boolean confirmed) {
        return projectFinanceRestService.saveCreditReportConfirmed(projectId, organisationId, confirmed).toServiceResult();
    }

    @Override
    public List<ProjectFinanceResource> getProjectFinanceTotals(Long projectId) {
        return projectFinanceRestService.getFinanceTotals(projectId).handleSuccessOrFailure(
                failure -> Collections.<ProjectFinanceResource>emptyList(),
                success -> success
        );
    }

    @Override
    public ProjectFinanceResource addProjectFinance(Long projectId, Long organisationId) {
        return projectFinanceRestService.addProjectFinanceForOrganisation(projectId, organisationId).getSuccessObjectOrThrowException();
    }

    @Override
    public ProjectFinanceResource getProjectFinance(Long projectId, Long organisationId) {
        return projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccessObjectOrThrowException();
    }

    @Override
    public ValidationMessages addCost(Long projectFinanceId, Long questionId) {
        return projectFinanceRowRestService.add(projectFinanceId, questionId, null).getSuccessObjectOrThrowException();
    }
}
