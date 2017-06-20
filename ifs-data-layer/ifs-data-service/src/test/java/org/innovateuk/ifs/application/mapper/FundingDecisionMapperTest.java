package org.innovateuk.ifs.application.mapper;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import org.innovateuk.ifs.application.domain.FundingDecisionStatus;
import org.innovateuk.ifs.application.resource.FundingDecision;

public class FundingDecisionMapperTest {

	private FundingDecisionMapper mapper;
	
	@Before
	public void setUp() {
		mapper = new MyFundingDecisionMapper();
	}
	
	@Test
	public void testConvertFromResourceToDomain() {
		for(FundingDecision decision: FundingDecision.values()){
			FundingDecisionStatus status = mapper.mapToDomain(decision);
			assertEquals(decision.name(), status.name());
		}
	}
	
	@Test
	public void testConvertFromDomainToResource() {
		for(FundingDecisionStatus status: FundingDecisionStatus.values()){
			FundingDecision decision = mapper.mapToResource(status);
			assertEquals(status.name(), decision.name());
		}
	}
	

	private static class MyFundingDecisionMapper extends FundingDecisionMapper {
		
	}
}
