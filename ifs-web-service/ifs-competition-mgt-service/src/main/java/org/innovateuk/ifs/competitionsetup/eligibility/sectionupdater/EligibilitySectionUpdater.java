package org.innovateuk.ifs.competitionsetup.eligibility.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.form.enumerable.ResearchParticipationAmount;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competitionsetup.application.sectionupdater.AbstractSectionUpdater;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.sectionupdater.CompetitionSetupSectionUpdater;
import org.innovateuk.ifs.competitionsetup.core.util.CompetitionUtils;
import org.innovateuk.ifs.competitionsetup.eligibility.form.EligibilityForm;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Competition setup section saver for the eligibility section.
 */
@Service
public class EligibilitySectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSectionUpdater {

    public static final String RESEARCH_CATEGORY_ID = "researchCategoryId";
    public static final String LEAD_APPLICANT_TYPES = "leadApplicantTypes";

    private CompetitionSetupRestService competitionSetupRestService;
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    public EligibilitySectionUpdater(CompetitionSetupRestService competitionSetupRestService,
                                     GrantClaimMaximumRestService grantClaimMaximumRestService) {
        this.competitionSetupRestService = competitionSetupRestService;
        this.grantClaimMaximumRestService = grantClaimMaximumRestService;
    }

    @Override
    public CompetitionSetupSection sectionToSave() {
        return CompetitionSetupSection.ELIGIBILITY;
    }

    @Override
    protected ServiceResult<Void> doSaveSection(
            CompetitionResource competition,
            CompetitionSetupForm competitionSetupForm
    ) {
        EligibilityForm eligibilityForm = (EligibilityForm) competitionSetupForm;

        competition.setResearchCategories(eligibilityForm.getResearchCategoryId());

        if (competition.isFullApplicationFinance() != null) {
            ResearchParticipationAmount amount = ResearchParticipationAmount.fromId(eligibilityForm.getResearchParticipationAmountId());

            if (amount != null) {
                competition.setMaxResearchRatio(amount.getAmount());
            }
        } else {
            competition.setMaxResearchRatio(ResearchParticipationAmount.NONE.getAmount());
        }

        boolean multiStream = "yes".equals(eligibilityForm.getMultipleStream());
        competition.setMultiStream(multiStream);

        if (multiStream) {
            competition.setStreamName(eligibilityForm.getStreamName());
        } else {
            competition.setStreamName(null);
        }

        handleGrantClaimMaximumChanges(competition, eligibilityForm);

        competition.setResubmission(CompetitionUtils.textToBoolean(eligibilityForm.getResubmission()));

        CollaborationLevel level = CollaborationLevel.fromCode(eligibilityForm.getSingleOrCollaborative());
        competition.setCollaborationLevel(level);
        competition.setLeadApplicantTypes(eligibilityForm.getLeadApplicantTypes());

        return competitionSetupRestService.update(competition).toServiceResult();
    }

    private void handleGrantClaimMaximumChanges(CompetitionResource competition,
                                                EligibilityForm eligibilityForm) {
        Set<GrantClaimMaximumResource> businessGcms = competition.getGrantClaimMaximums().stream()
                .map(id -> grantClaimMaximumRestService.getGrantClaimMaximumById(id).getSuccess())
                .filter(gcm -> gcm.getOrganisationType().getId().equals(OrganisationTypeEnum.BUSINESS.getId()))
                .collect(Collectors.toSet());

        if (eligibilityForm.getOverrideFundingRules()) {
            businessGcms.forEach(oldGCM -> {
                GrantClaimMaximumResource toSaveGCM = createNewGCM(oldGCM, eligibilityForm.getFundingLevelPercentage());

                if (!toSaveGCM.getMaximum().equals(oldGCM.getMaximum())) {
                    // remove the old
                    competition.getGrantClaimMaximums().remove(oldGCM.getId());
                    // save the new
                    GrantClaimMaximumResource saved = grantClaimMaximumRestService.save(toSaveGCM).getSuccess();
                    competition.getGrantClaimMaximums().add(saved.getId());
                }
            });
        } else {
            Set<Long> gcmsForCompetitionType = grantClaimMaximumRestService.getGrantClaimMaximumsForCompetitionType(
                    competition.getCompetitionType()).getSuccess();

            Set<Long> gcmsTemplateIds = filterToOnlyBusinessGcm(gcmsForCompetitionType);
            Set<Long> businessGcmsIds = grantClaimMaximumToIdSet(businessGcms);

            // remove the old
            competition.getGrantClaimMaximums().removeAll(businessGcmsIds);

            //save the new
            competition.getGrantClaimMaximums().addAll(gcmsTemplateIds);
        }
    }

    private GrantClaimMaximumResource createNewGCM(GrantClaimMaximumResource oldGCM, Integer newValue) {
        GrantClaimMaximumResource newGcm = new GrantClaimMaximumResource();
        newGcm.setOrganisationSize(oldGCM.getOrganisationSize());
        newGcm.setOrganisationType(oldGCM.getOrganisationType());
        newGcm.setResearchCategory(oldGCM.getResearchCategory());
        newGcm.setCompetitionType(oldGCM.getCompetitionType());
        newGcm.setMaximum(newValue);
        return newGcm;
    }

    private Set<Long> filterToOnlyBusinessGcm(Set<Long> gcms) {
        return gcms.stream()
                .map(id -> grantClaimMaximumRestService.getGrantClaimMaximumById(id).getSuccess())
                .filter(gcm -> gcm.getOrganisationType().getId().equals(OrganisationTypeEnum.BUSINESS.getId()))
                .map(GrantClaimMaximumResource::getId)
                .collect(Collectors.toSet());
    }

    private Set<Long> grantClaimMaximumToIdSet(Set<GrantClaimMaximumResource> gcms) {
        return gcms.stream().map(GrantClaimMaximumResource::getId).collect(Collectors.toSet());
    }

    @Override
    protected ServiceResult<Void> handleIrregularAutosaveCase(CompetitionResource competitionResource,
                                                              String fieldName,
                                                              String value,
                                                              Optional<Long> questionId) {
        if (RESEARCH_CATEGORY_ID.equals(fieldName)) {
            removeIfPresentAddIfNot(value, competitionResource.getResearchCategories());
            return competitionSetupRestService.update(competitionResource).toServiceResult();
        }
        if (LEAD_APPLICANT_TYPES.equals(fieldName)) {
            removeIfPresentAddIfNot(value, competitionResource.getLeadApplicantTypes());
            return competitionSetupRestService.update(competitionResource).toServiceResult();
        }
        return super.handleIrregularAutosaveCase(competitionResource,
                fieldName,
                value,
                questionId);
    }

    private void removeIfPresentAddIfNot(String inputValue, Collection<Long> collection) {
        Long value = Long.parseLong(inputValue);
        if (collection.contains(value)) {
            collection.remove(value);
        } else {
            collection.add(value);
        }
    }

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
        return EligibilityForm.class.equals(clazz);
    }

}
