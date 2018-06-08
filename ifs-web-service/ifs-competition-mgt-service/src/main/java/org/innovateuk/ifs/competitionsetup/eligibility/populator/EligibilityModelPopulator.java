package org.innovateuk.ifs.competitionsetup.eligibility.populator;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.form.enumerable.ResearchParticipationAmount;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CategoryFormatter;
import org.innovateuk.ifs.competitionsetup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.eligibility.viewmodel.EligibilityViewModel;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

/**
 * populates the model for the eligibility competition setup section.
 */
@Service
public class EligibilityModelPopulator implements CompetitionSetupSectionModelPopulator {

    @Autowired
    private CategoryRestService categoryRestService;

    @Autowired
    private CategoryFormatter categoryFormatter;

    @Autowired
    private OrganisationTypeRestService organisationTypeRestService;

    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return CompetitionSetupSection.ELIGIBILITY;
    }

    @Override
    public CompetitionSetupViewModel populateModel(
            GeneralSetupViewModel generalViewModel,
            CompetitionResource competitionResource
    ) {
        List<OrganisationTypeResource> organisationTypes = organisationTypeRestService.getAll().getSuccess();
        List<OrganisationTypeResource> leadApplicantTypes = simpleFilter(
                organisationTypes,
                OrganisationTypeResource::getVisibleInSetup
        );

        String leadApplicantTypesText = leadApplicantTypes.stream()
                .filter(organisationTypeResource ->
                                competitionResource.getLeadApplicantTypes().contains(organisationTypeResource.getId())
                )
                .map(OrganisationTypeResource::getName)
                .collect(Collectors.joining(", "));

        List<ResearchCategoryResource> researchCategories = categoryRestService.getResearchCategories().getSuccess();
        String researchCategoriesFormatted = categoryFormatter.format(
                competitionResource.getResearchCategories(),
                researchCategories
        );

        return new EligibilityViewModel(
                generalViewModel,
                getResearchParticipationAmounts(competitionResource),
                CollaborationLevel.values(),
                leadApplicantTypes,
                leadApplicantTypesText,
                researchCategories,
                researchCategoriesFormatted
        );
    }

    private ResearchParticipationAmount[] getResearchParticipationAmounts(CompetitionResource competitionResource) {
        if (competitionResource.isFullApplicationFinance() != null) {
            return ResearchParticipationAmount.values();
        }

        return new ResearchParticipationAmount[]{};
    }
}
