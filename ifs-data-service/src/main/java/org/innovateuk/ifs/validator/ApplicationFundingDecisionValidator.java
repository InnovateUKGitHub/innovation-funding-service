package org.innovateuk.ifs.validator;


import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FundingDecisionStatus;
import org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl;
import org.springframework.stereotype.Component;

@Component
public class ApplicationFundingDecisionValidator {
    public boolean isValid(Application application) {

        if(!hasBeenSubmitted(application)) {
            return false;
        }
        else if(decisionIsSuccessful(application)
                && decisionNotificationWasSent(application)) {
            return false;
        }

        return true;
    }

    private boolean hasBeenSubmitted(Application application) {
        boolean hasBeenSubmitted = ApplicationSummaryServiceImpl.SUBMITTED_STATUSES.contains(application.getApplicationProcess().getActivityState());

        return hasBeenSubmitted;
    }

    private boolean decisionIsSuccessful(Application application) {
        return application.getFundingDecision() != null && application.getFundingDecision().equals(FundingDecisionStatus.FUNDED);
    }

    private boolean decisionNotificationWasSent(Application application) {
        return application.getManageFundingEmailDate() != null;
    }
}
