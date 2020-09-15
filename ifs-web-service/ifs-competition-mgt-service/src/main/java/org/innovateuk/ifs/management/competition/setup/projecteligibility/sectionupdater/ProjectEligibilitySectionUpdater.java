package org.innovateuk.ifs.management.competition.setup.projecteligibility.sectionupdater;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.management.competition.setup.projecteligibility.form.ProjectEligibilityForm;
import org.innovateuk.ifs.management.funding.form.enumerable.ResearchParticipationAmount;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.management.competition.setup.application.sectionupdater.AbstractSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.core.util.CompetitionUtils;
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
import static org.innovateuk.ifs.management.funding.form.enumerable.ResearchParticipationAmount.NONE;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.PROJECT_ELIGIBILITY;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;

/**
 * Competition setup section saver for the eligibility section.
 */
@Service
public class ProjectEligibilitySectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSectionUpdater {

    public static final String RESEARCH_CATEGORY_ID = "researchCategoryId";
    public static final String LEAD_APPLICANT_TYPES = "leadApplicantTypes";

    private QuestionRestService questionRestService;
    private CompetitionSetupRestService competitionSetupRestService;
    private GrantClaimMaximumRestService grantClaimMaximumRestService;
    private QuestionSetupCompetitionRestService questionSetupCompetitionRestService;

    public ProjectEligibilitySectionUpdater(CompetitionSetupRestService competitionSetupRestService,
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
        return PROJECT_ELIGIBILITY;
    }

    @Override
    protected ServiceResult<Void> doSaveSection(
            CompetitionResource competition,
            CompetitionSetupForm competitionSetupForm
    ) {
        ProjectEligibilityForm projectEligibilityForm = (ProjectEligibilityForm) competitionSetupForm;

        competition.setResearchCategories(projectEligibilityForm.getResearchCategoryId());

        if (competition.isNonFinanceType()) {
            competition.setMaxResearchRatio(NONE.getAmount());
        } else {
            ResearchParticipationAmount amount = ResearchParticipationAmount.fromId(projectEligibilityForm.getResearchParticipationAmountId());

            if (amount != null) {
                competition.setMaxResearchRatio(amount.getAmount());
            }
        }

        boolean multiStream = "yes".equals(projectEligibilityForm.getMultipleStream());
        competition.setMultiStream(multiStream);

        if (multiStream) {
            competition.setStreamName(projectEligibilityForm.getStreamName());
        } else {
            competition.setStreamName(null);
        }

        handleResearchCategoryApplicableChanges(competition, projectEligibilityForm);
        handleGrantClaimMaximumChanges(competition, projectEligibilityForm);

        competition.setResubmission(CompetitionUtils.textToBoolean(projectEligibilityForm.getResubmission()));

        CollaborationLevel level = CollaborationLevel.fromCode(projectEligibilityForm.getSingleOrCollaborative());
        competition.setCollaborationLevel(level);
        competition.setLeadApplicantTypes(projectEligibilityForm.getLeadApplicantTypes());


        return competitionSetupRestService.update(competition).toServiceResult();
    }

    private ServiceResult<Void> handleResearchCategoryApplicableChanges(CompetitionResource competitionResource,
                                                                        ProjectEligibilityForm projectEligibilityForm) {

        Optional<QuestionResource> researchCategoryQuestionIfExists = getResearchCategoryQuestionIfExists
                (competitionResource.getId());

        if (projectEligibilityForm.getResearchCategoriesApplicable()) {
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
                                                ProjectEligibilityForm projectEligibilityForm) {

        if (projectEligibilityForm.getConfiguredFundingLevelPercentage() != null) {
            Set<GrantClaimMaximumResource> grantClaimMaximums = competition.getGrantClaimMaximums().stream()
                    .map(id -> grantClaimMaximumRestService.getGrantClaimMaximumById(id).getSuccess())
                    .collect(Collectors.toSet());

            grantClaimMaximums.forEach(oldGCM -> {
                GrantClaimMaximumResource toSaveGCM = createNewGCM(oldGCM, projectEligibilityForm.getConfiguredFundingLevelPercentage());

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
        return ProjectEligibilityForm.class.equals(clazz);
    }

}
