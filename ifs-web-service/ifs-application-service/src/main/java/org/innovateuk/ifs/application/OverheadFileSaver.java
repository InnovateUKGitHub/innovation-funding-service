package org.innovateuk.ifs.application;

import org.innovateuk.ifs.application.service.OverheadFileRestService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

/**
 * A saver class intended to use for adding / removing files attached to overhead finance row
 */

@Component
public class OverheadFileSaver {

    @Autowired
    OverheadFileRestService overheadFileRestService;

    private ValidationMessages uploadOverheadFile(HttpServletRequest request) {
        ValidationMessages messages = new ValidationMessages();

        final Map<String, MultipartFile> fileMap = ((StandardMultipartHttpServletRequest) request).getFileMap();
        final MultipartFile file = fileMap.get("overheadfile");
        Long overheadId = Long.valueOf(request.getParameter("fileoverheadid"));
        RestResult<FileEntryResource> fileEntryResult = RestResult.restFailure(new Error("", HttpStatus.BAD_REQUEST));
        try {
            fileEntryResult = overheadFileRestService.updateOverheadCalculationFile(overheadId, file.getContentType(), file.getSize(), file.getOriginalFilename(), file.getBytes());
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        if(fileEntryResult.isFailure()) {
            if(fileEntryResult.getErrors().stream().anyMatch(error -> error.getErrorKey().equals("UNSUPPORTED_MEDIA_TYPE"))) {
                Error error = new Error("validation.finance.overhead.file.type",UNSUPPORTED_MEDIA_TYPE);
                messages.addError(error);
            }
            else {
                messages.addAll(fileEntryResult);
            }
        }

        return messages;
    }

    private ValidationMessages deleteOverheadFile(HttpServletRequest request) {
        ValidationMessages messages = new ValidationMessages();
        Long overheadId = Long.valueOf(request.getParameter("fileoverheadid"));

        RestResult<Void> fileEntryResult = overheadFileRestService.removeOverheadCalculationFile(overheadId);

        if(fileEntryResult.isFailure()) {
            messages.addAll(fileEntryResult);
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
        return request instanceof StandardMultipartHttpServletRequest && request.getParameter("overheadfilesubmit") != null;
    }

    private boolean isOverheadFileDeleteRequest(HttpServletRequest request) {
        return request instanceof StandardMultipartHttpServletRequest && request.getParameter("overheadfiledelete") != null;
    }
}
