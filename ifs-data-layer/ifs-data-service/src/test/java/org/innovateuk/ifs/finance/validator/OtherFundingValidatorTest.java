package org.innovateuk.ifs.finance.validator;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.validator.ValidatorTestUtil;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder;
import org.innovateuk.ifs.finance.builder.ApplicationFinanceRowBuilder;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;
import org.innovateuk.ifs.form.builder.QuestionBuilder;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.transactional.QuestionService;
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

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.handler.item.OtherFundingHandler.COST_KEY;
import static org.innovateuk.ifs.finance.resource.category.OtherFundingCostCategory.OTHER_FUNDING;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OtherFundingValidatorTest {

	private Validator validator;

    @Mock
    private ApplicationFinanceRowRepository financeRowRepository;
    
    @Mock
    private QuestionService questionService;
    
	
	@Before
	public void setUp() {
        validator = new OtherFundingValidator(financeRowRepository, questionService);
    }
	
    @Test
    public void invalidSecuredDateYear() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(1L, "Yes", "Source1", "2342", new BigDecimal(100));
        expectError(otherFunding, "validation.finance.secured.date.invalid");
    }
    @Test
    public void invalidSecuredDateMonth() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(2L, "Yes", "Source1", "15-2014", new BigDecimal(100));
        expectError(otherFunding, "validation.finance.secured.date.invalid");
    }
    @Test
    public void invalidSecuredDateNoMonth() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(3L, "Yes", "Source1", "2014", new BigDecimal(100));
        expectError(otherFunding, "validation.finance.secured.date.invalid");
    }
    @Test
    public void invalidMinimum() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(3L, "Yes", "Source1", "12-2014", new BigDecimal(0));
        expectError(otherFunding, "validation.field.max.value.or.higher", 1);
    }
    @Test
    public void invalidFundingAmountNull() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(3L, "Yes", "Source1", "12-2014", null);
        expectError(otherFunding, "validation.field.must.not.be.blank");
    }
    @Test
    public void invalidSecuredDate() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(4L, "Yes", "Source1", "12-2014hvhvh", new BigDecimal(100));
        expectError(otherFunding, "validation.finance.secured.date.invalid");
    }
    @Test
    public void invalidSecuredDateNoSource() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(4L, "Yes", null, "12-2014hvhvh", new BigDecimal(100));
        expectError(otherFunding, "validation.finance.secured.date.invalid");
    }
    @Test
    public void invalidOtherPublicFunding() {
        mockWithRadio("Bobbins");
        OtherFunding otherFunding = new OtherFunding(4L, "Bobbins", OTHER_FUNDING, null, null);
        expectError(otherFunding, "validation.finance.other.funding.required");
    }
    @Test
    public void validFullAmount() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(1L, "Yes", "Source1", "10-2014", new BigDecimal(100));
        expectNoErrors(otherFunding);
    }
    @Test
    public void validWithoutDate() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(2L, "Yes", OTHER_FUNDING, null, null);
        expectNoErrors(otherFunding);
    }
    @Test
    public void validNoOtherPublicFunding() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(3L, null, "Source1", "11-1999", new BigDecimal(5));
        expectNoErrors(otherFunding);
    }
    @Test
    public void valid() {
        mockWithRadio("Yes");
        OtherFunding otherFunding = new OtherFunding(4L, "Yes", "Source1", "09-1999",  new BigDecimal(5));
        expectNoErrors(otherFunding);
    }
    @Test
    public void validNoSource() {
        mockWithRadio("No");
        OtherFunding otherFunding = new OtherFunding(5L, "No", "", "ertt", new BigDecimal(5));
        expectNoErrors(otherFunding);
    }
    @Test
    public void validFullFunding() {
        mockWithRadio("No");
        OtherFunding otherFunding = new OtherFunding(6L, "Yes", "Source1", "2014", new BigDecimal(100));
        expectNoErrors(otherFunding);
    }

    private void expectError(OtherFunding otherFunding, String errorKey, Object... arguments){
        BindingResult bindingResult = ValidatorTestUtil.getBindingResult(otherFunding);
        validator.validate(otherFunding, bindingResult);
        verifyError(bindingResult, errorKey, arguments);
    }

    private void expectNoErrors(OtherFunding otherFunding){
        BindingResult bindingResult = ValidatorTestUtil.getBindingResult(otherFunding);
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
        Competition competition = newCompetition().build();
        Application application = newApplication().withCompetition(competition).build();
        ApplicationFinance applicationFinance = ApplicationFinanceBuilder.newApplicationFinance().withApplication(application).build();
        ApplicationFinanceRow cost = ApplicationFinanceRowBuilder.newApplicationFinanceRow().withOwningFinance(applicationFinance).withItem(value).build();
        Question question = QuestionBuilder.newQuestion().build();
        when(financeRowRepository.findOne(any(Long.class))).thenReturn(cost);
        when(questionService.getQuestionByCompetitionIdAndFormInputType(competition.getId(), FinanceRowType.OTHER_FUNDING.getFormInputType())).thenReturn(ServiceResult.serviceSuccess(question));
        List<ApplicationFinanceRow> listOfCostWithYes = new ArrayList<>();
        listOfCostWithYes.add(cost);
        when(financeRowRepository.findByTargetIdAndNameAndQuestionId(anyLong(), eq(COST_KEY), anyLong())).thenReturn(listOfCostWithYes);
    }
}
