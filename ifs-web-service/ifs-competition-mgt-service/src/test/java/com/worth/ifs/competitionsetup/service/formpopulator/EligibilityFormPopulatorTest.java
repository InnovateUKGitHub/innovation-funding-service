package com.worth.ifs.competitionsetup.service.formpopulator;

import com.worth.ifs.competition.resource.*;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.EligibilityForm;
import com.worth.ifs.util.CollectionFunctions;
import org.junit.Before;
import org.junit.Test;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EligibilityFormPopulatorTest {

	private EligibilityFormPopulator service;
	
	@Before
	public void setUp() {
		service = new EligibilityFormPopulator();
	}
	@Test
	public void testSectionToFill() {
		CompetitionSetupSection result = service.sectionToFill();
		assertEquals(CompetitionSetupSection.ELIGIBILITY, result);
	}
				
	@Test
	public void testGetSectionFormDataInitialDetails() {
		
		CompetitionResource competition = newCompetitionResource()
				.withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
				.withMaxResearchRatio(30)
				.withMultiStream(true)
				.withStreamName("streamname")
				.withCollaborationLevel(CollaborationLevel.COLLABORATIVE)
				.withLeadApplicantType(LeadApplicantType.BUSINESS)
				.build();

		CompetitionSetupForm result = service.populateForm(competition);
		
		assertTrue(result instanceof EligibilityForm);
		EligibilityForm form = (EligibilityForm) result;
		assertEquals(CollectionFunctions.asLinkedSet(2L, 3L), form.getResearchCategoryId());
		assertEquals("no", form.getMultipleStream());
		assertEquals(null, form.getStreamName());
		assertEquals("collaborative", form.getSingleOrCollaborative());
		assertEquals("business", form.getLeadApplicantType());
	}
}
