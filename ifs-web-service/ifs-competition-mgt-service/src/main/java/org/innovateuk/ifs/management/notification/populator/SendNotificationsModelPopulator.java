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
        return populate(competitionId, applicationIds, form, false);
    }

    public SendNotificationsViewModel populate(long competitionId, List<Long> applicationIds, NotificationEmailsForm form, boolean eoi) {
        List<ApplicationDecisionToSendApplicationResource> filteredApplications = applicationDecisionRestService.getNotificationResourceForApplications(applicationIds).getSuccess();

        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();
        CompetitionAssessmentConfigResource competitionAssessmentConfigResource = competitionAssessmentConfigRestService.findOneByCompetitionId(competitionId).getSuccess();

        if (eoi) {
            return eoiSendNotificationsViewModel(form, filteredApplications, competitionResource);
        } else {
            return sendNotificationsViewModel(form, filteredApplications, competitionResource, competitionAssessmentConfigResource);
        }
    }

    private SendNotificationsViewModel sendNotificationsViewModel(NotificationEmailsForm form, List<ApplicationDecisionToSendApplicationResource> filteredApplications, CompetitionResource competitionResource, CompetitionAssessmentConfigResource competitionAssessmentConfigResource) {
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

    private SendNotificationsViewModel eoiSendNotificationsViewModel(NotificationEmailsForm form, List<ApplicationDecisionToSendApplicationResource> filteredApplications, CompetitionResource competitionResource) {
        long eoiApprovedCount = getApplicationCountByDecision(filteredApplications, Decision.EOI_APPROVED);
        long eoiRejectedCount = getApplicationCountByDecision(filteredApplications, Decision.EOI_REJECTED);

        if (form.getMessage() == null) {
            tryToPrePopulateEoiMessage(competitionResource, eoiApprovedCount, eoiRejectedCount, form);
        }

        return new SendNotificationsViewModel(filteredApplications, competitionResource, true);
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

    private void tryToPrePopulateEoiMessage(CompetitionResource competition, long eoiApprovedCount, long eoiRejectedCount, NotificationEmailsForm form) {
        if (onlyEoiApprovedEmails(eoiApprovedCount, eoiRejectedCount)) {
            form.setMessage(applicationNotificationTemplateRestService.getEoiApprovedNotificationTemplate(competition.getId()).getSuccess().getMessageBody());
        } else if (onlyEoiRejectedEmails(eoiApprovedCount, eoiRejectedCount)) {
            form.setMessage(applicationNotificationTemplateRestService.getEoiRejectedNotificationTemplate(competition.getId()).getSuccess().getMessageBody());
        }
    }

    private boolean onlyEoiRejectedEmails(long eoiApprovedCount, long eoiRejectedCount) {
        return eoiRejectedCount > 0 && eoiApprovedCount == 0;
    }

    private boolean onlyEoiApprovedEmails(long eoiApprovedCount, long eoiRejectedCount) {
        return eoiApprovedCount > 0 && eoiRejectedCount == 0;
    }
}
