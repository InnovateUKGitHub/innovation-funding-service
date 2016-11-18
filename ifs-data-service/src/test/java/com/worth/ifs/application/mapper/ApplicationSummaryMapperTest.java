package com.worth.ifs.application.mapper;

import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.application.domain.FundingDecisionStatus;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.application.resource.CompletedPercentageResource;
import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.application.transactional.ApplicationService;
import com.worth.ifs.application.transactional.ApplicationSummarisationService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.clearUniqueIds;
import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ApplicationSummaryMapperTest {

	private static final Long APPLICATION_ID = Long.valueOf(123L);

	@InjectMocks
	private ApplicationSummaryMapperImpl mapper;
	
	@Mock
	private ApplicationService applicationService;
	
	@Mock
	private ApplicationSummarisationService applicationSummarisationService;
	
	@Mock
	private FundingDecisionMapper fundingDecisionMapper;
	
	private Application source;
	
	@Before
	public void setUp() {
		clearUniqueIds();
		when(fundingDecisionMapper.mapToResource(FundingDecisionStatus.FUNDED)).thenReturn(FundingDecision.FUNDED);
		
		ApplicationStatus openStatus = new ApplicationStatus(ApplicationStatusConstants.OPEN.getId(), ApplicationStatusConstants.OPEN.getName());
		source = newApplication()
				.withId(APPLICATION_ID)
				.withName("appname")
				.withApplicationStatus(openStatus)
				.withDurationInMonths(7L)
				.withFundingDecision(FundingDecisionStatus.FUNDED)
				.build();
		
		Organisation org1 = newOrganisation().withId(1L).withName("leadorg").build();
		Organisation org2 = newOrganisation().withId(2L).withName("otherorg1").build();
		
		ProcessRole leadProcessRole = leadProcessRole(org1);
		source.addUserApplicationRole(leadProcessRole);
		
		ProcessRole collaboratorProcessRole1 = collaboratorProcessRole(org2);
		source.addUserApplicationRole(collaboratorProcessRole1);
		ProcessRole collaboratorProcessRole2 = collaboratorProcessRole(org2);
		source.addUserApplicationRole(collaboratorProcessRole2);
		ProcessRole collaboratorProcessRole3 = collaboratorProcessRole(org1);
		source.addUserApplicationRole(collaboratorProcessRole3);
		
		CompletedPercentageResource resource = new CompletedPercentageResource();
		resource.setCompletedPercentage(new BigDecimal("66.6"));
		when(applicationService.getProgressPercentageByApplicationId(APPLICATION_ID)).thenReturn(serviceSuccess(resource));
		
		when(applicationSummarisationService.getFundingSought(source)).thenReturn(serviceSuccess(new BigDecimal("1.23")));
		when(applicationSummarisationService.getTotalProjectCost(source)).thenReturn(serviceSuccess(new BigDecimal("9.87")));
	}
	
	@Test
	public void testMap() {
		
		ApplicationSummaryResource result = mapper.mapToResource(source);
		
		assertEquals(APPLICATION_ID, result.getId());
		assertEquals("appname", result.getName());
		assertEquals("In Progress", result.getStatus());
		assertEquals(Integer.valueOf(66), result.getCompletedPercentage());
		assertEquals("leadorg", result.getLead());
		assertEquals("User 4", result.getLeadApplicant());
		assertEquals(Integer.valueOf(2), result.getNumberOfPartners());
		assertEquals(new BigDecimal("1.23"), result.getGrantRequested());
		assertEquals(new BigDecimal("9.87"), result.getTotalProjectCost());
		assertEquals(Long.valueOf(7L), result.getDuration());
		assertTrue(result.isFunded());
		assertEquals(FundingDecision.FUNDED, result.getFundingDecision());
		
		verify(fundingDecisionMapper).mapToResource(FundingDecisionStatus.FUNDED);
	}
	
	@Test
	public void testMapFundedBecauseOfStatus() {
		ApplicationStatus approvedStatus = new ApplicationStatus(ApplicationStatusConstants.APPROVED.getId(), ApplicationStatusConstants.APPROVED.getName());
		source.setApplicationStatus(approvedStatus);
		source.setFundingDecision(null);
		
		ApplicationSummaryResource result = mapper.mapToResource(source);
		
		assertTrue(result.isFunded());
		assertEquals(FundingDecision.FUNDED, result.getFundingDecision());
	}
	
	@Test
	public void testMapFundedBecauseOfFundingDecision() {
		ApplicationStatus openStatus = new ApplicationStatus(ApplicationStatusConstants.OPEN.getId(), ApplicationStatusConstants.OPEN.getName());
		source.setApplicationStatus(openStatus);
		source.setFundingDecision(FundingDecisionStatus.FUNDED);
		
		ApplicationSummaryResource result = mapper.mapToResource(source);
		
		assertTrue(result.isFunded());
		assertEquals(FundingDecision.FUNDED, result.getFundingDecision());
	}

	private ProcessRole leadProcessRole(Organisation organisation) {
		ProcessRole leadProcessRole = new ProcessRole();
		
		leadProcessRole.setOrganisation(organisation);
		Role role = new Role();
		role.setName(UserRoleType.LEADAPPLICANT.getName());
        leadProcessRole.setUser(newUser().build());
		leadProcessRole.setRole(role);
		return leadProcessRole;
	}
	
	private ProcessRole collaboratorProcessRole(Organisation organisation) {
		ProcessRole collaboratorProcessRole = new ProcessRole();
		collaboratorProcessRole.setOrganisation(organisation);
		Role role = new Role();
		role.setName(UserRoleType.COLLABORATOR.getName());
				
		collaboratorProcessRole.setRole(role);
		return collaboratorProcessRole;
	}
}
