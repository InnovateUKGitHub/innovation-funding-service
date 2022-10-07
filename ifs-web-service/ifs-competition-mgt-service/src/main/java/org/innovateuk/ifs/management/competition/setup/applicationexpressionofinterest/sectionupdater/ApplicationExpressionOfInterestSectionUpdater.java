package org.innovateuk.ifs.management.competition.setup.applicationexpressionofinterest.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.management.competition.setup.application.sectionupdater.AbstractSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.applicationexpressionofinterest.form.ApplicationExpressionOfInterestForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupSectionUpdater;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class ApplicationExpressionOfInterestSectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSectionUpdater {

    @Value("${ifs.assessment.stage.competition.enabled}")
    private boolean isAssessmentStageEnabled;

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @Override
    public CompetitionSetupSection sectionToSave() {
        return CompetitionSetupSection.APPLICATION_EXPRESSION_OF_INTEREST;
    }

    @Override
    protected ServiceResult<Void> doSaveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm, UserResource loggedInUser) {
        ApplicationExpressionOfInterestForm form = (ApplicationExpressionOfInterestForm) competitionSetupForm;
        competition.setEnabledForPreRegistration(form.getExpressionOfInterest());
        return competitionSetupRestService.update(competition).toServiceResult();
    }

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
        return ApplicationExpressionOfInterestForm.class.equals(clazz);
    }

    @Override
    public String getNextSection(CompetitionSetupForm competitionSetupForm, CompetitionResource competition, CompetitionSetupSection section) {

        String sectionPath;

        if (competition.isAlwaysOpen() && isAssessmentStageEnabled) {
            sectionPath = CompetitionSetupSection.APPLICATION_ASSESSMENT.getPath();
        } else {
            sectionPath = CompetitionSetupSection.MILESTONES.getPath();
        }

        return format("redirect:/competition/setup/%d/section/%s", competition.getId(), sectionPath);
    }
}
