package org.innovateuk.ifs.application.finance.view;

import org.apache.commons.lang3.NotImplementedException;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.service.ProjectFinanceRowRestService;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
public class ProjectFinanceFormHandler extends BaseFinanceFormHandler<ProjectFinanceRowRestService> implements FinanceFormHandler {

    private ProjectFinanceService projectFinanceService;

    @Autowired
    public ProjectFinanceFormHandler(final ProjectFinanceService projectFinanceService,
                                     final ProjectFinanceRowRestService projectFinanceRowRestService,
                                     final UnsavedFieldsManager unsavedFieldsManager) {
        super(projectFinanceRowRestService, unsavedFieldsManager);
        this.projectFinanceService = projectFinanceService;
    }

    @Override
    public ValidationMessages update(HttpServletRequest request, Long organisationId, Long projectId, Long competitionId) {
        ProjectFinanceResource projectFinanceResource = projectFinanceService.getProjectFinance(projectId, organisationId);

        if (projectFinanceResource == null) {
            projectFinanceResource = projectFinanceService.addProjectFinance(projectId, organisationId);
        }

        ValidationMessages errors = getAndStoreCostitems(request, projectFinanceResource.getId(), financeRowItem ->
                getFinanceRowRestService().update(financeRowItem));
        addRemoveCostRows(request, projectId, organisationId);

        return errors;
    }

    @Override
    public ValidationMessages storeCost(Long userId, Long projectId, String fieldName, String value, Long competitionId) {
        return null;
    }

    @Override
    public void updateFinancePosition(Long userId, Long projectId, String fieldName, String value, Long competitionId) {
        // do nothing.
    }

    @Override
    public ValidationMessages addCost(Long projectId, Long userId, Long questionId) {
        return null;
    }

    @Override
    public FinanceRowItem addCostWithoutPersisting(Long projectId, Long organisationId, Long questionId) {
        ProjectFinanceResource projectFinanceResource = projectFinanceService.getProjectFinance(projectId, organisationId);
        return getFinanceRowRestService().addWithoutPersisting(projectFinanceResource.getId(), questionId).getSuccess();
    }

    @Override
    public RestResult<ByteArrayResource> getFile(Long applicationFinanceId) {
        throw new NotImplementedException("Project finances doesn't have any upload files to process");
    }

    private void addRemoveCostRows(HttpServletRequest request, Long projectId, Long organisationId) {
        Map<String, String[]> requestParams = request.getParameterMap();
        if (requestParams.containsKey("add_cost")) {
            String addCostParam = request.getParameter("add_cost");
            ProjectFinanceResource projectFinance = projectFinanceService.getProjectFinance(projectId, organisationId);
            projectFinanceService.addCost(projectFinance.getId(), Long.valueOf(addCostParam));
        }
        if (requestParams.containsKey("remove_cost")) {
            String removeCostParam = request.getParameter("remove_cost");
            getFinanceRowRestService().delete(projectId, organisationId, Long.valueOf(removeCostParam)).getSuccess();
        }
    }
}
