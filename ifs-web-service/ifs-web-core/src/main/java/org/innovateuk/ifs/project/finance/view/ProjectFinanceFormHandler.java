package org.innovateuk.ifs.project.finance.view;

import org.apache.commons.lang3.NotImplementedException;
import org.innovateuk.ifs.application.finance.model.FinanceFormField;
import org.innovateuk.ifs.application.finance.service.FinanceRowService;
import org.innovateuk.ifs.application.finance.view.BaseFinanceFormHandler;
import org.innovateuk.ifs.application.finance.view.FinanceFormHandler;
import org.innovateuk.ifs.application.finance.view.UnsavedFieldsManager;
import org.innovateuk.ifs.application.finance.view.item.FinanceRowHandler;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ProjectFinanceRowRestService;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRowService;
import org.innovateuk.ifs.util.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ProjectFinanceFormHandler extends BaseFinanceFormHandler implements FinanceFormHandler {
    @Autowired
    private ProjectFinanceRowService projectFinanceRowService;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @Autowired
    private FinanceRowService financeRowService;

    @Autowired
    private ProjectFinanceRowRestService projectFinanceRowRestService;

    @Autowired
    private UnsavedFieldsManager unsavedFieldsManager;

    @Override
    public ValidationMessages update(HttpServletRequest request, Long userId, Long projectId, Long competitionId) {
        ProjectFinanceResource projectFinanceResource = projectFinanceService.getProjectFinance(userId, projectId);
        if (projectFinanceResource == null) {
            projectFinanceResource = projectFinanceService.addProjectFinance(userId, projectId);
        }

        ValidationMessages errors = getAndStoreCostitems(request, projectFinanceResource.getId());
        addRemoveCostRows(request, projectId, userId);

        return errors;
    }

    @Override
    public ValidationMessages storeCost(Long userId, Long projectId, String fieldName, String value, Long competitionId) {
        return null;
    }

    @Override
    public void updateFinancePosition(Long userId, Long projectId, String fieldName, String value, Long competitionId) {

    }

    @Override
    public ValidationMessages addCost(Long projectId, Long userId, Long questionId) {
        return null;
    }

    @Override
    public FinanceRowItem addCostWithoutPersisting(Long projectId, Long organisationId, Long questionId) {
        ProjectFinanceResource projectFinanceResource = projectFinanceService.getProjectFinance(projectId, organisationId);
        return projectFinanceRowService.addWithoutPersisting(projectFinanceResource.getId(), questionId);
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
            financeRowService.delete(Long.valueOf(removeCostParam));
        }
    }

    /**
     * Retrieve the cost items from the request based on their type
     */
    protected List<Either<FinanceRowItem, ValidationMessages>> getFinanceRowItems(Map<Long, List<FinanceFormField>> costFieldMap, FinanceRowType costType, Long projectFinanceId) {
        List<Either<FinanceRowItem, ValidationMessages>> costItems = new ArrayList<>();

        if(costFieldMap.size() == 0) {
            return costItems;
        }
        FinanceRowHandler financeRowHandler = getFinanceRowItemHandler(costType);

        // create new cost items
        for (Map.Entry<Long, List<FinanceFormField>> entry : costFieldMap.entrySet()) {
            try{
                Long id = entry.getKey();
                List<FinanceFormField> fields = entry.getValue();

                if(id == -1L) {
                    List<List<FinanceFormField>> fieldsSeparated = unsavedFieldsManager.separateFields(fields);
                    for(List<FinanceFormField> fieldGroup: fieldsSeparated) {
                        FinanceRowItem costItem = financeRowHandler.toFinanceRowItem(null, fieldGroup);
                        if (costItem != null && fieldGroup.size() > 0) {
                            Long questionId = Long.valueOf(fieldGroup.get(0).getQuestionId());
                            ValidationMessages addResult = projectFinanceRowService.add(projectFinanceId, questionId, costItem);
                            Either<FinanceRowItem, ValidationMessages> either;
                            if(addResult.hasErrors()) {
                                either = Either.right(addResult);
                            } else {
                                FinanceRowItem added = financeRowService.findById(addResult.getObjectId());
                                either = Either.left(added);
                            }

                            costItems.add(either);
                        }
                    }
                } else {
                    FinanceRowItem costItem = financeRowHandler.toFinanceRowItem(id, fields);
                    if (costItem != null) {
                        Either<FinanceRowItem, ValidationMessages> either = Either.left(costItem);
                        costItems.add(either);
                    }
                }

            }catch(NumberFormatException e){
                ValidationMessages validationMessages = getValidationMessageFromException(entry, e);
                Either<FinanceRowItem, ValidationMessages> either = Either.right(validationMessages);
                costItems.add(either);
            }
        }
        return costItems;
    }
}
