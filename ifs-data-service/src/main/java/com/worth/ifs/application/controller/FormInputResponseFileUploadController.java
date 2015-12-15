package com.worth.ifs.application.controller;

import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.application.transactional.ApplicationService;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.resource.FileEntryResourceAssembler;
import com.worth.ifs.transactional.ServiceFailure;
import com.worth.ifs.transactional.ServiceSuccess;
import com.worth.ifs.util.Either;
import com.worth.ifs.util.JsonStatusResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

import static com.worth.ifs.application.resource.FormInputResponseFileEntryJsonStatusResponse.fileEntryCreated;
import static com.worth.ifs.util.Either.*;
import static com.worth.ifs.util.JsonStatusResponse.*;
import static com.worth.ifs.util.ParsingFunctions.validLong;
import static java.util.stream.Collectors.joining;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 *
 */
@RestController
@RequestMapping("/forminputresponse")
public class FormInputResponseFileUploadController {

    private static final Log LOG = LogFactory.getLog(FormInputResponseFileUploadController.class);

    @Value("${ifs.data.service.file.storage.fileinputresponse.max.filesize.bytes}")
    private Long maxFilesizeBytes;

    @Value("${ifs.data.service.file.storage.fileinputresponse.valid.mime.types}")
    private List<String> validMimeTypes;

    @Autowired
    private ApplicationService applicationService;

    @RequestMapping(value = "/{formInputResponseId}/fileupload", method = POST, produces = "application/json")
    public JsonStatusResponse createFile(
            @RequestHeader("Content-Type") String contentType,
            @RequestHeader("Content-Length") String contentLength,
            @PathVariable("formInputResponseId") long formInputResponseId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        Either<JsonStatusResponse, JsonStatusResponse> result =
            validContentLengthHeader(contentLength, response).map(length ->
            validContentTypeHeader(contentType, response).map(type ->
            validFilename(originalFilename, response).map(filename ->
            validContentLength(length, response).map(l ->
            validMimeType(type, response).map(mimeType ->
            createFormInputResponseFile(mimeType, length, originalFilename, formInputResponseId, request, response).map(fileEntry ->
            returnFileEntryId(fileEntry, formInputResponseId)
        ))))));

        return getLeftOrRight(result);
    }

    private Either<JsonStatusResponse, JsonStatusResponse> returnFileEntryId(FileEntry fileEntry, long formInputResponseId) {
        FileEntryResource fileEntryResource = FileEntryResourceAssembler.valueOf(fileEntry);
        FormInputResponseFileEntryResource formInputResponseFile = new FormInputResponseFileEntryResource(fileEntryResource, formInputResponseId);
        return right(fileEntryCreated(formInputResponseFile));
    }

    private Either<JsonStatusResponse, FileEntry> createFormInputResponseFile(MimeType mimeType, long length, String originalFilename, long formInputResponseId, HttpServletRequest request, HttpServletResponse response) {

        LOG.debug("Creating file with filename - " + originalFilename + "; Content Type - " + mimeType + "; Content Length - " + length);

        FileEntryResource fileEntry = new FileEntryResource(null, originalFilename, mimeType, length);
        FormInputResponseFileEntryResource formInputResponseFile = new FormInputResponseFileEntryResource(fileEntry, formInputResponseId);
        Either<ServiceFailure, ServiceSuccess<Pair<File, FileEntry>>> creationResult = applicationService.createFormInputResponseFileUpload(formInputResponseFile, inputStreamSupplier(request));

        return creationResult.mapLeftOrRight(
                failure -> left(internalServerError("Error creating file", response)),
                success -> right(success.getResult().getValue()));
    }

    private Supplier<InputStream> inputStreamSupplier(HttpServletRequest request) {
        return () -> {
            try {
                return request.getInputStream();
            } catch (IOException e) {
                LOG.error("Unable to open an input stream from request", e);
                throw new RuntimeException("Unable to open an input stream from request", e);
            }
        };
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
