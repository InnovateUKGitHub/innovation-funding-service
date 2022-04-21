package org.innovateuk.ifs.management.competition.setup.projecteligibility.sectionupdater;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.management.competition.setup.application.sectionupdater.AbstractSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.sectionupdater.CompetitionSetupSectionUpdater;
import org.innovateuk.ifs.management.competition.setup.core.util.CompetitionUtils;
import org.innovateuk.ifs.management.competition.setup.projecteligibility.form.ProjectEligibilityForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.PROJECT_ELIGIBILITY;

/**
 * Competition setup section saver for the eligibility section.
 */
@Service
public class ProjectEligibilitySectionUpdater extends AbstractSectionUpdater implements CompetitionSetupSectionUpdater {

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @Override
    public CompetitionSetupSection sectionToSave() {
        return PROJECT_ELIGIBILITY;
    }

    private static final int NON_FINANCE_RESEARCH_PARTICIPATION_DEFAULT_PERCENTAGE = 0;

    @Override
    protected ServiceResult<Void> doSaveSection(
            CompetitionResource competition,
            CompetitionSetupForm competitionSetupForm,
            UserResource loggedInUser
    ) {
        ProjectEligibilityForm projectEligibilityForm = (ProjectEligibilityForm) competitionSetupForm;

        if (competition.isNonFinanceType()) {
            competition.setMaxResearchRatio(NON_FINANCE_RESEARCH_PARTICIPATION_DEFAULT_PERCENTAGE);
        } else {
            competition.setMaxResearchRatio(projectEligibilityForm.getResearchParticipationPercentage());
        }

        boolean multiStream = "yes".equals(projectEligibilityForm.getMultipleStream());
        competition.setMultiStream(multiStream);

        if (multiStream) {
            competition.setStreamName(projectEligibilityForm.getStreamName());
        } else {
            competition.setStreamName(null);
        }

        competition.setResubmission(CompetitionUtils.textToBoolean(projectEligibilityForm.getResubmission()));

        CollaborationLevel level = CollaborationLevel.fromCode(projectEligibilityForm.getSingleOrCollaborative());
        competition.setCollaborationLevel(level);
        competition.setLeadApplicantTypes(projectEligibilityForm.getLeadApplicantTypes());


        return competitionSetupRestService.update(competition).toServiceResult();
    }

    @Override
    public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
        return ProjectEligibilityForm.class.equals(clazz);
    }

}
