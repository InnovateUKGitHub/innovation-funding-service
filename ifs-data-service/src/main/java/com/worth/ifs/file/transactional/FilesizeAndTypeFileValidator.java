package com.worth.ifs.file.transactional;

import com.worth.ifs.commons.error.CommonErrors;
import com.worth.ifs.commons.service.ServiceResult;
import org.springframework.http.MediaType;

import java.util.List;

import static com.worth.ifs.commons.error.CommonErrors.badRequestError;
import static com.worth.ifs.commons.error.CommonErrors.payloadTooLargeError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static com.worth.ifs.util.ParsingFunctions.validLongResult;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * A basic implementation of FileHttpHeadersValidator that, given a set of HTTP headers about the file being uploaded will be able to
 * validate their format and additionally ensure that the filesize and media type of the file conforms to a set of restrictions
 */
public class FilesizeAndTypeFileValidator implements FileHttpHeadersValidator {

    private long maxFilesizeBytes;
    private List<MediaType> validMediaTypes;

    public FilesizeAndTypeFileValidator(long maxFilesize, List<MediaType> validMediaTypes) {
        this.maxFilesizeBytes = maxFilesize;
        this.validMediaTypes = validMediaTypes;
    }

    @Override
    public ServiceResult<FileHeaderAttributes> validateFileHeaders(String contentTypeHeaderValue, String contentLengthValue, String originalFilenameValue) {

        ServiceResult<Long> contentLengthValidation = validContentLengthHeader(contentLengthValue);
        ServiceResult<MediaType> contentTypeValidation = validContentTypeHeader(contentTypeHeaderValue);
        ServiceResult<String> filenameValidation = validFilename(originalFilenameValue);

        return find(contentLengthValidation, contentTypeValidation, filenameValidation).andOnSuccess(
                (contentLength, contentType, filename) -> serviceSuccess(new FileHeaderAttributes(contentType, contentLength, filename)));
    }

    private ServiceResult<String> validFilename(String filename) {
        return checkParameterIsPresent(filename, "Please supply an original filename as a \"filename\" HTTP Request Parameter");
    }

    private ServiceResult<Long> validContentLengthHeader(String contentLengthHeader) {

        ServiceResult<Long> validLongValue = validLongResult(contentLengthHeader);

        if (validLongValue.isFailure()) {
            return serviceFailure(payloadTooLargeError(maxFilesizeBytes));
        }

        long length = validLongValue.getSuccessObject();

        if (length > maxFilesizeBytes) {
            return serviceFailure(payloadTooLargeError(maxFilesizeBytes));
        }

        return serviceSuccess(length);
    }

    private ServiceResult<MediaType> validContentTypeHeader(String contentTypeHeader) {

        if (isBlank(contentTypeHeader)) {
            return serviceFailure(CommonErrors.unsupportedMediaTypeError(validMediaTypes));
        }

        MediaType mediaType = MediaType.valueOf(contentTypeHeader);

        if (mediaType == null || !validMediaTypes.contains(mediaType)) {
            return serviceFailure(CommonErrors.unsupportedMediaTypeError(validMediaTypes));
        }

        return serviceSuccess(mediaType);
    }

    private ServiceResult<String> checkParameterIsPresent(String parameterValue, String failureMessage) {
        return !isBlank(parameterValue) ?  serviceSuccess(parameterValue) : serviceFailure(badRequestError(failureMessage));
    }
}
