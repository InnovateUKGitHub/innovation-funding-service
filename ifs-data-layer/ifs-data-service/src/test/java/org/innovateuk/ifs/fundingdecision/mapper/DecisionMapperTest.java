package org.innovateuk.ifs.fundingdecision.mapper;

import org.innovateuk.ifs.application.resource.Decision;
import org.innovateuk.ifs.fundingdecision.domain.DecisionStatus;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DecisionMapperTest {

	private DecisionMapper mapper;
	
	@Before
	public void setUp() {
		mapper = new MyDecisionMapper();
	}
	
	@Test
	public void testConvertFromResourceToDomain() {
		for(Decision decision: Decision.values()){
			DecisionStatus status = mapper.mapToDomain(decision);
			assertEquals(decision.name(), status.name());
		}
	}
	
	@Test
	public void testConvertFromDomainToResource() {
		for(DecisionStatus status: DecisionStatus.values()){
			Decision decision = mapper.mapToResource(status);
			assertEquals(status.name(), decision.name());
		}
	}
	

	private static class MyDecisionMapper extends DecisionMapper {
		
	}
}
