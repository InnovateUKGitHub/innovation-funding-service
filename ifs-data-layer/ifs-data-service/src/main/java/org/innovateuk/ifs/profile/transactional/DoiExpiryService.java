package org.innovateuk.ifs.profile.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;

public interface DoiExpiryService {

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    void notifyExpiredDoi();
}
