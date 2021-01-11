package org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.sectionupdater;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.management.competition.setup.application.sectionupdater.AbstractSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.form.FundingLevelMaximumForm;
import org.innovateuk.ifs.management.competition.setup.fundinglevelpercentage.form.FundingLevelPercentageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.commons.service.ServiceResult.aggregate;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.FUNDING_LEVEL_PERCENTAGE;

@Service
public class FundingLevelPercentageSectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSectionUpdater {

    @Autowired
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    @Autowired
    private CategoryRestService categoryRestService;

    @Override
    public CompetitionSetupSection sectionToSave() {
        return FUNDING_LEVEL_PERCENTAGE;
    }

    @Override
    protected ServiceResult<Void> doSaveSection(
            CompetitionResource competition,
            CompetitionSetupForm competitionSetupForm
    ) {
        FundingLevelPercentageForm form = (FundingLevelPercentageForm) competitionSetupForm;
        if (competition.isNonFinanceType()) {
            return serviceSuccess();
        }
        if (form.getMaximums().size() == 1) {
            return saveSingleValue(form.getMaximums().get(0).get(0), competition);
        } else {
            return saveTableOfMaximums(form, competition);
        }
    }

    private ServiceResult<Void> saveTableOfMaximums(FundingLevelPercentageForm form, CompetitionResource competition) {
        Map<Long, ResearchCategoryResource> researchCategories = categoryRestService.getResearchCategories().getSuccess().stream()
                .collect(toMap(ResearchCategoryResource::getId, Function.identity()));
        return aggregate(form.getMaximums().stream().flatMap(Collection::stream)
                .map(maximumForm -> saveTableFormCell(maximumForm, researchCategories))
                .collect(toList()))
                .andOnSuccess(() -> {
                    if (researchCategories.size() != competition.getResearchCategories().size()) {
                        return handleMissingResearchCategoriesFromTableForm(competition);
                    } else {
                        return serviceSuccess();
                    }
                });
    }

    private ServiceResult<GrantClaimMaximumResource> saveTableFormCell(FundingLevelMaximumForm maximumForm, Map<Long, ResearchCategoryResource> researchCategories) {
        GrantClaimMaximumResource grantClaimMaximumResource = new GrantClaimMaximumResource();
        grantClaimMaximumResource.setMaximum(maximumForm.getMaximum());
        grantClaimMaximumResource.setId(maximumForm.getGrantClaimMaximumId());
        grantClaimMaximumResource.setOrganisationSize(maximumForm.getOrganisationSize());
        grantClaimMaximumResource.setResearchCategory(researchCategories.get(maximumForm.getCategoryId()));
        return grantClaimMaximumRestService.save(grantClaimMaximumResource).toServiceResult();
    }

    private ServiceResult<Void> saveSingleValue(FundingLevelMaximumForm form, CompetitionResource competition) {
        return aggregate(grantClaimMaximumRestService.getGrantClaimMaximumByCompetitionId(competition.getId()).getSuccess()
                .stream()
                .map(max -> {
                    max.setMaximum(form.getMaximum());
                    return grantClaimMaximumRestService.save(max).toServiceResult();
                })
                .collect(toList()))
                .andOnSuccessReturnVoid();
    }

    private ServiceResult<Void> handleMissingResearchCategoriesFromTableForm(CompetitionResource competition) {
        return aggregate(grantClaimMaximumRestService.getGrantClaimMaximumByCompetitionId(competition.getId()).getSuccess()
                .stream()
                .filter(max -> !competition.getResearchCategories().contains(max.getResearchCategory().getId()))
                .map(max -> {
                    max.setMaximum(0);
                    return grantClaimMaximumRestService.save(max).toServiceResult();
                })
                .collect(toList()))
                .andOnSuccessReturnVoid();
    }

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
        return FundingLevelPercentageForm.class.equals(clazz);
    }
}
