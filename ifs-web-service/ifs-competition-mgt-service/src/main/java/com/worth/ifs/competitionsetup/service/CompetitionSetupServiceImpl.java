package com.worth.ifs.competitionsetup.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.service.formpopulator.CompetitionSetupFormPopulator;
import com.worth.ifs.competitionsetup.service.modelpopulator.CompetitionSetupSectionModelPopulator;
import com.worth.ifs.competitionsetup.service.sectionupdaters.CompetitionSetupSectionSaver;

@Service
public class CompetitionSetupServiceImpl implements CompetitionSetupService {

	private static final Log LOG = LogFactory.getLog(CompetitionSetupServiceImpl.class);
	
	@Autowired
	private CompetitionService competitionService;

    private Map<CompetitionSetupSection, CompetitionSetupFormPopulator> formPopulators;
	
	private Map<CompetitionSetupSection, CompetitionSetupSectionSaver> sectionSavers;

	private Map<CompetitionSetupSection, CompetitionSetupSectionModelPopulator> modelPopulators;

	
	@Autowired
	public void setCompetitionSetupFormPopulators(Collection<CompetitionSetupFormPopulator> populators) {
		formPopulators = populators.stream().collect(Collectors.toMap(p -> p.sectionToFill(), Function.identity()));
	}
	
	@Autowired
	public void setCompetitionSetupSectionSavers(Collection<CompetitionSetupSectionSaver> savers) {
		sectionSavers = savers.stream().collect(Collectors.toMap(p -> p.sectionToSave(), Function.identity()));
	}
	
	@Autowired
	public void setCompetitionSetupSectionModelPopulators(Collection<CompetitionSetupSectionModelPopulator> populators) {
		modelPopulators = populators.stream().collect(Collectors.toMap(p -> p.sectionToPopulateModel(), Function.identity()));
	}
	
	@Override
	public void populateCompetitionSectionModelAttributes(Model model, CompetitionResource competitionResource,
			CompetitionSetupSection section) {
		
		populateGeneralModelAttributes(model, competitionResource, section);
		
		CompetitionSetupSectionModelPopulator populator = modelPopulators.get(section);
		
		if(populator != null) {
			populator.populateModel(model, competitionResource);
		}
	}

	@Override
	public CompetitionSetupForm getSectionFormData(CompetitionResource competitionResource,
			CompetitionSetupSection section) {
		
		CompetitionSetupFormPopulator populator = formPopulators.get(section);
		if(populator == null) {
			LOG.error("unable to populate form for section " + section);
			throw new IllegalArgumentException();
		}
		
		return populator.populateForm(competitionResource);
	}
	
	@Override
	public void saveCompetitionSetupSection(CompetitionSetupForm competitionSetupForm,
			CompetitionResource competitionResource, CompetitionSetupSection section) {
		
		CompetitionSetupSectionSaver saver = sectionSavers.get(section);
		if(saver == null || !saver.supportsForm(competitionSetupForm.getClass())) {
			LOG.error("unable to save section " + section);
			throw new IllegalArgumentException();
		}
		
		saver.saveSection(competitionResource, competitionSetupForm);
		
		competitionService.setSetupSectionMarkedAsComplete(competitionResource.getId(), section);
	}

	private void populateGeneralModelAttributes(Model model, CompetitionResource competitionResource, CompetitionSetupSection section) {
		List<CompetitionSetupSection> completedSections = competitionService
				.getCompletedCompetitionSetupSectionStatusesByCompetitionId(competitionResource.getId());

		boolean editable = !completedSections.contains(section);
		model.addAttribute("editable", editable);

		model.addAttribute("competition", competitionResource);
		model.addAttribute("currentSection", section);
		model.addAttribute("currentSectionFragment", "section-" + section.getPath());

		model.addAttribute("allSections", CompetitionSetupSection.values());
		model.addAttribute("allCompletedSections", completedSections);
		model.addAttribute("subTitle",
				(competitionResource.getCode() != null ? competitionResource.getCode() : "Unknown") + ": "
						+ (competitionResource.getName() != null ? competitionResource.getName() : "Unknown"));
	}

}
