package org.innovateuk.ifs.management.notification.populator;

import org.innovateuk.ifs.application.resource.Decision;
import org.innovateuk.ifs.application.resource.ApplicationDecisionToSendApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationDecisionRestService;
import org.innovateuk.ifs.application.service.ApplicationNotificationTemplateRestService;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionAssessmentConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.decision.form.NotificationEmailsForm;
import org.innovateuk.ifs.management.notification.viewmodel.SendNotificationsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SendNotificationsModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ApplicationNotificationTemplateRestService applicationNotificationTemplateRestService;

    @Autowired
    private CompetitionAssessmentConfigRestService competitionAssessmentConfigRestService;

    @Autowired
    private ApplicationDecisionRestService applicationDecisionRestService;


    public SendNotificationsViewModel populate(long competitionId, List<Long> applicationIds, NotificationEmailsForm form) {
        List<ApplicationDecisionToSendApplicationResource> filteredApplications = applicationDecisionRestService.getNotificationResourceForApplications(applicationIds).getSuccess();

        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();
        CompetitionAssessmentConfigResource competitionAssessmentConfigResource = competitionAssessmentConfigRestService.findOneByCompetitionId(competitionId).getSuccess();

        long successfulCount = getApplicationCountByDecision(filteredApplications, Decision.FUNDED);
        long unsuccessfulCount = getApplicationCountByDecision(filteredApplications, Decision.UNFUNDED);
        long onHoldCount = getApplicationCountByDecision(filteredApplications, Decision.ON_HOLD);

        if (form.getMessage() == null) {
            tryToPrePopulateMessage(competitionResource, successfulCount, unsuccessfulCount, onHoldCount, form);
        }

        return new SendNotificationsViewModel(filteredApplications,
                                              successfulCount,
                                              unsuccessfulCount,
                                              onHoldCount,
                                              competitionResource,
                                              Boolean.TRUE.equals(competitionAssessmentConfigResource.getIncludeAverageAssessorScoreInNotifications()),
                                              competitionResource.isHorizonEuropeGuarantee());
    }

    private long getApplicationCountByDecision(List<ApplicationDecisionToSendApplicationResource> filteredApplications, Decision decision) {
        return filteredApplications.stream()
                .filter(application -> application.getDecision() == decision)
                .count();
    }

    private void tryToPrePopulateMessage(CompetitionResource competition, long successfulCount, long unsuccessfulCount, long onHoldCount, NotificationEmailsForm form) {
        if (onlySuccessfulEmails(successfulCount, unsuccessfulCount, onHoldCount)) {
            form.setMessage(applicationNotificationTemplateRestService.getSuccessfulNotificationTemplate(competition.getId()).getSuccess().getMessageBody());
        } else if (onlyUnsuccessfulEmails(successfulCount, unsuccessfulCount, onHoldCount)) {
            form.setMessage(applicationNotificationTemplateRestService.getUnsuccessfulNotificationTemplate(competition.getId()).getSuccess().getMessageBody());
        }
    }

    private boolean onlyUnsuccessfulEmails(long successfulCount, long unsuccessfulCount, long onHoldCount) {
        return unsuccessfulCount > 0 && successfulCount == 0 && onHoldCount == 0;
    }

    private boolean onlySuccessfulEmails(long successfulCount, long unsuccessfulCount, long onHoldCount) {
        return successfulCount > 0 && unsuccessfulCount == 0 && onHoldCount == 0;
    }
}
