package com.worth.ifs.user.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.user.domain.CompAdminEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.rest.RestResult.restFailure;

@Service
public class CompAdminEmailRestServiceImpl extends BaseRestService implements CompAdminEmailRestService {

    private String compAdminEmailRestURL;

    @Value("${ifs.data.service.rest.compadminemail}")
    void setCompAdminEmailRestURL(String compAdminEmailRestURL) {
        this.compAdminEmailRestURL= compAdminEmailRestURL;
    }

    @Override
    public RestResult<CompAdminEmail> findByEmail(String email) {
        if(StringUtils.isEmpty(email))
            return restFailure(notFoundError(CompAdminEmail.class, email));

        final String encodedEmail = encode(email);

        return getWithRestResult(compAdminEmailRestURL + "/email/" + encodedEmail, CompAdminEmail.class);
    }

    private String encode(String input){
        String output;
        try {
            output = UriUtils.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            output = input;
        }
        return output;
    }
}
