package org.innovateuk.ifs.application.workflow.actions;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationEvent;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.transactional.SectionStatusService;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.transactional.SectionService;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.form.resource.SectionType.TERMS_AND_CONDITIONS;

/**
 * Auto mark-as-complete terms and conditions for EOI competitions
 */
@Component
public class AutoCompleteSectionsAction extends BaseApplicationAction {

    private final SectionService sectionService;
    private final SectionStatusService sectionStatusService;

    public AutoCompleteSectionsAction(SectionService sectionService, SectionStatusService sectionStatusService) {
        this.sectionService = sectionService;
        this.sectionStatusService = sectionStatusService;
    }

    @Override
    protected void doExecute(final Application application,
                             final StateContext<ApplicationState, ApplicationEvent> context) {
        Competition competition = application.getCompetition();
        if (competition.getCompetitionType().getCompetitionTypeEnum() == CompetitionTypeEnum.EXPRESSION_OF_INTEREST) {
            long termsSectionId = sectionService.getSectionsByCompetitionIdAndType(competition.getId(), TERMS_AND_CONDITIONS)
                    .getSuccess()
                    .stream()
                    .findFirst()
                    .map(SectionResource::getId)
                    .get();
            completeTermsAndConditions(application, termsSectionId);
        }
    }

    private void completeTermsAndConditions(Application application, long termsSectionId) {
        sectionStatusService.markSectionAsComplete(termsSectionId, application.getId(), application.getLeadApplicantProcessRole().getId());
    }
}