package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.User;

/**
 * Service for sending survey messages to Users.
 */
public interface UserSurveyService {

    @NotSecured(value = "This Service is to be used within other secured services", mustBeSecuredByOtherServices = true)
    ServiceResult<Void> sendDiversitySurvey(User user);
}