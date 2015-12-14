package com.worth.ifs.file.controller;

import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.service.FileService;
import com.worth.ifs.util.Either;
import com.worth.ifs.util.JsonStatusResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.worth.ifs.util.Either.*;
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

    @Value("${ifs.data.service.file.storage.fileinputresponse.max.filesize.bytes}")
    private Long maxFilesizeBytes;

    @Value("${ifs.data.service.file.storage.fileinputresponse.valid.mime.types}")
    private List<String> validMimeTypes;

    @Autowired
    private FileService fileService;

    @RequestMapping(method = POST, produces = "application/json")
    public JsonStatusResponse createFile(
            @RequestHeader("Content-Type") String contentType,
            @RequestHeader("Content-Length") String contentLength,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        Either<JsonStatusResponse, JsonStatusResponse> result =
            validContentLengthHeader(contentLength, response).map(length ->
            validContentTypeHeader(contentType, response).map(type ->
            validFilename(originalFilename, response).map(filename ->
            validContentLength(length, response).map(l ->
            validMimeType(type, response).map(mimeType ->
            doCreateFile(mimeType, length, originalFilename, request, response)
        )))));

        return getLeftOrRight(result);
    }

    private Either<JsonStatusResponse, JsonStatusResponse> doCreateFile(MimeType mimeType, long length, String originalFilename, HttpServletRequest request, HttpServletResponse response) {

        final String fileContents;

        try {
            fileContents = request.getReader().lines().collect(joining(lineSeparator()));
            FileEntryResource fileEntry = new FileEntryResource(null, originalFilename, mimeType, length);
            fileService.createFile(fileEntry);
        } catch (IOException e) {
            return left(internalServerError("Error reading request", response));
        }

        LOG.debug("Filename - " + originalFilename + "; Content Type - " + mimeType + "; Content Length - " + length + "; Contents - " + StringUtils.abbreviate(fileContents, 100));

        return right(ok());
    }

    private Either<JsonStatusResponse, Long> validContentLengthHeader(String contentLengthHeader, HttpServletResponse response) {

        return validLong(contentLengthHeader).map(length -> Either.<JsonStatusResponse, Long> right(length)).
                orElseGet(() -> left(lengthRequired("Please supply a valid Content-Length HTTP header.  Maximum " + maxFilesizeBytes, response)));
    }

    private Either<JsonStatusResponse, String> validContentTypeHeader(String contentTypeHeader, HttpServletResponse response) {
        return !StringUtils.isBlank(contentTypeHeader) ? right(contentTypeHeader) :
                left(unsupportedMediaType("Please supply a valid Content-Type HTTP header.  Valid types are " + validMimeTypes.stream().collect(joining(", ")), response));
    }

    private Either<JsonStatusResponse, Long> validContentLength(long length, HttpServletResponse response) {
        if (length > maxFilesizeBytes) {
            return left(requestEntityTooLarge("File upload was too large for FormInputResponse.  Max filesize in bytes is " + maxFilesizeBytes, response));
        }
        return right(length);
    }

    private Either<JsonStatusResponse, String> validFilename(String filename, HttpServletResponse response) {
        return checkParameterIsPresent(filename, "Please supply an original filename as a \"filename\" HTTP Request Parameter", response);
    }

    private Either<JsonStatusResponse, MimeType> validMimeType(String contentType, HttpServletResponse response) {
        if (!validMimeTypes.contains(contentType)) {
            return left(unsupportedMediaType("Please supply a valid Content-Type HTTP header.  Valid types are " + validMimeTypes.stream().collect(joining(", ")), response));
        }
        return right(MimeType.valueOf(contentType));
    }

    private Either<JsonStatusResponse, String> checkParameterIsPresent(String parameterValue, String failureMessage, HttpServletResponse response) {
        return !StringUtils.isBlank(parameterValue) ? right(parameterValue) : left(badRequest(failureMessage, response));
    }
}
