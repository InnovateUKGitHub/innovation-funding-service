package org.innovateuk.ifs.util.state;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_OPEN;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.OPEN;

/**
 * Class provides functions returning a ServiceResult depending on the {@Application} entity state.
 */
public final class ApplicationStateVerificationFunctions {

    private ApplicationStateVerificationFunctions() {}

    public static ServiceResult<Application> verifyApplicationIsOpen(Application application) {
        if (application.getCompetition() != null && !OPEN.equals(application.getCompetition().getCompetitionStatus())) {
            return serviceFailure(COMPETITION_NOT_OPEN);
        } else {
            return serviceSuccess(application);
        }
    }
}