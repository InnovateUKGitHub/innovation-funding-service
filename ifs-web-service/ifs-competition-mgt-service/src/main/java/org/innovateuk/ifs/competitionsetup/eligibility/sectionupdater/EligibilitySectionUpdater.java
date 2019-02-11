package org.innovateuk.ifs.competitionsetup.eligibility.sectionupdater;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.form.enumerable.ResearchParticipationAmount;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competitionsetup.application.sectionupdater.AbstractSectionUpdater;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.sectionupdater.CompetitionSetupSectionUpdater;
import org.innovateuk.ifs.competitionsetup.core.util.CompetitionUtils;
import org.innovateuk.ifs.competitionsetup.eligibility.form.EligibilityForm;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.service.QuestionSetupCompetitionRestService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.form.enumerable.ResearchParticipationAmount.NONE;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.ELIGIBILITY;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;

/**
 * Competition setup section saver for the eligibility section.
 */
@Service
public class EligibilitySectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSectionUpdater {

    public static final String RESEARCH_CATEGORY_ID = "researchCategoryId";
    public static final String LEAD_APPLICANT_TYPES = "leadApplicantTypes";

    private QuestionRestService questionRestService;
    private CompetitionSetupRestService competitionSetupRestService;
    private GrantClaimMaximumRestService grantClaimMaximumRestService;
    private QuestionSetupCompetitionRestService questionSetupCompetitionRestService;

    public EligibilitySectionUpdater(CompetitionSetupRestService competitionSetupRestService,
                                     GrantClaimMaximumRestService grantClaimMaximumRestService,
                                     QuestionRestService questionRestService,
                                     QuestionSetupCompetitionRestService questionSetupCompetitionRestService) {
        this.competitionSetupRestService = competitionSetupRestService;
        this.grantClaimMaximumRestService = grantClaimMaximumRestService;
        this.questionRestService = questionRestService;
        this.questionSetupCompetitionRestService = questionSetupCompetitionRestService;
    }

    @Override
    public CompetitionSetupSection sectionToSave() {
        return ELIGIBILITY;
    }

    @Override
    protected ServiceResult<Void> doSaveSection(
            CompetitionResource competition,
            CompetitionSetupForm competitionSetupForm
    ) {
        EligibilityForm eligibilityForm = (EligibilityForm) competitionSetupForm;

        competition.setResearchCategories(eligibilityForm.getResearchCategoryId());

        if (competition.isNonFinanceType()) {
            competition.setMaxResearchRatio(NONE.getAmount());
        } else {
            ResearchParticipationAmount amount = ResearchParticipationAmount.fromId(eligibilityForm.getResearchParticipationAmountId());

            if (amount != null) {
                competition.setMaxResearchRatio(amount.getAmount());
            }
        }

        boolean multiStream = "yes".equals(eligibilityForm.getMultipleStream());
        competition.setMultiStream(multiStream);

        if (multiStream) {
            competition.setStreamName(eligibilityForm.getStreamName());
        } else {
            competition.setStreamName(null);
        }

        if(!competitionSetupForm.isAutoSaveAction()) {
            handleResearchCategoryApplicableChanges(competition, eligibilityForm);
            handleGrantClaimMaximumChanges(competition, eligibilityForm);
        }

        competition.setResubmission(CompetitionUtils.textToBoolean(eligibilityForm.getResubmission()));

        CollaborationLevel level = CollaborationLevel.fromCode(eligibilityForm.getSingleOrCollaborative());
        competition.setCollaborationLevel(level);
        competition.setLeadApplicantTypes(eligibilityForm.getLeadApplicantTypes());

        return competitionSetupRestService.update(competition).toServiceResult();
    }

    private ServiceResult<Void> handleResearchCategoryApplicableChanges(CompetitionResource competitionResource,
                                                                        EligibilityForm eligibilityForm) {

        Optional<QuestionResource> researchCategoryQuestionIfExists = getResearchCategoryQuestionIfExists
                (competitionResource.getId());

        if (eligibilityForm.getResearchCategoriesApplicable()) {
            if (!researchCategoryQuestionIfExists.isPresent()) {
                questionSetupCompetitionRestService.addResearchCategoryQuestionToCompetition(competitionResource
                        .getId());
            }
        } else {
            if (researchCategoryQuestionIfExists.isPresent()) {
                QuestionResource researchCategoryQuestion = researchCategoryQuestionIfExists.get();
                return questionSetupCompetitionRestService.deleteById(researchCategoryQuestion.getId())
                        .toServiceResult();
            }
        }
        return serviceSuccess();
    }

    private void handleGrantClaimMaximumChanges(CompetitionResource competition,
                                                EligibilityForm eligibilityForm) {

        if (eligibilityForm.getConfiguredFundingLevelPercentage() != null) {
            Set<GrantClaimMaximumResource> grantClaimMaximums = competition.getGrantClaimMaximums().stream()
                    .map(id -> grantClaimMaximumRestService.getGrantClaimMaximumById(id).getSuccess())
                    .collect(Collectors.toSet());

            grantClaimMaximums.forEach(oldGCM -> {
                GrantClaimMaximumResource toSaveGCM = createNewGCM(oldGCM, eligibilityForm.getConfiguredFundingLevelPercentage());

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

            // remove the old
            competition.getGrantClaimMaximums().clear();

            //save the new
            competition.getGrantClaimMaximums().addAll(gcmsForCompetitionType);
        }
    }

    private GrantClaimMaximumResource createNewGCM(GrantClaimMaximumResource oldGCM, Integer newValue) {
        GrantClaimMaximumResource newGcm = new GrantClaimMaximumResource();
        newGcm.setOrganisationSize(oldGCM.getOrganisationSize());
        newGcm.setResearchCategory(oldGCM.getResearchCategory());
        newGcm.setMaximum(newValue);
        return newGcm;
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

    private Optional<QuestionResource> getResearchCategoryQuestionIfExists(long competitionId) {
        RestResult<QuestionResource> researchCategoryQuestionResult = questionRestService
                .getQuestionByCompetitionIdAndQuestionSetupType(competitionId, RESEARCH_CATEGORY);
        return researchCategoryQuestionResult.toOptionalIfNotFound().getSuccess();
    }

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
        return EligibilityForm.class.equals(clazz);
    }

}
