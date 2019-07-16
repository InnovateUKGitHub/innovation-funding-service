package org.innovateuk.ifs.management.competition.setup.eligibility.populator;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.management.funding.form.enumerable.ResearchParticipationAmount;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.service.CategoryFormatter;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.eligibility.viewmodel.EligibilityViewModel;
import org.innovateuk.ifs.finance.resource.FundingLevel;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

/**
 * populates the model for the eligibility competition setup section.
 */
@Service
public class EligibilityModelPopulator implements CompetitionSetupSectionModelPopulator {

    private CategoryRestService categoryRestService;
    private CategoryFormatter categoryFormatter;
    private OrganisationTypeRestService organisationTypeRestService;

    public EligibilityModelPopulator(CategoryRestService categoryRestService,
                                     CategoryFormatter categoryFormatter,
                                     OrganisationTypeRestService organisationTypeRestService) {
        this.categoryRestService = categoryRestService;
        this.categoryFormatter = categoryFormatter;
        this.organisationTypeRestService = organisationTypeRestService;
    }

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
                FundingLevel.values(),
                researchCategories,
                researchCategoriesFormatted
        );
    }

    private ResearchParticipationAmount[] getResearchParticipationAmounts(CompetitionResource competitionResource) {
        if (competitionResource.isNonFinanceType()) {
            return new ResearchParticipationAmount[]{};
        }

        return ResearchParticipationAmount.values();
    }
}
