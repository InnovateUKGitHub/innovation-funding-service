package com.worth.ifs.user.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.domain.CompAdminEmail;
import com.worth.ifs.user.transactional.CompAdminEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/compadminemail")
public class CompAdminEmailContoller {

    @Autowired
    private CompAdminEmailService compAdminEmailService;

    @RequestMapping("/email/{email}")
    public RestResult<CompAdminEmail> getByEmail(@PathVariable final String email){
        return compAdminEmailService.getByEmail(email).toGetResponse();
    }

}
