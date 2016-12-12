package org.innovateuk.ifs.competitionsetup.service.modelpopulator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import org.innovateuk.ifs.application.service.CategoryService;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.category.resource.CategoryType;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.LeadApplicantType;
import org.innovateuk.ifs.competition.form.enumerable.ResearchParticipationAmount;
import org.innovateuk.ifs.competition.service.CategoryFormatter;

/**
 * populates the model for the eligibility competition setup section.
 */
@Service
public class EligibilityModelPopulator implements CompetitionSetupSectionModelPopulator {

	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private CategoryFormatter categoryFormatter;
	
	@Override
	public CompetitionSetupSection sectionToPopulateModel() {
		return CompetitionSetupSection.ELIGIBILITY;
	}

	@Override
	public void populateModel(Model model, CompetitionResource competitionResource) {
		model.addAttribute("researchParticipationAmounts", ResearchParticipationAmount.values());
		model.addAttribute("collaborationLevels", CollaborationLevel.values());
		model.addAttribute("leadApplicantTypes", LeadApplicantType.values());
		List<CategoryResource> researchCategories = categoryService.getCategoryByType(CategoryType.RESEARCH_CATEGORY);
		model.addAttribute("researchCategories",researchCategories);
		model.addAttribute("researchCategoriesFormatted", categoryFormatter.format(competitionResource.getResearchCategories(), researchCategories));
	}

}
