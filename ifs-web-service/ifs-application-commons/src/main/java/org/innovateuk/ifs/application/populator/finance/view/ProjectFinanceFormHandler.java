package org.innovateuk.ifs.application.populator.finance.view;

import org.apache.commons.lang3.NotImplementedException;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.service.ProjectFinanceRowRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
public class ProjectFinanceFormHandler extends BaseFinanceFormHandler<ProjectFinanceRowRestService> implements FinanceFormHandler {

    private ProjectFinanceRestService projectFinanceRestService;
    private ProjectFinanceRowRestService projectFinanceRowRestService;

    @Autowired
    public ProjectFinanceFormHandler(final ProjectFinanceRestService projectFinanceRestService,
                                     final ProjectFinanceRowRestService projectFinanceRowRestService,
                                     final UnsavedFieldsManager unsavedFieldsManager) {
        super(projectFinanceRowRestService, unsavedFieldsManager);
        this.projectFinanceRestService = projectFinanceRestService;
        this.projectFinanceRowRestService = projectFinanceRowRestService;
    }

    @Override
    public ValidationMessages update(HttpServletRequest request, Long organisationId, Long projectId, Long competitionId) {
        ProjectFinanceResource projectFinanceResource = projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();

        if (projectFinanceResource == null) {
            projectFinanceResource = projectFinanceRestService.addProjectFinanceForOrganisation(projectId, organisationId).getSuccess();
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
        ProjectFinanceResource projectFinanceResource = projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();
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
            ProjectFinanceResource projectFinance = projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();
            projectFinanceRowRestService.add(projectFinance.getId(), Long.valueOf(addCostParam), null);
        }
        if (requestParams.containsKey("remove_cost")) {
            String removeCostParam = request.getParameter("remove_cost");
            getFinanceRowRestService().delete(projectId, organisationId, Long.valueOf(removeCostParam)).getSuccess();
        }
    }
}
