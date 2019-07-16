package org.innovateuk.ifs.management.funding.form.enumerable;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ResearchParticipationAmountTest {

	@Test
	public void testFromIdThirty() {
		ResearchParticipationAmount result = ResearchParticipationAmount.fromId(1);
		
		assertEquals(ResearchParticipationAmount.THIRTY, result);
	}
	
	@Test
	public void testFromIdFifty() {
		ResearchParticipationAmount result = ResearchParticipationAmount.fromId(2);
		
		assertEquals(ResearchParticipationAmount.FIFTY, result);
	}
	
	@Test
	public void testFromIdHundred() {
		ResearchParticipationAmount result = ResearchParticipationAmount.fromId(3);
		
		assertEquals(ResearchParticipationAmount.HUNDRED, result);
	}
	
	@Test
	public void testFromIdNone() {
		ResearchParticipationAmount result = ResearchParticipationAmount.fromId(4);
		
		assertEquals(ResearchParticipationAmount.NONE, result);
	}
	
	@Test
	public void testFromIdNoMatch() {
		ResearchParticipationAmount result = ResearchParticipationAmount.fromId(5);
		
		assertNull(result);
	}
	
	@Test
	public void testFromAmountThirty() {
		ResearchParticipationAmount result = ResearchParticipationAmount.fromAmount(30);
		
		assertEquals(ResearchParticipationAmount.THIRTY, result);
	}
	
	@Test
	public void testFromAmountFifty() {
		ResearchParticipationAmount result = ResearchParticipationAmount.fromAmount(50);
		
		assertEquals(ResearchParticipationAmount.FIFTY, result);
	}
	
	@Test
	public void testFromAmountHundred() {
		ResearchParticipationAmount result = ResearchParticipationAmount.fromAmount(100);
		
		assertEquals(ResearchParticipationAmount.HUNDRED, result);
	}
	
	@Test
	public void testFromAmountNone() {
		ResearchParticipationAmount result = ResearchParticipationAmount.fromAmount(0);
		
		assertEquals(ResearchParticipationAmount.NONE, result);
	}
	
	@Test
	public void testFromAmountNoMatchSetsDefault() {
		ResearchParticipationAmount result = ResearchParticipationAmount.fromAmount(95);

		assertEquals(ResearchParticipationAmount.THIRTY, result);
	}
	
}
