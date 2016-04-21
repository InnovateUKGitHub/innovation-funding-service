package com.worth.ifs.validator;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.transactional.QuestionService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.repository.CostRepository;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.CostType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

/**
 * This class validates the FormInputResponse, it checks if the maximum word count has been exceeded.
 */
@Component
public class EmptyRowValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return CostItem.class.equals(clazz);
    }
    private static final Log LOG = LogFactory.getLog(EmptyRowValidator.class);

    static CostRepository costRepository;
    static QuestionService questionService;

    @Autowired
    public EmptyRowValidator(CostRepository costRepository, QuestionService questionService) {
        this.costRepository = costRepository;
        this.questionService = questionService;
    }

    @Override
    public void validate(Object target, Errors errors) {
        CostItem response = (CostItem) target;
        Cost cost = costRepository.findOne(response.getId());
        ApplicationFinance applicationFinance = cost.getApplicationFinance();
        ServiceResult<Question> question = questionService.getQuestionByFormInputType(CostType.OTHER_FUNDING.getType());
        List<Cost> otherFundingRows = costRepository.findByApplicationFinanceIdAndNameAndQuestionId(applicationFinance.getId(), "", question.getSuccessObject().getId());
        errors.reject("MinimumRows", "You should provide at least one Source of funding");
    }
}
