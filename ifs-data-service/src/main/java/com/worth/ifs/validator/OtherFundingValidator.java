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
        /*OtherFunding response = (OtherFunding) target;
        Cost cost = costRepository.findOne(response.getId());
        ApplicationFinance applicationFinance = cost.getApplicationFinance();

        ServiceResult<Question> question = questionService.getQuestionByFormInputType(CostType.OTHER_FUNDING.getType());

        if(OtherFundingCostCategory.OTHER_FUNDING.equals(response.getFundingSource()) && response.getOtherPublicFunding().equals("Yes")){
            List<Cost> otherFundingRows = costRepository.findByApplicationFinanceIdAndNameAndQuestionId(applicationFinance.getId(), "", question.getSuccessObject().getId());
            errors.reject("MinimumRows", "You should provide at least one Source of funding");
        }else{
            LOG.debug("NO "+response.getName() + " vs " +CostType.OTHER_FUNDING.getType());
        }*/
    }
}
