package org.innovateuk.ifs.assessment.review.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.populator.ApplicationReadOnlyViewModelPopulator;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.review.viewmodel.AssessmentReviewApplicationSummaryViewModel;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Build the model for the Application under review view.
 */
@Component
public class AssessmentReviewApplicationSummaryModelPopulator {

    private static final String TERMS_AND_CONDITIONS_INVESTOR_PARTNERSHIPS = "Investor Partnerships terms and conditions";
    private static final String TERMS_AND_CONDITIONS_OTHER = "Award terms and conditions";

    @Autowired
    private ApplicationReadOnlyViewModelPopulator applicationReadOnlyViewModelPopulator;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private AssessmentRestService assessmentRestService;

    public AssessmentReviewApplicationSummaryViewModel populateModel(UserResource user, long applicationId) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        Optional<AssessmentResource> assessment = assessmentRestService
                .getByUserAndApplication(user.getId(), applicationId)
                .getSuccess().stream().findFirst();
        ApplicationReadOnlySettings settings = ApplicationReadOnlySettings.defaultSettings();
        if (assessment.isPresent()) {
            settings.setAssessmentId(assessment.get().getId());
        }
        ApplicationReadOnlyViewModel readOnlyViewModel = applicationReadOnlyViewModelPopulator.populate(application, competition, user, settings);
        return new AssessmentReviewApplicationSummaryViewModel(application.getId(),
                application.getName(),
                readOnlyViewModel,
                competition,
                assessment.orElse(null),
                termsAndConditionsTerminology(competition));
    }

    private String termsAndConditionsTerminology(CompetitionResource competitionResource) {
        if(FundingType.INVESTOR_PARTNERSHIPS == competitionResource.getFundingType()) {
            return TERMS_AND_CONDITIONS_INVESTOR_PARTNERSHIPS;
        }
        return TERMS_AND_CONDITIONS_OTHER;
    }

}
