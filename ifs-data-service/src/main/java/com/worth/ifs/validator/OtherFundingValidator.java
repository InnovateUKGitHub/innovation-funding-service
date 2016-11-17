package com.worth.ifs.validator;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.transactional.QuestionService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.ApplicationFinanceRow;
import com.worth.ifs.finance.domain.FinanceRow;
import com.worth.ifs.finance.repository.ApplicationFinanceRowRepository;
import com.worth.ifs.finance.resource.cost.FinanceRowType;
import com.worth.ifs.finance.resource.cost.OtherFunding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.worth.ifs.commons.rest.ValidationMessages.rejectValue;
import static com.worth.ifs.finance.handler.item.OtherFundingHandler.COST_KEY;
import static com.worth.ifs.finance.resource.category.OtherFundingCostCategory.OTHER_FUNDING;

/**
 * This class validates the FormInputResponse, it checks if the maximum word count has been exceeded.
 */
@Component
public class OtherFundingValidator implements Validator {

    private static final Log LOG = LogFactory.getLog(OtherFundingValidator.class);

    private ApplicationFinanceRowRepository financeRowRepository;
    private QuestionService questionService;

    @Override
    public boolean supports(Class<?> clazz) {
        return OtherFunding.class.equals(clazz);
    }

    @Autowired
    public OtherFundingValidator(ApplicationFinanceRowRepository financeRowRepository, QuestionService questionService) {
        this.financeRowRepository = financeRowRepository;
        this.questionService = questionService;
    }

    @Override
    public void validate(Object target, Errors errors) {
        OtherFunding otherFunding = (OtherFunding) target;
        boolean userHasSelectedYesToOtherFunding = userHasSelectedYes(otherFunding);
        String fundingSource = otherFunding.getFundingSource();
        BigDecimal fundingAmount = otherFunding.getFundingAmount();
        if(userHasSelectedYesToOtherFunding && fundingSource != null && !fundingSource.equals(OTHER_FUNDING)){
            validateDate(otherFunding, errors);
            validateFundingSource(fundingSource, errors);
            validateFundingAmount(fundingAmount, errors);
        } else if(userHasSelectedYesToOtherFunding && fundingSource == null) {
        	validateDate(otherFunding, errors);
        }
    }

    private void validateFundingAmount(BigDecimal fundingAmount, Errors errors) {
        if(fundingAmount == null || fundingAmount.compareTo(BigDecimal.ZERO) != 1){
            rejectValue(errors, "fundingAmount", "validation.field.max.value.or.higher", 1);

        }
    }

    private void validateDate(OtherFunding otherFunding, Errors errors){
        String securedDate = otherFunding.getSecuredDate();
        if(StringUtils.isBlank(securedDate)){
            rejectValue(errors, "securedDate", "validation.field.must.not.be.blank");
        }else if(!isValidDate(securedDate)) {
            rejectValue(errors, "securedDate", "validation.finance.secured.date.invalid");
        }
    }

    private void validateFundingSource(String fundingSource, Errors errors){
        if(StringUtils.isBlank(fundingSource)){
            rejectValue(errors, "fundingSource", "validation.finance.funding.source.blank");
        }

    }

    private boolean userHasSelectedYes(final OtherFunding otherFunding) {
        FinanceRow cost = financeRowRepository.findOne(otherFunding.getId());
        ApplicationFinance applicationFinance = ((ApplicationFinanceRow)cost).getTarget();
        ServiceResult<Question> question = questionService.getQuestionByFormInputType(FinanceRowType.OTHER_FUNDING.getType());
        List<ApplicationFinanceRow> otherFundingRows = financeRowRepository.findByTargetIdAndNameAndQuestionId(applicationFinance.getId(), COST_KEY, question.getSuccessObject().getId());
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
