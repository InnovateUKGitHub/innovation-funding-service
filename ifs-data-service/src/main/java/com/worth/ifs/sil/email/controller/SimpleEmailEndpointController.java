package com.worth.ifs.sil.email.controller;

import com.worth.ifs.sil.email.resource.SilEmailAddress;
import com.worth.ifs.sil.email.resource.SilEmailMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

import static com.worth.ifs.util.CollectionFunctions.*;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * A simple endpoint to allow stubbing of the SIL outbound email endpoint for non-integration test environments
 */
@RestController
@RequestMapping("/silstub")
public class SimpleEmailEndpointController {

    private static final Log LOG = LogFactory.getLog(SimpleEmailEndpointController.class);

    @RequestMapping(value="/sendmail", method = POST)
    public void sendMail(@RequestBody SilEmailMessage message, HttpServletResponse response) {

        LOG.info("Stubbing out SIL outbound email:\n\n" +
                "From: " + message.getFrom().getEmail() + "\n" +
                "To: " + simpleJoiner(simpleMap(message.getTo(), SilEmailAddress::getEmail), ", ") + "\n" +
                "Subject: " + message.getSubject() + "\n" +
                "Plain text body: " +  simpleFilter(message.getBody(), body -> body.getContentType().equals("text/plain")).get(0).getContent() + "\n" +
                "HTML body: " + simpleFilter(message.getBody(), body -> body.getContentType().equals("text/html")).get(0).getContent());

        response.setStatus(HttpServletResponse.SC_ACCEPTED);
    }
}
