package com.worth.ifs.validator;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.transactional.QuestionService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.repository.CostRepository;
import com.worth.ifs.finance.resource.cost.CostType;
import com.worth.ifs.finance.resource.cost.OtherFunding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.worth.ifs.finance.handler.item.OtherFundingHandler.COST_KEY;
import static com.worth.ifs.finance.resource.category.OtherFundingCostCategory.OTHER_FUNDING;

/**
 * This class validates the FormInputResponse, it checks if the maximum word count has been exceeded.
 */
@Component
public class OtherFundingValidator implements Validator {

    private static final Log LOG = LogFactory.getLog(OtherFundingValidator.class);

    private CostRepository costRepository;
    private QuestionService questionService;

    @Override
    public boolean supports(Class<?> clazz) {
        return OtherFunding.class.equals(clazz);
    }

    @Autowired
    public OtherFundingValidator(CostRepository costRepository, QuestionService questionService) {
        this.costRepository = costRepository;
        this.questionService = questionService;
    }

    @Override
    public void validate(Object target, Errors errors) {
        OtherFunding otherFunding = (OtherFunding) target;
        boolean userHasSelectedYesToOtherFunding = userHasSelectedYes(otherFunding);
        String fundingSource = otherFunding.getFundingSource();
        if(userHasSelectedYesToOtherFunding && fundingSource != null && !fundingSource.equals(OTHER_FUNDING)){
            validateDate(otherFunding, errors);
            validateFundingSource(fundingSource, errors);
        }
    }

    private void validateDate(OtherFunding otherFunding, Errors errors){
        String securedDate = otherFunding.getSecuredDate();
        if(StringUtils.isNotBlank(securedDate) && !isValidDate(securedDate)) {
            errors.rejectValue("securedDate", "validation.finance.secured.date.invalid");
        }
    }

    private void validateFundingSource(String fundingSource, Errors errors){
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fundingSource", "validation.finance.funding.source.blank");
    }

    private boolean userHasSelectedYes(final OtherFunding otherFunding) {
        Cost cost = costRepository.findOne(otherFunding.getId());
        ApplicationFinance applicationFinance = cost.getApplicationFinance();
        ServiceResult<Question> question = questionService.getQuestionByFormInputType(CostType.OTHER_FUNDING.getType());
        List<Cost> otherFundingRows = costRepository.findByApplicationFinanceIdAndNameAndQuestionId(applicationFinance.getId(), COST_KEY, question.getSuccessObject().getId());
        return otherFundingRows.size() > 0 && otherFundingRows.get(0).getItem().equals("Yes");
    }

    private boolean isValidDate(final String input){
        SimpleDateFormat format = new SimpleDateFormat("MM-yyyy");
        format.setLenient(false);
        try {
            Date dt = format.parse(input);
            return format.format(dt).equals(input);
        } catch(ParseException e){
            return false;
        }
    }
}
