package com.worth.ifs.application.transactional;

import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.security.NotSecured;

public interface ApplicationStatusService {
    @NotSecured("TODO")
    ApplicationStatus getById(Long id);
}
