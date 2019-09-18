package org.innovateuk.ifs.finance.validator;

import org.innovateuk.ifs.application.validator.ValidatorTestUtil;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimPercentage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.Optional;

import static org.innovateuk.ifs.finance.builder.ApplicationFinanceRowBuilder.newApplicationFinanceRow;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class GrantClaimValidatorTest {
	private static final Long CLAIM_ID = 1L;

	@InjectMocks
	private GrantClaimPercentageValidator validator;

	@Mock
	private ApplicationFinanceRowRepository financeRowRepository;
	
	private GrantClaimPercentage claim;
	private BindingResult bindingResult;
	
	@Before
	public void setUp() {
        claim = new GrantClaimPercentage(CLAIM_ID, 100, 1L);
        bindingResult = ValidatorTestUtil.getBindingResult(claim);
    }

    @Test
	public void testNullClaimPercentageError() {
		claim.setPercentage(null);

		validator.validate(claim, bindingResult);

		verifyError("org.hibernate.validator.constraints.NotBlank.message");
	}

	@Test
	public void testMaximumNotDefinedError() {
		ApplicationFinance applicationFinance = mock(ApplicationFinance.class);
		when(financeRowRepository.findById(CLAIM_ID)).thenReturn(Optional.of(newApplicationFinanceRow().withTarget(applicationFinance).build()));
		when(applicationFinance.getMaximumFundingLevel()).thenReturn(null);

		validator.validate(claim, bindingResult);

		verifyError("validation.grantClaimPercentage.maximum.not.defined");
	}

	@Test
	public void testMinimumError() {
		claim.setPercentage(-1);
		ApplicationFinance applicationFinance = mock(ApplicationFinance.class);
		when(financeRowRepository.findById(CLAIM_ID)).thenReturn(Optional.of(newApplicationFinanceRow().withTarget(applicationFinance).build()));
		when(applicationFinance.getMaximumFundingLevel()).thenReturn(100);

		validator.validate(claim, bindingResult);

		verifyError("validation.field.percentage.max.value.or.higher", 0);

	}

	@Test
	public void testMaximumError() {
		claim.setPercentage(50);
		ApplicationFinance applicationFinance = mock(ApplicationFinance.class);
		when(financeRowRepository.findById(CLAIM_ID)).thenReturn(Optional.of(newApplicationFinanceRow().withTarget(applicationFinance).build()));
		when(applicationFinance.getMaximumFundingLevel()).thenReturn(30);

		validator.validate(claim, bindingResult);

		verifyError("validation.finance.grant.claim.percentage.max", 30);
	}

	@Test
	public void testSuccess() {
		claim.setPercentage(100);
		ApplicationFinance applicationFinance = mock(ApplicationFinance.class);
		when(financeRowRepository.findById(CLAIM_ID)).thenReturn(Optional.of(newApplicationFinanceRow().withTarget(applicationFinance).build()));
		when(applicationFinance.getMaximumFundingLevel()).thenReturn(100);

		validator.validate(claim, bindingResult);

		verifyNoErrors();
	}

	private void verifyNoErrors() {
	   assertFalse(bindingResult.hasErrors());
	}

	private void verifyError(String errorCode, Object... expectedArguments) {
   		assertTrue(bindingResult.hasErrors());
	   	assertEquals(1, bindingResult.getAllErrors().size());
		ObjectError actualError = bindingResult.getAllErrors().get(0);
		assertEquals(errorCode, actualError.getCode());
		assertEquals(errorCode, actualError.getDefaultMessage());
		assertArrayEquals(expectedArguments, actualError.getArguments());
	}
}
