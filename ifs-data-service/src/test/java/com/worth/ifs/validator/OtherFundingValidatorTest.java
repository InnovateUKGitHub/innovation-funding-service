package com.worth.ifs.validator;

import com.worth.ifs.application.builder.QuestionBuilder;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.transactional.QuestionService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.builder.ApplicationFinanceBuilder;
import com.worth.ifs.finance.builder.ApplicationFinanceRowBuilder;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.ApplicationFinanceRow;
import com.worth.ifs.finance.repository.ApplicationFinanceRowRepository;
import com.worth.ifs.finance.resource.cost.FinanceRowType;
import com.worth.ifs.finance.resource.cost.OtherFunding;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.finance.handler.item.OtherFundingHandler.COST_KEY;
import static com.worth.ifs.finance.resource.category.OtherFundingCostCategory.OTHER_FUNDING;
import static com.worth.ifs.validator.ValidatorTestUtil.getBindingResult;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OtherFundingValidatorTest {

	private Validator validator;
	
    private ReloadableResourceBundleMessageSource messageSource;

    @Mock
    private ApplicationFinanceRowRepository financeRowRepository;
    
    @Mock
    private QuestionService questionService;
    
	
	@Before
	public void setUp() {
		messageSource = new ReloadableResourceBundleMessageSource();
        validator = new OtherFundingValidator(financeRowRepository, questionService);
    }
	
    @Test
    public void testInvalidSecuredDateYear() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(1L, "Yes", "Source1", "2342", new BigDecimal(100));
        expectError(otherFunding, "validation.finance.secured.date.invalid");
    }
    @Test
    public void testInvalidSecuredDateMonth() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(2L, "Yes", "Source1", "15-2014", new BigDecimal(100));
        expectError(otherFunding, "validation.finance.secured.date.invalid");
    }
    @Test
    public void testInvalidSecuredDateNoMonth() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(3L, "Yes", "Source1", "2014", new BigDecimal(100));
        expectError(otherFunding, "validation.finance.secured.date.invalid");
    }
    @Test
    public void testInvalidMinimum() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(3L, "Yes", "Source1", "12-2014", new BigDecimal(0));
        expectError(otherFunding, "validation.field.max.value.or.higher", 1);
    }
    @Test
    public void testInvalidSecuredDate() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(4L, "Yes", "Source1", "12-2014hvhvh", new BigDecimal(100));
        expectError(otherFunding, "validation.finance.secured.date.invalid");
    }
    @Test
    public void testInvalidSecuredDateNoSource() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(4L, "Yes", null, "12-2014hvhvh", new BigDecimal(100));
        expectError(otherFunding, "validation.finance.secured.date.invalid");
    }
    @Test
    public void testValidFullAmount() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(1L, "Yes", "Source1", "10-2014", new BigDecimal(100));
        expectNoErrors(otherFunding);
    }
    @Test
    public void testValidWithoutDate() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(2L, "Yes", OTHER_FUNDING, null, null);
        expectNoErrors(otherFunding);
    }
    @Test
    public void testValidNoOtherPublicFunding() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(3L, null, "Source1", "11-1999", new BigDecimal(5));
        expectNoErrors(otherFunding);
    }
    @Test
    public void testValid() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(4L, "Yes", "Source1", "09-1999",  new BigDecimal(5));
        expectNoErrors(otherFunding);
    }
    @Test
    public void testValidNoSource() {
        mockWithRadio("No");
        OtherFunding otherFunding = new OtherFunding(5L, "No", "", "ertt", new BigDecimal(5));
        expectNoErrors(otherFunding);
    }
    @Test
    public void testValidFullFunding() {
        mockWithRadio("No");
        OtherFunding otherFunding = new OtherFunding(6L, "Yes", "Source1", "2014", new BigDecimal(100));
        expectNoErrors(otherFunding);
    }

    private void expectError(OtherFunding otherFunding, String errorKey, Object... arguments){
        BindingResult bindingResult = getBindingResult(otherFunding);
        validator.validate(otherFunding, bindingResult);
        verifyError(bindingResult, errorKey, arguments);
    }

    private void expectNoErrors(OtherFunding otherFunding){
        BindingResult bindingResult = getBindingResult(otherFunding);
        validator.validate(otherFunding, bindingResult);
        assertFalse(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getErrorCount());
    }

    private void verifyError(BindingResult bindingResult, String errorCode, Object... expectedArguments) {
        assertTrue(bindingResult.hasErrors());
        assertEquals(1, bindingResult.getAllErrors().size());
        ObjectError actualError = bindingResult.getAllErrors().get(0);

        assertEquals(errorCode, actualError.getCode());
        assertEquals(errorCode, actualError.getDefaultMessage());
        assertArrayEquals(expectedArguments, actualError.getArguments());
    }

    private void mockWithRadio(String value){
        ApplicationFinance applicationFinance = ApplicationFinanceBuilder.newApplicationFinance().build();
        ApplicationFinanceRow cost = ApplicationFinanceRowBuilder.newFinanceRow().withApplicationFinance(applicationFinance).withItem(value).build();
        Question question = QuestionBuilder.newQuestion().build();
        when(financeRowRepository.findOne(any(Long.class))).thenReturn(cost);
        when(questionService.getQuestionByFormInputType(FinanceRowType.OTHER_FUNDING.getType())).thenReturn(ServiceResult.serviceSuccess(question));
        List<ApplicationFinanceRow> listOfCostWithYes = new ArrayList<>();
        listOfCostWithYes.add(cost);
        when(financeRowRepository.findByTargetIdAndNameAndQuestionId(anyLong(), eq(COST_KEY), anyLong())).thenReturn(listOfCostWithYes);
    }
}
