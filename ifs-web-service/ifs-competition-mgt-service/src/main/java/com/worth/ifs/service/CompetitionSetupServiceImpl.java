package com.worth.ifs.service;

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

import com.worth.ifs.application.service.CategoryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.competition.resource.CollaborationLevel;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.LeadApplicantType;
import com.worth.ifs.controller.form.competitionsetup.CompetitionSetupForm;
import com.worth.ifs.controller.form.enumerable.ResearchParticipationAmount;
import com.worth.ifs.service.competitionsetup.formpopulator.CompetitionSetupFormPopulator;
import com.worth.ifs.service.competitionsetup.sectionupdaters.CompetitionSetupSectionSaver;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.service.UserService;

@Service
public class CompetitionSetupServiceImpl implements CompetitionSetupService {

	private static final Log LOG = LogFactory.getLog(CompetitionSetupServiceImpl.class);
	
	@Autowired
	private CompetitionService competitionService;

	@Autowired
	private UserService userService;

	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private CategoryFormatter categoryFormatter;
	
	private Map<CompetitionSetupSection, CompetitionSetupFormPopulator> formPopulators;
	
	private Map<CompetitionSetupSection, CompetitionSetupSectionSaver> sectionSavers;
	
	@Autowired
	public void setCompetitionSetupFormPopulators(Collection<CompetitionSetupFormPopulator> populators) {
		formPopulators = populators.stream().collect(Collectors.toMap(p -> p.sectionToFill(), Function.identity()));
	}
	
	@Autowired
	public void setCompetitionSetupSectionSavers(Collection<CompetitionSetupSectionSaver> savers) {
		sectionSavers = savers.stream().collect(Collectors.toMap(p -> p.sectionToSave(), Function.identity()));
	}
	
	@Override
	public void populateCompetitionSectionModelAttributes(Model model, CompetitionResource competitionResource,
			CompetitionSetupSection section) {
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

		
		if(CompetitionSetupSection.INITIAL_DETAILS.equals(section)) {
			model.addAttribute("competitionExecutiveUsers", userService.findUserByType(UserRoleType.COMP_EXEC));
			model.addAttribute("innovationSectors", categoryService.getCategoryByType(CategoryType.INNOVATION_SECTOR));
			if (competitionResource.getInnovationSector() != null) {
				model.addAttribute("innovationAreas",
						categoryService.getCategoryByParentId(competitionResource.getInnovationSector()));
			} else {
				model.addAttribute("innovationAreas", categoryService.getCategoryByType(CategoryType.INNOVATION_AREA));
			}
			model.addAttribute("competitionTypes", competitionService.getAllCompetitionTypes());
			model.addAttribute("competitionLeadTechUsers", userService.findUserByType(UserRoleType.COMP_TECHNOLOGIST));
		} else if (CompetitionSetupSection.ELIGIBILITY.equals(section)) {
			model.addAttribute("researchParticipationAmounts", ResearchParticipationAmount.values());
			model.addAttribute("collaborationLevels", CollaborationLevel.values());
			model.addAttribute("leadApplicantTypes", LeadApplicantType.values());
			List<CategoryResource> researchCategories = categoryService.getCategoryByType(CategoryType.RESEARCH_CATEGORY);
			model.addAttribute("researchCategories",researchCategories);
			model.addAttribute("researchCategoriesFormatted", categoryFormatter.format(competitionResource.getResearchCategories(), researchCategories));
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

}
