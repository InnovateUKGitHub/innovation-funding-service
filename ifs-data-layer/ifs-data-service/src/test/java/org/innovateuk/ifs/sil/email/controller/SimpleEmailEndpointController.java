package org.innovateuk.ifs.sil.email.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.sil.email.resource.SilEmailAddress;
import org.innovateuk.ifs.sil.email.resource.SilEmailBody;
import org.innovateuk.ifs.sil.email.resource.SilEmailMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.springframework.http.HttpStatus.ACCEPTED;

/**
 * A simple endpoint to allow stubbing of the SIL outbound email endpoint for integration tests
 */
@RestController
@RequestMapping("/silstub/sendmail")
@Profile({"integration-test"})
public class SimpleEmailEndpointController {

    @PostMapping
    public RestResult<Void> sendMail(@RequestBody SilEmailMessage message) {
        return restSuccess(ACCEPTED);
    }
}
