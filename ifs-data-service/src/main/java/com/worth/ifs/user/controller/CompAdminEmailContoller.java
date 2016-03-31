package com.worth.ifs.user.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.domain.CompAdminEmail;
import com.worth.ifs.user.transactional.CompAdminEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/compadminemail")
public class CompAdminEmailContoller {

    @Autowired
    private CompAdminEmailService compAdminEmailService;

    @RequestMapping("/email/{email:.+}")
    public RestResult<CompAdminEmail> getByEmail(@PathVariable("email") final String email){
        final String decodedEmail = decode(email);
        return compAdminEmailService.getByEmail(decodedEmail).toGetResponse();
    }

    private String decode(final String input){
        String output;
        try {
            output = UriUtils.decode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            output = input;
        }
        return output;
    }

}
