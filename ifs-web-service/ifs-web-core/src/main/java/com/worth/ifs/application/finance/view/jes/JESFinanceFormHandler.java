package com.worth.ifs.application.finance.view.jes;

import com.worth.ifs.application.finance.model.FinanceFormField;
import com.worth.ifs.application.finance.service.FinanceRowService;
import com.worth.ifs.application.finance.service.FinanceService;
import com.worth.ifs.application.finance.view.FinanceFormHandler;
import com.worth.ifs.application.finance.view.item.FinanceRowHandler;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.exception.UnableToReadUploadedFile;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import static com.worth.ifs.commons.error.Error.fieldError;
import static com.worth.ifs.commons.rest.ValidationMessages.noErrors;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;

@Component
public class JESFinanceFormHandler implements FinanceFormHandler {
	
	private static final Log LOG = LogFactory.getLog(JESFinanceFormHandler.class);

    @Autowired
    private FinanceRowService financeRowService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private QuestionService questionService;

    private static final String REMOVE_FINANCE_DOCUMENT = "remove_finance_document";

    @Override
    public ValidationMessages update(HttpServletRequest request, Long userId, Long applicationId, Long competitionId) {
        storeFinanceRowItems(request, userId, applicationId, competitionId);
        return storeJESUpload(request, userId, applicationId);
    }

    private void storeFinanceRowItems(HttpServletRequest request, Long userId, Long applicationId, Long competitionId) {
        Enumeration<String> parameterNames = request.getParameterNames();
        while(parameterNames.hasMoreElements()) {
            String parameter = parameterNames.nextElement();
            String[] parameterValues = request.getParameterValues(parameter);

            if(parameterValues.length > 0) {
                storeCost(userId, applicationId, parameter, parameterValues[0], competitionId);
            }
        }
    }

    @Override
    public ValidationMessages storeCost(Long userId, Long applicationId, String fieldName, String value, Long competitionId) {
        if (fieldName != null && value != null) {
            if (fieldName.startsWith("cost-")) {
                return storeField(fieldName.replace("cost-", ""), value, userId, applicationId, competitionId);
            }
        }
        return null;
    }

    private ValidationMessages storeField(String fieldName, String value, Long userId, Long applicationId, Long competitionId) {
        FinanceFormField financeFormField = getCostFormField(competitionId, fieldName, value);
        if(financeFormField==null)
            return null;

        FinanceRowHandler financeRowHandler = new AcademicFinanceHandler();
        Long costFormFieldId = 0L;
        if (financeFormField.getId() != null && !"null".equals(financeFormField.getId())) {
            costFormFieldId = Long.parseLong(financeFormField.getId());
        }
        FinanceRowItem costItem = financeRowHandler.toFinanceRowItem(costFormFieldId, Arrays.asList(financeFormField));
        if(costItem != null) {
        	return storeFinanceRowItem(costItem, userId, applicationId, financeFormField.getQuestionId());
        } else {
        	return ValidationMessages.noErrors();
        }
    }

    private FinanceFormField getCostFormField(Long competitionId, String costTypeKey, String value) {
        // check for question id
        String[] keyParts = costTypeKey.split("-");
        if (keyParts.length == 2) {
            Long questionId = getQuestionId(competitionId, keyParts[1]);
            return new FinanceFormField(costTypeKey, value, keyParts[0], String.valueOf(questionId), keyParts[1], "");
        }
        return null;
    }

    private ValidationMessages storeFinanceRowItem(FinanceRowItem costItem, Long userId, Long applicationId, String question) {
        if (costItem.getId().equals(0L)) {
            addFinanceRowItem(costItem, userId, applicationId, question);
        } else {
            RestResult<ValidationMessages> messages = financeRowService.update(costItem);
            ValidationMessages validationMessages = messages.getSuccessObject();

            if (validationMessages == null || validationMessages.getErrors() == null || validationMessages.getErrors().isEmpty()) {
                LOG.debug("no validation errors on cost items");
                return messages.getSuccessObject();
            } else {
                messages.getSuccessObject().getErrors().stream()
                        .peek(e -> LOG.debug(String.format("Got cost item Field error: %s", e.getErrorKey())));
                return messages.getSuccessObject();
            }
        }
        return null;
    }

    private void addFinanceRowItem(FinanceRowItem costItem, Long userId, Long applicationId, String question) {
        ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId);

        if (question != null && !question.isEmpty()) {
            Long questionId = Long.parseLong(question);
            financeRowService.add(applicationFinanceResource.getId(), questionId, costItem);
        }
    }

    private Long getQuestionId(Long competitionId, String costFieldName) {
        QuestionResource question;
        switch (costFieldName) {
            case "tsb_reference":
                question = questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, "your_finance").getSuccessObject();
                break;
            case "incurred_staff":
                question = questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, "labour").getSuccessObject();
                break;
            case "incurred_travel_subsistence":
                question = questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, "travel").getSuccessObject();
                break;
            case "incurred_other_costs":
                question = questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, "materials").getSuccessObject();
                break;
            case "allocated_investigators":
                question = questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, "labour").getSuccessObject();
                break;
            case "allocated_estates_costs":
                question = questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, "other_costs").getSuccessObject();
                break;
            case "allocated_other_costs":
                question = questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, "other_costs").getSuccessObject();
                break;
            case "indirect_costs":
                question = questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, "overheads").getSuccessObject();
                break;
            case "exceptions_staff":
                question = questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, "labour").getSuccessObject();
                break;
            case "exceptions_other_costs":
                question = questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, "other_costs").getSuccessObject();
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

    private ValidationMessages storeJESUpload(HttpServletRequest request, Long userId, Long applicationId) {

        final Map<String, String[]> params = request.getParameterMap();

        if (params.containsKey(REMOVE_FINANCE_DOCUMENT)) {
            ApplicationFinanceResource applicationFinance = financeService.getApplicationFinance(userId, applicationId);
            financeService.removeFinanceDocument(applicationFinance.getId()).getSuccessObjectOrThrowException();
        } else {
            final Map<String, MultipartFile> fileMap = ((StandardMultipartHttpServletRequest) request).getFileMap();
            final MultipartFile file = fileMap.get("jes-upload");
            if (file != null && !file.isEmpty()) {
            	ApplicationFinanceResource applicationFinance = financeService.getApplicationFinance(userId, applicationId);
                try {
                    RestResult<FileEntryResource> result = financeService.addFinanceDocument(applicationFinance.getId(),
                            file.getContentType(),
                            file.getSize(),
                            file.getOriginalFilename(),
                            file.getBytes());

                    if (result.isFailure()) {

                        List<Error> errors = simpleMap(result.getFailure().getErrors(),
                                e -> fieldError("formInput[jes-upload]", e.getFieldRejectedValue(), e.getErrorKey(), e.getArguments()));

                        return new ValidationMessages(errors);
                    }
                } catch (IOException e) {
                	LOG.error(e);
                    throw new UnableToReadUploadedFile();
                }
            }
        }

        return noErrors();
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
    public ValidationMessages addCost(Long applicationId, Long userId, Long questionId) {
        // not to be implemented, can't add extra rows of finance to the JES form
        throw new NotImplementedException("Can't add extra rows of finance to the JES form");
    }

	@Override
	public FinanceRowItem addCostWithoutPersisting(Long applicationId, Long userId, Long questionId) {
		// not to be implemented, can't add extra rows of finance to the JES form
        throw new NotImplementedException("Can't add extra rows of finance to the JES form");
	}
}
