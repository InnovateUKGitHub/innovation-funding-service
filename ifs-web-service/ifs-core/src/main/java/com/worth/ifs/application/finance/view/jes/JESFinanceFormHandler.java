package com.worth.ifs.application.finance.view.jes;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.application.finance.service.CostService;
import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.application.finance.view.FinanceFormHandler;
import com.worth.ifs.application.finance.view.item.CostHandler;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.exception.UnableToReadUploadedFile;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.util.MessageUtil;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JESFinanceFormHandler implements FinanceFormHandler {
	
	private static final Log LOG = LogFactory.getLog(JESFinanceFormHandler.class);
	
    @Autowired
    private CostService costService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    protected MessageSource messageSource;

    public static final String REMOVE_UPLOADED_FILE = "remove_uploaded_file";

    @Override
    public Map<String, List<String>> update(HttpServletRequest request, Long userId, Long applicationId) {
        storeCostItems(request, userId, applicationId);
        return storeJESUpload(request, userId, applicationId);
    }

    private void storeCostItems(HttpServletRequest request, Long userId, Long applicationId) {
        Enumeration<String> parameterNames = request.getParameterNames();
        while(parameterNames.hasMoreElements()) {
            String parameter = parameterNames.nextElement();
            String[] parameterValues = request.getParameterValues(parameter);

            if(parameterValues.length > 0) {
                storeCost(userId, applicationId, parameter, parameterValues[0]);
            }
        }
    }

    @Override
    public ValidationMessages storeCost(Long userId, Long applicationId, String fieldName, String value) {
        if (fieldName != null && value != null) {
            if (fieldName.startsWith("cost-")) {
                storeField(fieldName.replace("cost-", ""), value, userId, applicationId);
            }
        }
        return null;
    }

    private void storeField(String fieldName, String value, Long userId, Long applicationId) {
        FinanceFormField financeFormField = getCostFormField(fieldName, value);
        if(financeFormField==null)
            return;

        CostHandler costHandler = new AcademicFinanceHandler();
        Long costFormFieldId = 0L;
        if (financeFormField.getId() != null && !"null".equals(financeFormField.getId())) {
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
        QuestionResource question;
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
            default:
            	question = null;
            	break;
        }
        if (question != null) {
            return question.getId();
        } else {
            return null;
        }
    }

    private Map<String, List<String>> storeJESUpload(HttpServletRequest request, Long userId, Long applicationId) {
        final Map<String, String[]> params = request.getParameterMap();
        ApplicationFinanceResource applicationFinance = financeService.getApplicationFinance(userId, applicationId);

        Map<String, List<String>> errorMap = new HashMap<>();
        if (params.containsKey(REMOVE_UPLOADED_FILE)) {
            financeService.removeFinanceDocument(applicationFinance.getId()).getSuccessObjectOrThrowException();
        } else {
            final Map<String, MultipartFile> fileMap = ((StandardMultipartHttpServletRequest) request).getFileMap();
            final MultipartFile file = fileMap.get("jes-upload");
            if (file != null && !file.isEmpty()) {
                try {
                    RestResult<FileEntryResource> result = financeService.addFinanceDocument(applicationFinance.getId(),
                            file.getContentType(),
                            file.getSize(),
                            file.getOriginalFilename(),
                            file.getBytes());
                    if (result.isFailure()) {
                        errorMap.put("formInput[jes-upload]",
                                result.getFailure().getErrors().stream()
                                        .map(e ->
                                                MessageUtil.getFromMessageBundle(messageSource, e.getErrorKey(), "Unknown error on file upload", request.getLocale())).collect(Collectors.toList()));
                    }
                } catch (IOException e) {
                	LOG.error(e);
                    throw new UnableToReadUploadedFile();
                }
            }
        }
        return errorMap;
    }

    @Override
    public RestResult<ByteArrayResource> getFile(Long applicationFinanceId) {
        return financeService.getFinanceDocumentByApplicationFinance(applicationFinanceId);
    }

    @Override
    public void updateFinancePosition(Long userId, Long applicationId, String fieldName, String value) {
        // not to be implemented, there are not any finance positions for the JES form
        throw new NotImplementedException("There are not any finance positions for the JES form");
    }

    @Override
    public CostItem addCost(Long applicationId, Long userId, Long questionId) {
        // not to be implemented, can't add extra rows of finance to the JES form
        throw new NotImplementedException("Can't add extra rows of finance to the JES form");
    }
}
