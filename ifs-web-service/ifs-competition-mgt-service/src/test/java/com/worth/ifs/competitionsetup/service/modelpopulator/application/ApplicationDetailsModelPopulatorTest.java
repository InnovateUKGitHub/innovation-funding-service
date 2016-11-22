package com.worth.ifs.competitionsetup.service.modelpopulator.application;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.SectionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSubsection;
import com.worth.ifs.competitionsetup.form.application.ApplicationDetailsForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.Optional;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationDetailsModelPopulatorTest {

	@InjectMocks
	private ApplicationDetailsModelPopulator populator;

	@Mock
	private CompetitionService competitionService;

	@Mock
	private SectionService sectionService;
	
	@Test
	public void testSectionToPopulateModel() {
		CompetitionSetupSubsection result = populator.sectionToPopulateModel();
		
		assertEquals(CompetitionSetupSubsection.APPLICATION_DETAILS, result);
	}
	
	@Test
	public void testPopulateModel() {

		boolean useResubmissionQuestion = false;

		Model model = new ExtendedModelMap();
		CompetitionResource competitionResource = newCompetitionResource()
				.withCompetitionCode("code")
				.withName("name")
				.withId(8L)
				.withUseResubmissionQuestion(useResubmissionQuestion)
				.build();

		populator.populateModel(model, competitionResource, Optional.empty());
		assertEquals(2, model.asMap().size());
		assertEquals(8L, model.asMap().get("competitionId"));
		assertEquals(useResubmissionQuestion, ((ApplicationDetailsForm)model.asMap().get("competitionSetupForm")).isUseResubmissionQuestion());
	}
}
