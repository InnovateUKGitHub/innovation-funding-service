package org.innovateuk.ifs.application.areas.populator;

import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.areas.viewmodel.ResearchCategoryViewModel;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionType.RESEARCH_CATEGORY;

/**
 * Populates the research category selection viewmodel.
 */
@Component
public class ApplicationResearchCategoryModelPopulator {

    private ApplicantRestService applicantRestService;
    private CategoryRestService categoryRestService;
    private FinanceService financeService;
    private QuestionRestService questionRestService;
    private UserService userService;

    public ApplicationResearchCategoryModelPopulator(final ApplicantRestService applicantRestService,
                                                     final CategoryRestService categoryRestService,
                                                     final FinanceService financeService,
                                                     final QuestionRestService questionRestService,
                                                     final UserService userService) {
        this.applicantRestService = applicantRestService;
        this.categoryRestService = categoryRestService;
        this.financeService = financeService;
        this.questionRestService = questionRestService;
        this.userService = userService;
    }

    public ResearchCategoryViewModel populate(ApplicationResource applicationResource,
                                              long loggedInUserId,
                                              long questionId) {
        boolean hasApplicationFinances = hasApplicationFinances(applicationResource);
        List<ResearchCategoryResource> researchCategories = categoryRestService.getResearchCategories().getSuccess();

        return new ResearchCategoryViewModel(applicationResource.getCompetitionName(),
                applicationResource.getId(),
                questionId,
                researchCategories,
                hasApplicationFinances,
                !isCompetitionOpen(applicationResource),
                isComplete(applicationResource, loggedInUserId),
                userService.isLeadApplicant(loggedInUserId, applicationResource));
    }

    private boolean isCompetitionOpen(ApplicationResource applicationResource) {
        return CompetitionStatus.OPEN == applicationResource.getCompetitionStatus();
    }

    private boolean isComplete(ApplicationResource applicationResource, long loggedInUserId) {
        return questionRestService.getQuestionByCompetitionIdAndCompetitionSetupQuestionType(
                applicationResource.getCompetition(), RESEARCH_CATEGORY).handleSuccessOrFailure(
                failure -> false,
                success -> {
                    ApplicantQuestionResource question = applicantRestService.getQuestion(loggedInUserId,
                            applicationResource.getId(), success.getId());
                    return question.isCompleteByApplicant(question.getCurrentApplicant());
                });
    }

    private boolean hasApplicationFinances(ApplicationResource applicationResource) {
        if (applicationResource.getResearchCategory() != null
                && applicationResource.getResearchCategory().getId() != null) {
            return financeService.getApplicationFinanceDetails
                    (applicationResource.getId())
                    .stream()
                    .anyMatch(applicationFinanceResource -> applicationFinanceResource.getOrganisationSize() != null);
        }
        return false;
    }
}
