package com.worth.ifs.validator.transactional;

import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.finance.transactional.CostService;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.repository.FormInputResponseRepository;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.validator.util.ValidationUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to validate serveral objects
 */
@Service
public class ValidatorServiceImpl extends BaseTransactionalService implements ValidatorService {

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private CostService costService;

    @Autowired
    private ValidationUtil validationUtil;

    private static final Log LOG = LogFactory.getLog(ValidatorServiceImpl.class);

    @Override
    public List<BindingResult> validateFormInputResponse(Long applicationId, Long formInputId){
        List<BindingResult> results = new ArrayList<>();
        List<FormInputResponse> response = formInputResponseRepository.findByApplicationIdAndFormInputId(applicationId, formInputId);
        if(!response.isEmpty()) {
            for (FormInputResponse formInputResponse : response) {
                results.add(validationUtil.validateResponse(formInputResponse, false));
            }
        }
        return results;
    }

    @Override
    public BindingResult validateFormInputResponse(Long applicationId, Long formInputId, Long markedAsCompleteById) {
        FormInputResponse response = formInputResponseRepository.findByApplicationIdAndUpdatedByIdAndFormInputId(applicationId, markedAsCompleteById, formInputId);
        return validationUtil.validateResponse(response, false);
    }


    @Override
    public List<ValidationMessages> validateCostItem(Long applicationId, Long questionId, Long markedAsCompleteById) {
        return getProcessRole(markedAsCompleteById).andOnSuccess(role -> {
            return costService.financeDetails(applicationId, role.getOrganisation().getId()).andOnSuccess(financeDetails -> {
                return costService.getCostItems(financeDetails.getId(), questionId).andOnSuccessReturn(costItems -> {
                    LOG.debug("=======Got Cost Items : count 2: "+costItems.size());
                    return validationUtil.validateCostItem(costItems);
                });
            });
        }).getSuccessObject();
    }
}
