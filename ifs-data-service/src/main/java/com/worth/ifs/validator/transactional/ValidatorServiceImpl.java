package com.worth.ifs.validator.transactional;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.finance.handler.OrganisationFinanceDelegate;
import com.worth.ifs.finance.handler.item.FinanceRowHandler;
import com.worth.ifs.finance.mapper.ApplicationFinanceRowMapper;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.transactional.FinanceRowService;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.form.repository.FormInputRepository;
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

    private static final String FORM_INPUT_TYPE_APPLICATION_DETAILS = "application_details";
    @Autowired
    private FormInputResponseRepository formInputResponseRepository;
    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private OrganisationFinanceDelegate organisationFinanceDelegate;

    @Autowired
    private FinanceRowService financeRowService;

    @Autowired
    private ApplicationFinanceRowMapper applicationFinanceRowMapper;

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
        
        FormInput formInput = formInputRepository.findOne(formInputId);
        if(formInput.getFormInputType().getTitle().equals(FORM_INPUT_TYPE_APPLICATION_DETAILS)){
            Application application = applicationRepository.findOne(applicationId);
            results.add(validationUtil.validationApplicationDetails(application));
        }

        return results;
    }

    @Override
    public BindingResult validateFormInputResponse(Long applicationId, Long formInputId, Long markedAsCompleteById) {
        FormInputResponse response = formInputResponseRepository.findByApplicationIdAndUpdatedByIdAndFormInputId(applicationId, markedAsCompleteById, formInputId);
        return validationUtil.validateResponse(response, false);
    }


    @Override
    public List<ValidationMessages> validateCostItem(Long applicationId, Question question, Long markedAsCompleteById) {
        return getProcessRole(markedAsCompleteById).andOnSuccess(role ->
             financeRowService.financeDetails(applicationId, role.getOrganisation().getId()).andOnSuccess(financeDetails ->
                 financeRowService.getCostItems(financeDetails.getId(), question.getId()).andOnSuccessReturn(costItems ->
                    validationUtil.validateCostItem(costItems, question)
                )
            )
        ).getSuccessObject();
    }

    @Override
    public FinanceRowHandler getCostHandler(FinanceRowItem costItem){
        return financeRowService.getCostHandler(costItem.getId());
    }
}
