package org.innovateuk.ifs.project.finance.service;

import org.apache.commons.lang3.NotImplementedException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.resource.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.projectFinanceResourceListType;

/**
 * Rest Service for dealing with Project finance operations
 */
@Service
public class ProjectFinanceRestServiceImpl extends BaseRestService implements ProjectFinanceRestService {
    private static final String PROJECT_FINANCE_REST_URL = "/project";

    @Override
    public RestResult<List<ProjectFinanceResource>> getProjectFinances(Long projectId) {
        return getWithRestResult(PROJECT_FINANCE_REST_URL + "/" + projectId + "/project-finances", projectFinanceResourceListType());
    }

    @Override
    public RestResult<ProjectFinanceResource> getProjectFinance(Long projectId, Long organisationId) {
        return getWithRestResult(PROJECT_FINANCE_REST_URL + "/" + projectId + "/organisation/" + organisationId + "/financeDetails", ProjectFinanceResource.class);
    }

    @Override
    public RestResult<ViabilityResource> getViability(Long projectId, Long organisationId) {
        return getWithRestResult(PROJECT_FINANCE_REST_URL + "/" + projectId + "/partner-organisation/" + organisationId + "/viability", ViabilityResource.class);
    }

    @PostMapping("/{projectId}/partner-organisation/{organisationId}/viability/{viability}")
    public RestResult<Void> saveViability(Long projectId, Long organisationId, Viability viability, ViabilityRagStatus viabilityRagStatus) {

        String postUrl = PROJECT_FINANCE_REST_URL + "/" + projectId + "/partner-organisation/" + organisationId +
                "/viability/" + viability.name() + "/" + viabilityRagStatus.name();

        return postWithRestResult(postUrl, Void.class);
    }

    @Override
    public RestResult<EligibilityResource> getEligibility(Long projectId, Long organisationId) {
        return getWithRestResult(PROJECT_FINANCE_REST_URL + "/" + projectId + "/partner-organisation/" + organisationId + "/eligibility", EligibilityResource.class);
    }

    @Override
    public RestResult<Void> saveEligibility(Long projectId, Long organisationId, EligibilityState eligibility, EligibilityRagStatus eligibilityRagStatus) {

        String postUrl = PROJECT_FINANCE_REST_URL + "/" + projectId + "/partner-organisation/" + organisationId +
                "/eligibility/" + eligibility.name() + "/" + eligibilityRagStatus.name();

        return postWithRestResult(postUrl, Void.class);
    }

    @Override
    public RestResult<Boolean> isCreditReportConfirmed(Long projectId, Long organisationId) {
        String url = PROJECT_FINANCE_REST_URL + "/" + projectId + "/partner-organisation/" + organisationId + "/credit-report";
        return getWithRestResult(url, Boolean.class);
    }

    @Override
    public RestResult<Void> saveCreditReportConfirmed(Long projectId, Long organisationId, boolean confirmed) {
        String url = PROJECT_FINANCE_REST_URL + "/" + projectId + "/partner-organisation/" + organisationId + "/credit-report/" + confirmed;
        return postWithRestResult(url);
    }

    @Override
    public RestResult<List<ProjectFinanceResource>> getFinanceTotals(Long applicationId) {
        return getWithRestResult(PROJECT_FINANCE_REST_URL + "/financeTotals/" + applicationId, projectFinanceResourceListType());
    }

    @Override
    public RestResult<ProjectFinanceResource> addProjectFinanceForOrganisation(Long projectId, Long organisationId) {
        throw new NotImplementedException("Adding of project finance organisation will usually not be necessary as they are added when project is created");
    }
}
