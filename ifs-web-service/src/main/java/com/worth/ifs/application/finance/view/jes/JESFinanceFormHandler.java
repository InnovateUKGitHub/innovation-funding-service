package com.worth.ifs.application.finance.view.jes;

import com.worth.ifs.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.application.finance.service.CostService;
import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.application.finance.view.FinanceFormHandler;
import com.worth.ifs.application.finance.view.item.CostHandler;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.CostType;
import com.worth.ifs.form.domain.FormInputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Component
public class JESFinanceFormHandler implements FinanceFormHandler {
    @Autowired
    private CostService costService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ApplicationService applicationService;

    @Override
    public void update(HttpServletRequest request, Long userId, Long applicationId) {

    }

    @Override
    public void storeCost(Long userId, Long applicationId, String fieldName, String value) {
        if (fieldName != null && value != null) {
            String cleanedFieldName = fieldName;
            if (fieldName.startsWith("cost-")) {
                cleanedFieldName = fieldName.replace("cost-", "");
            }
            storeField(cleanedFieldName, value, userId, applicationId);
        }
    }

    private void storeField(String fieldName, String value, Long userId, Long applicationId) {
        ApplicationResource application = applicationService.getById(applicationId);
        FinanceFormField financeFormField = getCostFormField(fieldName, value);
        CostHandler costHandler = new AcademicFinanceHandler();
        Long costFormFieldId = 0L;
        if (financeFormField.getId() != null && !financeFormField.getId().equals("null")) {
            costFormFieldId = Long.parseLong(financeFormField.getId());
        }
        CostItem costItem = costHandler.toCostItem(costFormFieldId, Arrays.asList(financeFormField));
        storeCostItem(costItem, userId, applicationId, financeFormField.getQuestionId());
    }

    private FinanceFormField getCostFormField(String costTypeKey, String value) {
        // check for question id
        String[] keyParts = costTypeKey.split("-");
        if (keyParts.length == 2) {
            Long questionId = getQuestionId(keyParts[1]);
            return new FinanceFormField(costTypeKey, value, keyParts[0], String.valueOf(questionId), keyParts[1], "");
        }
        return null;
    }

    private void storeCostItem(CostItem costItem, Long userId, Long applicationId, String question) {
        if (costItem.getId().equals(0L)) {
            addCostItem(costItem, userId, applicationId, question);
        } else {
            costService.update(costItem);
        }
    }

    private void addCostItem(CostItem costItem, Long userId, Long applicationId, String question) {
        ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId);

        if (question != null && !question.isEmpty()) {
            Long questionId = Long.parseLong(question);
            costService.add(applicationFinanceResource.getId(), questionId, costItem);
        }
    }

    private Long getQuestionId(String costFieldName) {
        Question question = null;
        switch (costFieldName) {
            case "tsb_reference":
                question = questionService.getQuestionByFormInputType("your_finance").getSuccessObject();
                break;
            case "incurred_staff":
                question = questionService.getQuestionByFormInputType("labour").getSuccessObject();
                break;
            case "incurred_travel_subsistence":
                question = questionService.getQuestionByFormInputType("travel").getSuccessObject();
                break;
            case "incurred_other_costs":
                question = questionService.getQuestionByFormInputType("materials").getSuccessObject();
                break;
            case "allocated_investigators":
                question = questionService.getQuestionByFormInputType("labour").getSuccessObject();
                break;
            case "allocated_estates_costs":
                question = questionService.getQuestionByFormInputType("other_costs").getSuccessObject();
                break;
            case "allocated_other_costs":
                question = questionService.getQuestionByFormInputType("other_costs").getSuccessObject();
                break;
            case "indirect_costs":
                question = questionService.getQuestionByFormInputType("overheads").getSuccessObject();
                break;
            case "exceptions_staff":
                question = questionService.getQuestionByFormInputType("labour").getSuccessObject();
                break;
            case "exceptions_other_costs":
                question = questionService.getQuestionByFormInputType("other_costs").getSuccessObject();
                break;
        }
        if (question != null) {
            return question.getId();
        } else {
            return null;
        }
    }

    @Override
    public void updateFinancePosition(Long userId, Long applicationId, String fieldName, String value) {

    }

    @Override
    public CostItem addCost(Long applicationId, Long userId, Long questionId) {
        return null;
    }
}
