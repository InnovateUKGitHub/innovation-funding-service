package com.worth.ifs.file.controller;

import com.worth.ifs.file.service.FileService;
import com.worth.ifs.util.JsonStatusResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.worth.ifs.util.JsonStatusResponse.*;
import static com.worth.ifs.util.ParsingFunctions.validLong;
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

    @Value("${ifs.data.service.file.storage.max.fileinputresponse.filesize.bytes}")
    private Long maxFilesizeBytes;

    @Autowired
    private FileService fileService;

    @RequestMapping(method = POST, produces = "application/json")
    public JsonStatusResponse createFile(
            @RequestHeader("Content-Type") String contentType,
            @RequestHeader("Content-Length") String contentLengthHeader,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        return validLong(contentLengthHeader).map(length -> {

            if (length > maxFilesizeBytes) {
                return requestEntityTooLarge("File upload was too large for FormInputResponse.  Max filesize in bytes is " + maxFilesizeBytes, response);
            }

            String fileContents = null;
            try {
                fileContents = request.getReader().lines().collect(joining(lineSeparator()));
            } catch (IOException e) {
                return internalServerError("Error reading request", response);
            }

            LOG.debug("Content Type - " + contentType + "; Content Length - " + contentLengthHeader + "; " + fileContents);

            return ok();

        }).orElseGet(() -> lengthRequired("Please supply a valid Content-Length HTTP header", response));
    }
}
