package com.worth.ifs.user.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.domain.CompAdminEmail;

public interface CompAdminEmailService {
    RestResult<CompAdminEmail> findByEmail(final String email);
}
