package org.innovateuk.ifs.competitionsetup.service.modelpopulator;

import org.innovateuk.ifs.application.service.CategoryService;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.competition.form.enumerable.ResearchParticipationAmount;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CategoryFormatter;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * populates the model for the eligibility competition setup section.
 */
@Service
public class EligibilityModelPopulator implements CompetitionSetupSectionModelPopulator {

	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private CategoryFormatter categoryFormatter;

    @Autowired
    private OrganisationTypeRestService organisationTypeRestService;
	
	@Override
	public CompetitionSetupSection sectionToPopulateModel() {
		return CompetitionSetupSection.ELIGIBILITY;
	}

	@Override
	public void populateModel(Model model, CompetitionResource competitionResource) {
		model.addAttribute("researchParticipationAmounts", ResearchParticipationAmount.values());
		model.addAttribute("collaborationLevels", CollaborationLevel.values());
        List<OrganisationTypeResource> organisationTypes = organisationTypeRestService.getAll().getSuccessObject();

        List<OrganisationTypeResource> leadApplicantTypes = organisationTypes.stream()
                .filter(organisationType -> organisationType.getVisibleInSetup())
                .collect(toList());

		model.addAttribute("leadApplicantTypes", leadApplicantTypes);
        model.addAttribute("leadApplicantTypesText", leadApplicantTypes.stream()
                .filter(organisationTypeResource -> competitionResource.getLeadApplicantTypes().contains(organisationTypeResource.getId()))
                .map(organisationTypeResource -> organisationTypeResource.getName())
                .collect(Collectors.joining(", ")));
		List<ResearchCategoryResource> researchCategories = categoryService.getResearchCategories();
		model.addAttribute("researchCategories",researchCategories);
		model.addAttribute("researchCategoriesFormatted", categoryFormatter.format(competitionResource.getResearchCategories(), researchCategories));
	}
}
