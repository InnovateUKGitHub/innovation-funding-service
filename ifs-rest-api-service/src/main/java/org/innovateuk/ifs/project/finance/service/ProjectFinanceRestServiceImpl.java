package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;
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
        return getWithRestResult(PROJECT_FINANCE_REST_URL + "/" + projectId + "/organisation/" + organisationId + "/finance-details", ProjectFinanceResource.class);
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
        return getWithRestResult(PROJECT_FINANCE_REST_URL + "/finance-totals/" + applicationId, projectFinanceResourceListType());
    }

    @Override
    public RestResult<Boolean> hasAnyProjectOrganisationSizeChangedFromApplication(long projectId) {
        return getWithRestResult(format(PROJECT_FINANCE_REST_URL + "/" + projectId + "/finance/has-organisation-size-changed"), Boolean.class);
    }
}