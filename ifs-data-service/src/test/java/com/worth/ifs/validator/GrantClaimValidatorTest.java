package com.worth.ifs.validator;

import static com.worth.ifs.validator.ValidatorTestUtil.getBindingResult;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;

import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.repository.CostRepository;
import com.worth.ifs.finance.resource.cost.GrantClaim;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.OrganisationType;
import com.worth.ifs.user.resource.OrganisationSize;
import com.worth.ifs.user.resource.OrganisationTypeEnum;

@RunWith(MockitoJUnitRunner.class)
public class GrantClaimValidatorTest {
	@InjectMocks
	private GrantClaimValidator validator;

	@Mock
	private CostRepository costRepository;
	
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
        
        assertTrue(bindingResult.hasErrors());
        assertEquals("validation.finance.select.organisation.size", bindingResult.getAllErrors().get(0).getCode());
    }
    
    @Test
    public void testBusinessNoSize() {
    	setUpOrgType(OrganisationTypeEnum.BUSINESS, null);
        claim.setGrantClaimPercentage(100);
        
        validator.validate(claim, bindingResult);
        
        assertTrue(bindingResult.hasErrors());
        assertEquals("validation.finance.select.organisation.size", bindingResult.getAllErrors().get(0).getCode());
    }
    
    @Test
    public void testResearchNoSize() {
    	setUpOrgType(OrganisationTypeEnum.RESEARCH, null);
        claim.setGrantClaimPercentage(100);
        
        validator.validate(claim, bindingResult);
        
        assertFalse(bindingResult.hasErrors());
    }
    
    @Test
    public void testAcademicNullClaim() {
    	setUpOrgType(OrganisationTypeEnum.ACADEMIC, OrganisationSize.MEDIUM);
        claim.setGrantClaimPercentage(null);
        
        validator.validate(claim, bindingResult);
        
        assertTrue(bindingResult.hasErrors());
        assertEquals("org.hibernate.validator.constraints.NotBlank.message", bindingResult.getAllErrors().get(0).getCode());
    }
    
    @Test
    public void testBusinessNullClaim() {
    	setUpOrgType(OrganisationTypeEnum.BUSINESS, OrganisationSize.MEDIUM);
        claim.setGrantClaimPercentage(null);
        
        validator.validate(claim, bindingResult);
        
        assertTrue(bindingResult.hasErrors());
        assertEquals("org.hibernate.validator.constraints.NotBlank.message", bindingResult.getAllErrors().get(0).getCode());
    }
    
    @Test
    public void testResearchNullClaim() {
    	setUpOrgType(OrganisationTypeEnum.RESEARCH, null);
        claim.setGrantClaimPercentage(null);
        
        validator.validate(claim, bindingResult);
        
        assertTrue(bindingResult.hasErrors());
        assertEquals("org.hibernate.validator.constraints.NotBlank.message", bindingResult.getAllErrors().get(0).getCode());
    }
    
    @Test
    public void testResearchZeroClaim() {
    	setUpOrgType(OrganisationTypeEnum.RESEARCH, null);
        claim.setGrantClaimPercentage(0);
        
        validator.validate(claim, bindingResult);
        
        assertFalse(bindingResult.hasErrors());
    }
    
    @Test
    public void testResearchNegativeClaim() {
    	setUpOrgType(OrganisationTypeEnum.RESEARCH, null);
        claim.setGrantClaimPercentage(-1);
        
        validator.validate(claim, bindingResult);
        
        assertTrue(bindingResult.hasErrors());
        assertEquals("Min", bindingResult.getAllErrors().get(0).getCode());
    }
    
    @Test
    public void testResearchHundredClaim() {
    	setUpOrgType(OrganisationTypeEnum.RESEARCH, null);
        claim.setGrantClaimPercentage(100);
        
        validator.validate(claim, bindingResult);
        
        assertFalse(bindingResult.hasErrors());
    }
    
    @Test
    public void testResearchOverHundredClaim() {
    	setUpOrgType(OrganisationTypeEnum.RESEARCH, null);
        claim.setGrantClaimPercentage(101);
        
        validator.validate(claim, bindingResult);
        
        assertTrue(bindingResult.hasErrors());
        assertEquals("Max", bindingResult.getAllErrors().get(0).getCode());
    }
    
    @Test
    public void testResearchBetweenZeroAndHundredClaim() {
    	setUpOrgType(OrganisationTypeEnum.RESEARCH, null);
        claim.setGrantClaimPercentage(50);
        
        validator.validate(claim, bindingResult);
        
        assertFalse(bindingResult.hasErrors());
    }
    
    @Test
    public void testAcademicNegativeClaim() {
    	setUpOrgType(OrganisationTypeEnum.ACADEMIC, OrganisationSize.MEDIUM);
        claim.setGrantClaimPercentage(-1);
        
        validator.validate(claim, bindingResult);
        
        assertTrue(bindingResult.hasErrors());
        assertEquals("Min", bindingResult.getAllErrors().get(0).getCode());
    }
    
    @Test
    public void testBusinessNegativeClaim() {
    	setUpOrgType(OrganisationTypeEnum.BUSINESS, OrganisationSize.MEDIUM);
        claim.setGrantClaimPercentage(-1);
        
        validator.validate(claim, bindingResult);
        
        assertTrue(bindingResult.hasErrors());
        assertEquals("Min", bindingResult.getAllErrors().get(0).getCode());
    }
    
    @Test
    public void testAcademicZeroClaim() {
    	setUpOrgType(OrganisationTypeEnum.ACADEMIC, OrganisationSize.MEDIUM);
        claim.setGrantClaimPercentage(0);
        
        validator.validate(claim, bindingResult);
        
        assertTrue(bindingResult.hasErrors());
        assertEquals("org.hibernate.validator.constraints.NotBlank.message", bindingResult.getAllErrors().get(0).getCode());
    }
    
    @Test
    public void testBusinessZeroClaim() {
    	setUpOrgType(OrganisationTypeEnum.BUSINESS, OrganisationSize.MEDIUM);
        claim.setGrantClaimPercentage(0);
        
        validator.validate(claim, bindingResult);
        
        assertTrue(bindingResult.hasErrors());
        assertEquals("org.hibernate.validator.constraints.NotBlank.message", bindingResult.getAllErrors().get(0).getCode());
    }
    
    @Test
    public void testAcademicMaxClaim() {
    	setUpOrgType(OrganisationTypeEnum.ACADEMIC, OrganisationSize.MEDIUM);
        claim.setGrantClaimPercentage(OrganisationSize.MEDIUM.getMaxGrantClaimPercentage());
        
        validator.validate(claim, bindingResult);
        
        assertFalse(bindingResult.hasErrors());
    }
    
    @Test
    public void testBusinessMaxClaim() {
    	setUpOrgType(OrganisationTypeEnum.BUSINESS, OrganisationSize.MEDIUM);
        claim.setGrantClaimPercentage(OrganisationSize.MEDIUM.getMaxGrantClaimPercentage());
        
        validator.validate(claim, bindingResult);
        
        assertFalse(bindingResult.hasErrors());
    }
    
    @Test
    public void testAcademicBetweenZeroAndMaxClaim() {
    	setUpOrgType(OrganisationTypeEnum.ACADEMIC, OrganisationSize.MEDIUM);
        claim.setGrantClaimPercentage(30);
        
        validator.validate(claim, bindingResult);
        
        assertFalse(bindingResult.hasErrors());
    }
    
    @Test
    public void testBusinessBetweenZeroAndMaxClaim() {
    	setUpOrgType(OrganisationTypeEnum.BUSINESS, OrganisationSize.MEDIUM);
        claim.setGrantClaimPercentage(30);
        
        validator.validate(claim, bindingResult);
        
        assertFalse(bindingResult.hasErrors());
    }
    
    private void setUpOrgType(OrganisationTypeEnum orgType, OrganisationSize organisationSize) {
    	OrganisationType organisationType = new OrganisationType();
    	organisationType.setId(orgType.getOrganisationTypeId());
    	Organisation organisation = new Organisation();
    	organisation.setOrganisationType(organisationType);
    	ApplicationFinance applicationFinance = new ApplicationFinance();
    	applicationFinance.setOrganisation(organisation);
    	applicationFinance.setOrganisationSize(organisationSize);
    	Cost cost = new Cost();
    	cost.setApplicationFinance(applicationFinance);
    	when(costRepository.findOne(any(Long.class))).thenReturn(cost);
    }
    

}
