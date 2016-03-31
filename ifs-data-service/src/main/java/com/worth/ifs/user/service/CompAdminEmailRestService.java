package com.worth.ifs.user.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.domain.CompAdminEmail;

public interface CompAdminEmailRestService {
    RestResult<CompAdminEmail> findByEmail(final String email);
}
