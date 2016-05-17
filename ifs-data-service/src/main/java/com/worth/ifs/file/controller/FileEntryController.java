package com.worth.ifs.file.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.transactional.FileEntryService;
import com.worth.ifs.file.transactional.FileService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

import static com.worth.ifs.commons.error.CommonErrors.*;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static com.worth.ifs.util.ParsingFunctions.validLong;

@RestController
@RequestMapping("/fileentry")
public class FileEntryController {
    private static final Log LOG = LogFactory.getLog(FileEntryController.class);

    @Value("${ifs.data.service.file.storage.fileinputresponse.max.filesize.bytes}")
    private Long maxFilesizeBytes;

    @Value("${ifs.data.service.file.storage.fileinputresponse.valid.media.types}")
    private List<String> validMediaTypes;

    @Autowired
    private FileEntryService fileEntryService;

    @Autowired
    private FileService fileService;

    @RequestMapping("/{id}")
    public RestResult<FileEntryResource> findById(@PathVariable("id") final Long id) {
        return fileEntryService.findOne(id).toGetResponse();
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

    private ServiceResult<Long> validContentLengthHeader(String contentLengthHeader) {

        return validLong(contentLengthHeader).map(ServiceResult::serviceSuccess).
                orElseGet(() -> serviceFailure(lengthRequiredError(maxFilesizeBytes)));
    }

    private ServiceResult<String> validContentTypeHeader(String contentTypeHeader) {
        return !StringUtils.isBlank(contentTypeHeader) ? serviceSuccess(contentTypeHeader) : serviceFailure(unsupportedMediaTypeByNameError(validMediaTypes));
    }

    private ServiceResult<Long> validContentLength(long length) {
        if (length > maxFilesizeBytes) {
            return serviceFailure(payloadTooLargeError(maxFilesizeBytes));
        }
        return serviceSuccess(length);
    }

    private ServiceResult<String> validFilename(String filename) {
        return checkParameterIsPresent(filename, "Please supply an original filename as a \"filename\" HTTP Request Parameter");
    }

    private ServiceResult<MediaType> validMediaType(String contentType) {
        if (!validMediaTypes.contains(contentType)) {
            return serviceFailure(unsupportedMediaTypeByNameError(validMediaTypes));
        }
        return serviceSuccess(MediaType.valueOf(contentType));
    }

    private ServiceResult<String> checkParameterIsPresent(String parameterValue, String failureMessage) {
        return !StringUtils.isBlank(parameterValue) ? serviceSuccess(parameterValue) : serviceFailure(badRequestError(failureMessage));
    }

    void setMaxFilesizeBytes(Long maxFilesizeBytes) {
        this.maxFilesizeBytes = maxFilesizeBytes;
    }

    void setValidMediaTypes(List<String> validMediaTypes) {
        this.validMediaTypes = validMediaTypes;
    }
}