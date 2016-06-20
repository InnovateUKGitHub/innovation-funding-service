package com.worth.ifs.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.worth.ifs.application.service.CategoryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.controller.form.CompetitionSetupForm;
import com.worth.ifs.controller.form.CompetitionSetupInitialDetailsForm;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.service.UserService;

@Service
public class CompetitionSetupServiceImpl implements CompetitionSetupService {

	@Autowired
	private CompetitionService competitionService;

	@Autowired
	private UserService userService;

	@Autowired
	private CategoryService categoryService;

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
	}

	@Override
	public CompetitionSetupForm getSectionFormData(CompetitionResource competitionResource,
			CompetitionSetupSection section) {
		CompetitionSetupForm competitionSetupForm;

		switch (section) {
		case INITIAL_DETAILS:
			competitionSetupForm = fillFirstSectionFormSection(competitionResource);
			break;
		default:
			competitionSetupForm = fillFirstSectionFormSection(competitionResource);
			break;
		}

		return competitionSetupForm;
	}
	
	@Override
	public void saveCompetitionSetupSection(CompetitionSetupForm competitionSetupForm,
			CompetitionResource competitionResource, CompetitionSetupSection section) {
		switch (section) {
		case INITIAL_DETAILS:
			saveInitialDetailSection((CompetitionSetupInitialDetailsForm) competitionSetupForm, competitionResource);
			break;
		}

		competitionService.setSetupSectionMarkedAsComplete(competitionResource.getId(), section);
	}

	private CompetitionSetupForm fillFirstSectionFormSection(CompetitionResource competitionResource) {
		CompetitionSetupInitialDetailsForm competitionSetupForm = new CompetitionSetupInitialDetailsForm();

		competitionSetupForm.setCompetitionTypeId(competitionResource.getCompetitionType());
		competitionSetupForm.setExecutiveUserId(competitionResource.getExecutive());

		competitionSetupForm.setInnovationAreaCategoryId(competitionResource.getInnovationArea());
		competitionSetupForm.setLeadTechnologistUserId(competitionResource.getLeadTechnologist());

		if (competitionResource.getStartDate() != null) {
			competitionSetupForm.setOpeningDateDay(competitionResource.getStartDate().getDayOfMonth());
			competitionSetupForm.setOpeningDateMonth(competitionResource.getStartDate().getMonth().getValue());
			competitionSetupForm.setOpeningDateYear(competitionResource.getStartDate().getYear());
		}

		competitionSetupForm.setCompetitionCode(competitionResource.getCode());
		competitionSetupForm.setPafNumber(competitionResource.getPafCode());
		competitionSetupForm.setTitle(competitionResource.getName());
		competitionSetupForm.setBudgetCode(competitionResource.getBudgetCode());

		return competitionSetupForm;
	}

	private void saveInitialDetailSection(CompetitionSetupInitialDetailsForm competitionSetupForm,
			CompetitionResource competition) {
		competition.setName(competitionSetupForm.getTitle());
		competition.setBudgetCode(competitionSetupForm.getBudgetCode());
		competition.setExecutive(competitionSetupForm.getExecutiveUserId());

		try {
			LocalDateTime startDate = LocalDateTime.of(competitionSetupForm.getOpeningDateYear(),
					competitionSetupForm.getOpeningDateMonth(), competitionSetupForm.getOpeningDateDay(), 0, 0);
			competition.setStartDate(startDate);
		} catch (Exception e) {
			competition.setStartDate(null);
		}
		competition.setCompetitionType(competitionSetupForm.getCompetitionTypeId());
		competition.setLeadTechnologist(competitionSetupForm.getLeadTechnologistUserId());
		competition.setPafCode(competitionSetupForm.getPafNumber());

		competition.setInnovationArea(competitionSetupForm.getInnovationAreaCategoryId());
		competition.setInnovationSector(competitionSetupForm.getInnovationSectorCategoryId());

		competitionService.update(competition);

		competitionSetupForm.setCompetitionCode(competition.getCode());
	}
}
