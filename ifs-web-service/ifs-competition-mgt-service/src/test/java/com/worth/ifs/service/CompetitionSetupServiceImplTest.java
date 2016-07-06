package com.worth.ifs.service;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.worth.ifs.competitionsetup.service.CompetitionSetupServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.AdditionalInfoForm;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.service.formpopulator.CompetitionSetupFormPopulator;
import com.worth.ifs.competitionsetup.service.modelpopulator.CompetitionSetupSectionModelPopulator;
import com.worth.ifs.competitionsetup.service.sectionupdaters.CompetitionSetupSectionSaver;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionSetupServiceImplTest {

	@InjectMocks
	private CompetitionSetupServiceImpl service;
	
	@Mock
	private CompetitionService competitionService;

	@Test
	public void testPopulateCompetitionSectionModelAttributesNoMatchingFormPopulator() {
		Model model = new ExtendedModelMap();
		CompetitionResource competition = newCompetitionResource()
				.withCompetitionCode("code")
				.withName("name")
				.build();
		
		service.setCompetitionSetupSectionModelPopulators(asList());
		
		CompetitionSetupSection section = CompetitionSetupSection.INITIAL_DETAILS;
		
		List<CompetitionSetupSection> completedSections = new ArrayList<>();
		when(competitionService.getCompletedCompetitionSetupSectionStatusesByCompetitionId(8L)).thenReturn(completedSections);
		
		service.populateCompetitionSectionModelAttributes(model, competition, section);
		
		verifyCommonModelAttributes(model, competition, section, completedSections);
		assertEquals("section-initial", model.asMap().get("currentSectionFragment"));
	}
	
	@Test
	public void testPopulateCompetitionSectionModelAttributesEligibility() {
		Model model = new ExtendedModelMap();
		CompetitionResource competition = newCompetitionResource()
				.withCompetitionCode("code")
				.withName("name")
				.build();
		
		CompetitionSetupSectionModelPopulator matchingPopulator = mock(CompetitionSetupSectionModelPopulator.class);
		when(matchingPopulator.sectionToPopulateModel()).thenReturn(CompetitionSetupSection.ELIGIBILITY);
		CompetitionSetupSectionModelPopulator notMatchingPopulator = mock(CompetitionSetupSectionModelPopulator.class);
		when(notMatchingPopulator.sectionToPopulateModel()).thenReturn(CompetitionSetupSection.MILESTONES);
		
		service.setCompetitionSetupSectionModelPopulators(asList(matchingPopulator, notMatchingPopulator));

		CompetitionSetupSection section = CompetitionSetupSection.ELIGIBILITY;
		
		List<CompetitionSetupSection> completedSections = new ArrayList<>();
		when(competitionService.getCompletedCompetitionSetupSectionStatusesByCompetitionId(8L)).thenReturn(completedSections);
		
		service.populateCompetitionSectionModelAttributes(model, competition, section);
		
		verifyCommonModelAttributes(model, competition, section, completedSections);
		assertEquals("section-eligibility", model.asMap().get("currentSectionFragment"));
		
		verify(matchingPopulator).populateModel(model, competition);
		verify(notMatchingPopulator, never()).populateModel(model, competition);;
	}

	private void verifyCommonModelAttributes(Model model, CompetitionResource competition,
			CompetitionSetupSection section, List<CompetitionSetupSection> completedSections) {
		assertEquals(7, model.asMap().size());
		assertEquals(Boolean.TRUE, model.asMap().get("editable"));
		assertEquals(competition, model.asMap().get("competition"));
		assertEquals(section, model.asMap().get("currentSection"));
		assertArrayEquals(CompetitionSetupSection.values(), (Object[])model.asMap().get("allSections"));
		assertEquals(completedSections, model.asMap().get("allCompletedSections"));
		assertEquals("code: name", model.asMap().get("subTitle"));
	}
	
	@Test
	public void testGetSectionFormData() {
		CompetitionResource competitionResource = newCompetitionResource().build();
		
		CompetitionSetupFormPopulator matchingPopulator = mock(CompetitionSetupFormPopulator.class);
		when(matchingPopulator.sectionToFill()).thenReturn(CompetitionSetupSection.ADDITIONAL_INFO);
		CompetitionSetupForm matchingForm = mock(CompetitionSetupForm.class);
		when(matchingPopulator.populateForm(competitionResource)).thenReturn(matchingForm);
		
		CompetitionSetupFormPopulator otherPopulator = mock(CompetitionSetupFormPopulator.class);
		when(otherPopulator.sectionToFill()).thenReturn(CompetitionSetupSection.APPLICATION_FORM);
		CompetitionSetupForm otherForm = mock(CompetitionSetupForm.class);
		when(otherPopulator.populateForm(competitionResource)).thenReturn(otherForm);

		service.setCompetitionSetupFormPopulators(asList(matchingPopulator, otherPopulator));
		
		CompetitionSetupForm result = service.getSectionFormData(competitionResource, CompetitionSetupSection.ADDITIONAL_INFO);
		
		assertEquals(matchingForm, result);
		verify(matchingPopulator).populateForm(competitionResource);
		verify(otherPopulator, never()).populateForm(competitionResource);
	}
	
	@Test
	public void testSaveSection() {
		CompetitionSetupForm competitionSetupForm = new AdditionalInfoForm();
		CompetitionResource competitionResource = newCompetitionResource().build();
		
		CompetitionSetupSectionSaver matchingSaver = mock(CompetitionSetupSectionSaver.class);
		when(matchingSaver.sectionToSave()).thenReturn(CompetitionSetupSection.ADDITIONAL_INFO);
		when(matchingSaver.supportsForm(AdditionalInfoForm.class)).thenReturn(true);
		
		CompetitionSetupSectionSaver otherSaver = mock(CompetitionSetupSectionSaver.class);
		when(otherSaver.sectionToSave()).thenReturn(CompetitionSetupSection.APPLICATION_FORM);
		when(otherSaver.supportsForm(AdditionalInfoForm.class)).thenReturn(false);


		service.setCompetitionSetupSectionSavers(asList(matchingSaver, otherSaver));
		
		service.saveCompetitionSetupSection(competitionSetupForm, competitionResource, CompetitionSetupSection.ADDITIONAL_INFO);
		
		verify(matchingSaver).saveSection(competitionResource, competitionSetupForm);
		verify(otherSaver, never()).saveSection(competitionResource, competitionSetupForm);
	}
	
}
