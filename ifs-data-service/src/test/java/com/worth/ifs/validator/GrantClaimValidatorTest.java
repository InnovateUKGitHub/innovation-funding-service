package com.worth.ifs.validator;

import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.ApplicationFinanceRow;
import com.worth.ifs.finance.repository.ApplicationFinanceRowRepository;
import com.worth.ifs.finance.resource.cost.GrantClaim;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.OrganisationType;
import com.worth.ifs.user.resource.OrganisationSize;
import com.worth.ifs.user.resource.OrganisationTypeEnum;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import static com.worth.ifs.validator.ValidatorTestUtil.getBindingResult;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GrantClaimValidatorTest {
	@InjectMocks
	private GrantClaimValidator validator;

	@Mock
	private ApplicationFinanceRowRepository financeRowRepository;
	
	private GrantClaim claim;
	private BindingResult bindingResult;
	
	@Before
	public void setUp() {
        claim = new GrantClaim();
        bindingResult = getBindingResult(claim);
    }
	
    @Test
    public void testAcademicNoSize() {
    	setUpOrgType(OrganisationTypeEnum.ACADEMIC, null);
        claim.setGrantClaimPercentage(100);
        
        validator.validate(claim, bindingResult);
        
        verifyError("validation.finance.select.organisation.size");
    }
    
	@Test
    public void testBusinessNoSize() {
    	setUpOrgType(OrganisationTypeEnum.BUSINESS, null);
        claim.setGrantClaimPercentage(100);
        
        validator.validate(claim, bindingResult);
        
        verifyError("validation.finance.select.organisation.size");
    }
    
    @Test
    public void testResearchNoSize() {
    	setUpOrgType(OrganisationTypeEnum.RESEARCH, null);
        claim.setGrantClaimPercentage(100);
        
        validator.validate(claim, bindingResult);
        
        verifyNoErrors();
    }
    
    @Test
    public void testAcademicNullClaim() {
    	setUpOrgType(OrganisationTypeEnum.ACADEMIC, OrganisationSize.MEDIUM);
        claim.setGrantClaimPercentage(null);
        
        validator.validate(claim, bindingResult);
        
        verifyError("org.hibernate.validator.constraints.NotBlank.message");
    }
    
    @Test
    public void testBusinessNullClaim() {
    	setUpOrgType(OrganisationTypeEnum.BUSINESS, OrganisationSize.MEDIUM);
        claim.setGrantClaimPercentage(null);
        
        validator.validate(claim, bindingResult);
        
        verifyError("org.hibernate.validator.constraints.NotBlank.message");
    }
    
    @Test
    public void testResearchNullClaim() {
    	setUpOrgType(OrganisationTypeEnum.RESEARCH, null);
        claim.setGrantClaimPercentage(null);
        
        validator.validate(claim, bindingResult);
        
        verifyError("org.hibernate.validator.constraints.NotBlank.message");
    }
    
    @Test
    public void testResearchZeroClaim() {
    	setUpOrgType(OrganisationTypeEnum.RESEARCH, null);
        claim.setGrantClaimPercentage(0);
        
        validator.validate(claim, bindingResult);
        
        verifyNoErrors();
    }
    
    @Test
    public void testResearchNegativeClaim() {
    	setUpOrgType(OrganisationTypeEnum.RESEARCH, null);
        claim.setGrantClaimPercentage(-1);
        
        validator.validate(claim, bindingResult);
        
        verifyError("validation.field.percentage.max.value.or.higher", 0);
    }
    
    @Test
    public void testResearchHundredClaim() {
    	setUpOrgType(OrganisationTypeEnum.RESEARCH, null);
        claim.setGrantClaimPercentage(100);
        
        validator.validate(claim, bindingResult);
        
        verifyNoErrors();
    }
    
    @Test
    public void testResearchOverHundredClaim() {
    	setUpOrgType(OrganisationTypeEnum.RESEARCH, null);
        claim.setGrantClaimPercentage(101);
        
        validator.validate(claim, bindingResult);

        verifyError("validation.field.percentage.max.value.or.lower", 100);
    }
    
    @Test
    public void testBusinessOverMaximumClaim() {
    	setUpOrgType(OrganisationTypeEnum.BUSINESS, OrganisationSize.MEDIUM);
        claim.setGrantClaimPercentage(61);
        
        validator.validate(claim, bindingResult);

        verifyError("validation.field.percentage.max.value.or.lower", 60);
    }
    
    @Test
    public void testAcademicOverMaximumClaim() {
    	setUpOrgType(OrganisationTypeEnum.ACADEMIC, OrganisationSize.MEDIUM);
        claim.setGrantClaimPercentage(61);
        
        validator.validate(claim, bindingResult);

        verifyError("validation.field.percentage.max.value.or.lower", 60);
    }
    
    @Test
    public void testResearchBetweenZeroAndHundredClaim() {
    	setUpOrgType(OrganisationTypeEnum.RESEARCH, null);
        claim.setGrantClaimPercentage(50);
        
        validator.validate(claim, bindingResult);
        
        verifyNoErrors();
    }
    
    @Test
    public void testAcademicNegativeClaim() {
    	setUpOrgType(OrganisationTypeEnum.ACADEMIC, OrganisationSize.MEDIUM);
        claim.setGrantClaimPercentage(-1);
        
        validator.validate(claim, bindingResult);

        verifyError("validation.field.percentage.max.value.or.higher", 0);
    }
    
    @Test
    public void testBusinessNegativeClaim() {
    	setUpOrgType(OrganisationTypeEnum.BUSINESS, OrganisationSize.MEDIUM);
        claim.setGrantClaimPercentage(-1);
        
        validator.validate(claim, bindingResult);

        verifyError("validation.field.percentage.max.value.or.higher", 0);
    }
    
    @Test
    public void testAcademicZeroClaim() {
    	setUpOrgType(OrganisationTypeEnum.ACADEMIC, OrganisationSize.MEDIUM);
        claim.setGrantClaimPercentage(0);
        
        validator.validate(claim, bindingResult);
        
        verifyNoErrors();
    }
    
    @Test
    public void testBusinessZeroClaim() {
    	setUpOrgType(OrganisationTypeEnum.BUSINESS, OrganisationSize.MEDIUM);
        claim.setGrantClaimPercentage(0);
        
        validator.validate(claim, bindingResult);
        
        verifyNoErrors();
    }
    
    @Test
    public void testAcademicMaxClaim() {
    	setUpOrgType(OrganisationTypeEnum.ACADEMIC, OrganisationSize.MEDIUM);
        claim.setGrantClaimPercentage(OrganisationSize.MEDIUM.getMaxGrantClaimPercentage());
        
        validator.validate(claim, bindingResult);
        
        verifyNoErrors();
    }
    
    @Test
    public void testBusinessMaxClaim() {
    	setUpOrgType(OrganisationTypeEnum.BUSINESS, OrganisationSize.MEDIUM);
        claim.setGrantClaimPercentage(OrganisationSize.MEDIUM.getMaxGrantClaimPercentage());
        
        validator.validate(claim, bindingResult);
        
        verifyNoErrors();
    }
    
    @Test
    public void testAcademicBetweenZeroAndMaxClaim() {
    	setUpOrgType(OrganisationTypeEnum.ACADEMIC, OrganisationSize.MEDIUM);
        claim.setGrantClaimPercentage(30);
        
        validator.validate(claim, bindingResult);
        
        verifyNoErrors();
    }
    
    @Test
    public void testBusinessBetweenZeroAndMaxClaim() {
    	setUpOrgType(OrganisationTypeEnum.BUSINESS, OrganisationSize.MEDIUM);
        claim.setGrantClaimPercentage(30);
        
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
    
    private void setUpOrgType(OrganisationTypeEnum orgType, OrganisationSize organisationSize) {
    	OrganisationType organisationType = new OrganisationType();
    	organisationType.setId(orgType.getOrganisationTypeId());
    	Organisation organisation = new Organisation();
    	organisation.setOrganisationType(organisationType);
    	ApplicationFinance applicationFinance = new ApplicationFinance();
    	applicationFinance.setOrganisation(organisation);
    	applicationFinance.setOrganisationSize(organisationSize);
        ApplicationFinanceRow cost = new ApplicationFinanceRow();
    	cost.setTarget(applicationFinance);
    	when(financeRowRepository.findOne(any(Long.class))).thenReturn(cost);
    }
    
}
