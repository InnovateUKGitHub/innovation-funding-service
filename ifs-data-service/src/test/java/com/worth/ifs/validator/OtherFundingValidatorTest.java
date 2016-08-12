package com.worth.ifs.validator;

import static com.worth.ifs.finance.handler.item.OtherFundingHandler.COST_KEY;
import static com.worth.ifs.finance.resource.category.OtherFundingCostCategory.OTHER_FUNDING;
import static com.worth.ifs.validator.ValidatorTestUtil.getBindingResult;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.worth.ifs.finance.domain.FinanceRow;
import com.worth.ifs.finance.resource.cost.FinanceRowType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import com.worth.ifs.application.builder.QuestionBuilder;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.transactional.QuestionService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.builder.ApplicationFinanceBuilder;
import com.worth.ifs.finance.builder.FinanceRowBuilder;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.repository.FinanceRowRepository;
import com.worth.ifs.finance.resource.cost.OtherFunding;
import com.worth.ifs.util.MessageUtil;

@RunWith(MockitoJUnitRunner.class)
public class OtherFundingValidatorTest {

	private Validator validator;
	
    private ReloadableResourceBundleMessageSource messageSource;

    @Mock
    private FinanceRowRepository financeRowRepository;
    
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
        expectErrors(1, Collections.singletonList(MessageUtil.getFromMessageBundle(messageSource, "validation.finance.secured.date.invalid", null, null)), otherFunding);
    }
    @Test
    public void testInvalidSecuredDateMonth() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(2L, "Yes", "Source1", "15-2014", new BigDecimal(100));
        expectErrors(1, Collections.singletonList(MessageUtil.getFromMessageBundle(messageSource, "validation.finance.secured.date.invalid", null, null)), otherFunding);
    }
    @Test
    public void testInvalidSecuredDateNoMonth() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(3L, "Yes", "Source1", "2014", new BigDecimal(100));
        expectErrors(1, Collections.singletonList(MessageUtil.getFromMessageBundle(messageSource, "validation.finance.secured.date.invalid", null, null)), otherFunding);
    }
    @Test
    public void testInvalidMinimum() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(3L, "Yes", "Source1", "12-2014", new BigDecimal(0));
        expectErrors(1, Collections.singletonList(MessageUtil.getFromMessageBundle(messageSource, "javax.validation.constraints.DecimalMin.message", null, new Integer[]{1}, null)), otherFunding);
    }
    @Test
    public void testInvalidSecuredDate() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(4L, "Yes", "Source1", "12-2014hvhvh", new BigDecimal(100));
        expectErrors(1, Collections.singletonList(MessageUtil.getFromMessageBundle(messageSource, "validation.finance.secured.date.invalid", null, null)), otherFunding);
    }
    @Test
    public void testInvalidSecuredDateNoSource() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(4L, "Yes", null, "12-2014hvhvh", new BigDecimal(100));
        expectErrors(1, Collections.singletonList(MessageUtil.getFromMessageBundle(messageSource, "validation.finance.secured.date.invalid", null, null)), otherFunding);
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

    private void expectErrors(int count, List<String> messages, OtherFunding otherFunding){
        BindingResult bindingResult = getBindingResult(otherFunding);
        validator.validate(otherFunding, bindingResult);
        assertTrue(bindingResult.hasErrors());
        assertEquals(count, bindingResult.getErrorCount());

        int i = 0;
        for(String message : messages){
            assertEquals(message, bindingResult.getAllErrors().get(i).getDefaultMessage());
            i++;
        }
    }

    private void expectNoErrors(OtherFunding otherFunding){
        BindingResult bindingResult = getBindingResult(otherFunding);
        validator.validate(otherFunding, bindingResult);
        assertFalse(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getErrorCount());
    }

    private void mockWithRadio(String value){
        ApplicationFinance applicationFinance = ApplicationFinanceBuilder.newApplicationFinance().build();
        FinanceRow cost = FinanceRowBuilder.newFinanceRow().withApplicationFinance(applicationFinance).withItem(value).build();
        Question question = QuestionBuilder.newQuestion().build();
        when(financeRowRepository.findOne(any(Long.class))).thenReturn(cost);
        when(questionService.getQuestionByFormInputType(FinanceRowType.OTHER_FUNDING.getType())).thenReturn(ServiceResult.serviceSuccess(question));
        List<FinanceRow> listOfCostWithYes = new ArrayList<>();
        listOfCostWithYes.add(cost);
        when(financeRowRepository.findByApplicationFinanceIdAndNameAndQuestionId(anyLong(), eq(COST_KEY), anyLong())).thenReturn(listOfCostWithYes);
    }
}
