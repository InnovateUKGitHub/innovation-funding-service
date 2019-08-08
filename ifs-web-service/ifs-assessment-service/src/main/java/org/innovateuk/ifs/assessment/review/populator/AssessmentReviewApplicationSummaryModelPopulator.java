package org.innovateuk.ifs.assessment.review.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.populator.ApplicationReadOnlyViewModelPopulator;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.assessment.review.viewmodel.AssessmentReviewApplicationSummaryViewModel;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Application under review view.
 */
@Component
public class AssessmentReviewApplicationSummaryModelPopulator {

    @Autowired
    private ApplicationReadOnlyViewModelPopulator summaryViewModelPopulator;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private AssessmentRestService assessmentRestService;

    public AssessmentReviewApplicationSummaryViewModel populateModel(UserResource user, long applicationId) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        ApplicationReadOnlyViewModel readOnlyViewModel = summaryViewModelPopulator.populate(application, competition, user, ApplicationReadOnlySettings.defaultSettings());
        return new AssessmentReviewApplicationSummaryViewModel(application.getId(),
                application.getName(),
                readOnlyViewModel,
                competition,assessmentRestService
                        .getByUserAndApplication(user.getId(), applicationId)
                        .getSuccess());
    }

}
