package org.innovateuk.ifs.validator;


import org.innovateuk.ifs.application.constant.ApplicationStatusConstants;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FundingDecisionStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
        List<Long> submittedApplicationStatusIds = new ArrayList<>();
        submittedApplicationStatusIds.add(ApplicationStatusConstants.SUBMITTED.getId());
        submittedApplicationStatusIds.add(ApplicationStatusConstants.REJECTED.getId());
        submittedApplicationStatusIds.add(ApplicationStatusConstants.APPROVED.getId());

        boolean hasBeenSubmitted = submittedApplicationStatusIds.contains(application.getApplicationStatus().getId());;

        return hasBeenSubmitted;
    }

    private boolean decisionIsSuccessful(Application application) {
        return application.getFundingDecision() != null && application.getFundingDecision().equals(FundingDecisionStatus.FUNDED);
    }

    private boolean decisionNotificationWasSent(Application application) {
        return application.getManageFundingEmailDate() != null;
    }
}
