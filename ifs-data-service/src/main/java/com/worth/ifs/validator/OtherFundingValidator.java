package com.worth.ifs.validator;

import com.worth.ifs.application.transactional.QuestionService;
import com.worth.ifs.finance.handler.OrganisationFinanceDelegate;
import com.worth.ifs.finance.repository.CostRepository;
import com.worth.ifs.finance.resource.cost.OtherFunding;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * This class validates the FormInputResponse, it checks if the maximum word count has been exceeded.
 */
@Component
public class OtherFundingValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return OtherFunding.class.equals(clazz);
    }
    private static final Log LOG = LogFactory.getLog(OtherFundingValidator.class);

    static OrganisationFinanceDelegate organisationFinanceDelegate;
    static CostRepository costRepository;
    static QuestionService questionService;

    @Autowired
    public OtherFundingValidator(OrganisationFinanceDelegate organisationFinanceDelegate, CostRepository costRepository, QuestionService questionService) {
        this.organisationFinanceDelegate = organisationFinanceDelegate;
        this.costRepository = costRepository;
        this.questionService = questionService;
    }

    @Override
    public void validate(Object target, Errors errors) {
        // Not required for now as covered by empty row validation.  May need additional validation for date.
    }
}
