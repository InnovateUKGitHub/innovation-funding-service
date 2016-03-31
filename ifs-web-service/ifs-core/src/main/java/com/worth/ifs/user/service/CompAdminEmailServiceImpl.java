package com.worth.ifs.user.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.domain.CompAdminEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompAdminEmailServiceImpl implements CompAdminEmailService {

    @Autowired
    private CompAdminEmailRestService compAdminEmailRestService;

    @Override
    public RestResult<CompAdminEmail> findByEmail(String email) {
        return compAdminEmailRestService.findByEmail(email);
    }
}
