package org.innovateuk.ifs.application.forms.researchcategory.populator;

import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.forms.researchcategory.viewmodel.ResearchCategorySummaryViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;

/**
 * Populates the research category selection viewmodel.
 */
@Component
public class ApplicationResearchCategorySummaryModelPopulator extends AbstractLeadOnlyModelPopulator {

    private QuestionRestService questionRestService;

    public ApplicationResearchCategorySummaryModelPopulator(ApplicantRestService applicantRestService,
                                                            QuestionRestService questionRestService) {
        super(applicantRestService, questionRestService);
        this.questionRestService = questionRestService;
    }

    public ResearchCategorySummaryViewModel populate(ApplicationResource applicationResource,
                                                     long loggedInUserId,
                                                     boolean userIsLeadApplicant) {
        String researchCategoryName = Optional.of(applicationResource.getResearchCategory())
                .map(ResearchCategoryResource::getName).orElse(null);

        boolean isComplete = isComplete(applicationResource, loggedInUserId);
        boolean allReadOnly = !userIsLeadApplicant || isComplete;

        return new ResearchCategorySummaryViewModel(applicationResource.getId(),
                getResearchCategoryQuestion(applicationResource.getCompetition()),
                researchCategoryName,
                !isCompetitionOpen(applicationResource),
                isComplete,
                userIsLeadApplicant,
                allReadOnly
        );
    }

    private Long getResearchCategoryQuestion(long competitionId) {
        return questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competitionId,
                RESEARCH_CATEGORY).handleSuccessOrFailure(failure -> null, QuestionResource::getId);
    }
}
