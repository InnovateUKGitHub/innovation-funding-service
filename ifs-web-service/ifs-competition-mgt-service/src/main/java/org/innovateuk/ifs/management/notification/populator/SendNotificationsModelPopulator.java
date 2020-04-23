package org.innovateuk.ifs.management.notification.populator;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingDecisionToSendApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationFundingDecisionRestService;
import org.innovateuk.ifs.application.service.ApplicationNotificationTemplateRestService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionAssessmentConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.funding.form.NotificationEmailsForm;
import org.innovateuk.ifs.management.notification.viewmodel.SendNotificationsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SendNotificationsModelPopulator {

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ApplicationNotificationTemplateRestService applicationNotificationTemplateRestService;

    @Autowired
    private CompetitionAssessmentConfigRestService competitionAssessmentConfigRestService;

    @Autowired
    private ApplicationFundingDecisionRestService applicationFundingDecisionRestService;


    public SendNotificationsViewModel populate(long competitionId, List<Long> applicationIds, NotificationEmailsForm form) {
        List<FundingDecisionToSendApplicationResource> filteredApplications = applicationFundingDecisionRestService.getNotificationResourceForApplications(applicationIds).getSuccess();

        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();
        CompetitionAssessmentConfigResource competitionAssessmentConfigResource = competitionAssessmentConfigRestService.findOneByCompetitionId(competitionId).getSuccess();

        long successfulCount = getApplicationCountByFundingDecision(filteredApplications, FundingDecision.FUNDED);
        long unsuccessfulCount = getApplicationCountByFundingDecision(filteredApplications, FundingDecision.UNFUNDED);
        long onHoldCount = getApplicationCountByFundingDecision(filteredApplications, FundingDecision.ON_HOLD);

        if (form.getMessage() == null) {
            tryToPrePopulateMessage(competitionId, successfulCount, unsuccessfulCount, onHoldCount, form);
        }

        return new SendNotificationsViewModel(filteredApplications,
                                              successfulCount,
                                              unsuccessfulCount,
                                              onHoldCount,
                                              competitionId,
                                              competitionResource.getName(),
                                              competitionResource.isH2020(),
                                              Boolean.TRUE.equals(competitionAssessmentConfigResource.getIncludeAverageAssessorScoreInNotifications()));
    }


    private long getApplicationCountByFundingDecision(List<FundingDecisionToSendApplicationResource> filteredApplications, FundingDecision fundingDecision) {
        return filteredApplications.stream()
                .filter(application -> application.getFundingDecision() == fundingDecision)
                .count();
    }

    private void tryToPrePopulateMessage(long competitionId, long successfulCount, long unsuccessfulCount, long onHoldCount, NotificationEmailsForm form) {
        if (onlySuccessfulEmails(successfulCount, unsuccessfulCount, onHoldCount)) {
            form.setMessage(applicationNotificationTemplateRestService.getSuccessfulNotificationTemplate(competitionId).getSuccess().getMessageBody());
        } else if (onlyUnsuccessfulEmails(successfulCount, unsuccessfulCount, onHoldCount)) {
            form.setMessage(applicationNotificationTemplateRestService.getUnsuccessfulNotificationTemplate(competitionId).getSuccess().getMessageBody());
        }
    }

    private boolean onlyUnsuccessfulEmails(long successfulCount, long unsuccessfulCount, long onHoldCount) {
        return unsuccessfulCount > 0 && successfulCount == 0 && onHoldCount == 0;
    }

    private boolean onlySuccessfulEmails(long successfulCount, long unsuccessfulCount, long onHoldCount) {
        return successfulCount > 0 && unsuccessfulCount == 0 && onHoldCount == 0;
    }


}
