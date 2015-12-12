package com.worth.ifs.file.controller;

import com.worth.ifs.file.service.FileService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 *
 */
@RestController
@RequestMapping("/forminputresponse/fileupload")
public class FormInputResponseFileUploadController {

    private static final Log LOG = LogFactory.getLog(FormInputResponseFileUploadController.class);

    @Autowired
    private FileService fileService;

    @RequestMapping(method = POST)
    public void createFile(
            @RequestHeader("Content-Type") String contentType,
            @RequestHeader("Content-Length") String contentLength,
            HttpServletRequest request) throws IOException {

        String fileContents = request.getReader().lines().collect(joining(lineSeparator()));

        LOG.debug("Content Type - " + contentType + "; Content Length - " + contentLength + "; " + fileContents);
    }
}
