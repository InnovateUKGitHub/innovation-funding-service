package com.worth.ifs.service.competitionsetup.formpopulator;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.worth.ifs.competition.resource.CollaborationLevel;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.LeadApplicantType;
import com.worth.ifs.controller.form.competitionsetup.CompetitionSetupForm;
import com.worth.ifs.controller.form.competitionsetup.EligibilityForm;
import com.worth.ifs.util.CollectionFunctions;

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
				.withCollaborationLevel(CollaborationLevel.COLLABORATIVE)
				.withLeadApplicantType(LeadApplicantType.BUSINESS)
				.build();

		CompetitionSetupForm result = service.populateForm(competition);
		
		assertTrue(result instanceof EligibilityForm);
		EligibilityForm form = (EligibilityForm) result;
		assertEquals(CollectionFunctions.asLinkedSet(2L, 3L), form.getResearchCategoryId());
		assertEquals("yes", form.getMultipleStream());
		assertEquals("collaborative", form.getSingleOrCollaborative());
		assertEquals("business", form.getLeadApplicantType());
	}
}
