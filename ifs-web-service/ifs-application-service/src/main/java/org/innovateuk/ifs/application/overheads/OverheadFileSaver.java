package org.innovateuk.ifs.application.overheads;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.service.OverheadFileRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

/**
 * A saver class intended to use for adding / removing files attached to overhead finance row
 */
@Slf4j
@Component
public class OverheadFileSaver {
    public static final String OVERHEAD_FILE_SUBMIT = "overheadfilesubmit";
    public static final String OVERHEAD_FILE_DELETE = "overheadfiledelete";
    public static final String OVERHEAD_FILE_ID = "fileoverheadid";
    public static final String OVERHEAD_FILE = "overheadfile";

    @Autowired
    private OverheadFileRestService overheadFileRestService;

    private ValidationMessages uploadOverheadFile(HttpServletRequest request) {
        ValidationMessages messages = new ValidationMessages();

        final Map<String, MultipartFile> fileMap = ((StandardMultipartHttpServletRequest) request).getFileMap();
        final MultipartFile file = fileMap.get(OVERHEAD_FILE);
        try {
            Long overheadId = Long.valueOf(request.getParameter("fileoverheadid"));
            RestResult<FileEntryResource> fileEntryResult = overheadFileRestService.updateOverheadCalculationFile(overheadId, file.getContentType(), file.getSize(), file.getOriginalFilename(), file.getBytes());

            handleRestResultUpload(fileEntryResult, messages);
        } catch(NumberFormatException | IOException e) {
            log.error("Overheadfile cannot be saved :"  + e.getMessage(), e);
        }

        return messages;
    }

    private void handleRestResultUpload(RestResult<FileEntryResource> fileEntryResult, ValidationMessages messages) {
        if(fileEntryResult.isFailure()) {
            fileEntryResult.getErrors().forEach(error -> {
                if(UNSUPPORTED_MEDIA_TYPE.name().equals(error.getErrorKey())) {
                    messages.addError(fieldError(OVERHEAD_FILE, new Error("validation.finance.overhead.file.type", UNSUPPORTED_MEDIA_TYPE)));
                } else {
                    messages.addError(fieldError(OVERHEAD_FILE, error));
                }
            });
        }
    }

    private ValidationMessages deleteOverheadFile(HttpServletRequest request) {
        ValidationMessages messages = new ValidationMessages();
        try {
            Long overheadId = Long.valueOf(request.getParameter(OVERHEAD_FILE_ID));

            RestResult<Void> fileEntryResult = overheadFileRestService.removeOverheadCalculationFile(overheadId);

            if (fileEntryResult.isFailure()) {
                messages.addAll(fileEntryResult);
            }
        } catch (NumberFormatException e) {
            log.error("Overheadfile cannot be deleted :"  + e.getMessage());
        }

        return messages;
    }

    public ValidationMessages handleOverheadFileRequest(HttpServletRequest request) {
        if(isOverheadFileUploadRequest(request)) {
            return uploadOverheadFile(request);
        }
        else if (isOverheadFileDeleteRequest(request)) {
            return deleteOverheadFile(request);
        }
        else {
            return new ValidationMessages();
        }
    }

    public boolean isOverheadFileRequest(HttpServletRequest request) {
        return isOverheadFileUploadRequest(request) || isOverheadFileDeleteRequest(request);
    }

    private boolean isOverheadFileUploadRequest(HttpServletRequest request) {
        return request instanceof MultipartHttpServletRequest && request.getParameter(OVERHEAD_FILE_SUBMIT) != null;
    }

    private boolean isOverheadFileDeleteRequest(HttpServletRequest request) {
        return request.getParameter(OVERHEAD_FILE_DELETE) != null;
    }
}
