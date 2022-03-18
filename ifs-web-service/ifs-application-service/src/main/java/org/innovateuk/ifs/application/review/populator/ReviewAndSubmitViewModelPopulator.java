package org.innovateuk.ifs.application.review.populator;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.application.overview.ApplicationOverviewData;
import org.innovateuk.ifs.application.readonly.populator.ApplicationReadOnlyViewModelPopulator;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.review.viewmodel.ReviewAndSubmitViewModel;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings.defaultSettings;

@Slf4j
@Component
public class ReviewAndSubmitViewModelPopulator {

    @Autowired
    private ApplicationReadOnlyViewModelPopulator applicationRowsSummaryViewModelPopulator;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private QuestionStatusRestService questionStatusRestService;

    public ReviewAndSubmitViewModel populate(long applicationId, UserResource user) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        boolean userIsLeadApplicant = userService.isLeadApplicant(user.getId(), application);
        boolean isApplicationReadyForSubmit = applicationRestService.isApplicationReadyForSubmit(applicationId).getSuccess();
        boolean isWaitingForPartnerSubsidyBasisOnly = isWaitingForPartnerSubsidyBasisOnly(application, competition);

        ApplicationReadOnlyViewModel applicationSummaryViewModel = applicationRowsSummaryViewModelPopulator.populate(application, competition, user, defaultSettings()
                .setIncludeQuestionLinks(true)
                .setIncludeStatuses(true));
        return new ReviewAndSubmitViewModel(applicationSummaryViewModel, application, competition,
                isApplicationReadyForSubmit, userIsLeadApplicant, isWaitingForPartnerSubsidyBasisOnly);
    }

    private boolean isWaitingForPartnerSubsidyBasisOnly(ApplicationResource application, CompetitionResource competition) {
        return questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(), QuestionSetupType.NORTHERN_IRELAND_DECLARATION)
                .handleSuccessOrFailure(failure -> false,       // TODO at the moment only subsidy-tactical competition needs this checking, subsidy-strategic is not handled yet
                        question -> {
                            List<QuestionStatusResource> questionStatuses = questionStatusRestService.findQuestionStatusesByQuestionAndApplicationId(question.getId(), application.getId()).getSuccess();
                            boolean completedByLeadOrganisation = questionStatuses
                                    .stream()
                                    .anyMatch(questionStatus -> Objects.equals(questionStatus.getMarkedAsCompleteByOrganisationId(), application.getLeadOrganisationId()) &&
                                            questionStatus.getMarkedAsComplete() != null && questionStatus.getMarkedAsComplete());
                            boolean completeByAll = questionStatuses.stream()
                                    .allMatch(questionStatus -> questionStatus.getMarkedAsComplete() != null && questionStatus.getMarkedAsComplete());
                            return completedByLeadOrganisation && (!completeByAll);
                        }
                );
    }
}
