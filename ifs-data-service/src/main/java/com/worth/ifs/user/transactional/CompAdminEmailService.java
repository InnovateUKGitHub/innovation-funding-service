package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.user.domain.CompAdminEmail;

public interface CompAdminEmailService {
    @NotSecured("Need to keep open to all to allow login")
    ServiceResult<CompAdminEmail> getByEmail(final String String);
}
