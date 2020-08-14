package org.innovateuk.ifs.management.competition.setup.projecteligibility.populator;

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
import org.innovateuk.ifs.management.competition.setup.projecteligibility.viewmodel.ProjectEligibilityViewModel;
import org.innovateuk.ifs.finance.resource.FundingLevel;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
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
public class ProjectEligibilityModelPopulator implements CompetitionSetupSectionModelPopulator {

    private CategoryRestService categoryRestService;
    private CategoryFormatter categoryFormatter;
    private OrganisationTypeRestService organisationTypeRestService;

    public ProjectEligibilityModelPopulator(CategoryRestService categoryRestService,
                                            CategoryFormatter categoryFormatter,
                                            OrganisationTypeRestService organisationTypeRestService) {
        this.categoryRestService = categoryRestService;
        this.categoryFormatter = categoryFormatter;
        this.organisationTypeRestService = organisationTypeRestService;
    }

    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return CompetitionSetupSection.PROJECT_ELIGIBILITY;
    }

    @Override
    public CompetitionSetupViewModel populateModel(
            GeneralSetupViewModel generalViewModel,
            CompetitionResource competitionResource
    ) {
        List<OrganisationTypeResource> organisationTypes = organisationTypeRestService.getAll().getSuccess();

//        this is rubbish
        List<OrganisationTypeResource> leadApplicantTypes;
        if (competitionResource.isKtp()) {
            leadApplicantTypes = simpleFilter(organisationTypes,
                    organisationType ->
                            OrganisationTypeEnum.getFromId(organisationType.getId()).equals(OrganisationTypeEnum.CATAPULT) ||
                                    OrganisationTypeEnum.getFromId(organisationType.getId()).equals(OrganisationTypeEnum.UNIVERSITY) ||
                                    OrganisationTypeEnum.getFromId(organisationType.getId()).equals(OrganisationTypeEnum.RTO));
        } else {
            leadApplicantTypes = simpleFilter(organisationTypes, OrganisationTypeResource::getVisibleInSetup);
        }

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

        return new ProjectEligibilityViewModel(
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
