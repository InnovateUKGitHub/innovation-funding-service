package org.innovateuk.ifs.competitionsetup.eligibility.populator;

import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.eligibility.form.EligibilityForm;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
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
				.withMaxResearchRatio(50)
				.withMultiStream(true)
				.withStreamName("streamname")
				.withCollaborationLevel(CollaborationLevel.COLLABORATIVE)
				.withLeadApplicantType(asList(2L))
				.build();

		CompetitionSetupForm result = service.populateForm(competition);
		
		assertTrue(result instanceof EligibilityForm);
		EligibilityForm form = (EligibilityForm) result;
		assertEquals(CollectionFunctions.asLinkedSet(2L, 3L), form.getResearchCategoryId());
		assertEquals("no", form.getMultipleStream());
		assertEquals(null, form.getStreamName());
		assertEquals("collaborative", form.getSingleOrCollaborative());
		assertEquals(asList(2L), form.getLeadApplicantTypes());
		assertEquals(2, form.getResearchParticipationAmountId());
	}

	@Test
	public void testGetDefaultResearchParticipationAmountId() {
		CompetitionResource competition = newCompetitionResource()
				.withResearchCategories(CollectionFunctions.asLinkedSet(2L, 3L))
				.withMultiStream(true)
				.withStreamName("streamname")
				.withCollaborationLevel(CollaborationLevel.COLLABORATIVE)
				.withLeadApplicantType(asList(2L))
				.build();

		CompetitionSetupForm result = service.populateForm(competition);

		assertTrue(result instanceof EligibilityForm);
		EligibilityForm form = (EligibilityForm) result;
		assertEquals(1, form.getResearchParticipationAmountId());
	}
}
