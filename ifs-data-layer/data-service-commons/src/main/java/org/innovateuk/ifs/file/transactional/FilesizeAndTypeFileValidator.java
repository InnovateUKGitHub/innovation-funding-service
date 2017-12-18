package org.innovateuk.ifs.file.transactional;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.http.MediaType;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.innovateuk.ifs.commons.error.CommonErrors.payloadTooLargeError;
import static org.innovateuk.ifs.commons.error.CommonErrors.unsupportedMediaTypeError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.FILES_NO_NAME_PROVIDED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.ParsingFunctions.validLongResult;

/**
 * A basic implementation of FilesizeAndTypeFileValidator that, given a set of HTTP headers about the file being uploaded will be able to
 * validate their format and additionally ensure that the filesize and media type of the file conforms to a set of restrictions
 */
public class FilesizeAndTypeFileValidator<T> {

    private MediaTypesGenerator<T> validMediaTypesGenerator;

    public FilesizeAndTypeFileValidator(MediaTypesGenerator<T> validMediaTypesGenerator) {
        this.validMediaTypesGenerator = validMediaTypesGenerator;
    }

    public ServiceResult<FileHeaderAttributes> validateFileHeaders(String contentTypeHeaderValue, String contentLengthValue, String originalFilenameValue, T validMediaTypesContext, long maxFilesizeBytes) {

        ServiceResult<Long> contentLengthValidation = validContentLengthHeader(contentLengthValue, maxFilesizeBytes);
        ServiceResult<MediaType> contentTypeValidation = validContentTypeHeader(contentTypeHeaderValue, validMediaTypesContext);
        ServiceResult<String> filenameValidation = validFilename(originalFilenameValue);

        return find(contentLengthValidation, contentTypeValidation, filenameValidation).andOnSuccess(
                (contentLength, contentType, filename) -> serviceSuccess(new FileHeaderAttributes(contentType, contentLength, filename)));
    }

    private ServiceResult<String> validFilename(String filename) {
        return checkParameterIsPresent(filename, new Error(FILES_NO_NAME_PROVIDED));
    }

    private ServiceResult<Long> validContentLengthHeader(String contentLengthHeader, long maxFilesizeBytes) {

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

    private ServiceResult<MediaType> validContentTypeHeader(String contentTypeHeader, T validMediaTypesContext) {

        List<MediaType> validMediaTypes = validMediaTypesGenerator.apply(validMediaTypesContext);

        if (isBlank(contentTypeHeader)) {
            return serviceFailure(unsupportedMediaTypeError(validMediaTypes));
        }

        MediaType mediaType = MediaType.valueOf(contentTypeHeader);

        if (!validMediaTypes.contains(mediaType)) {
            return serviceFailure(unsupportedMediaTypeError(validMediaTypes));
        }

        return serviceSuccess(mediaType);
    }

    private ServiceResult<String> checkParameterIsPresent(String parameterValue, Error error) {
        return !isBlank(parameterValue) ?  serviceSuccess(parameterValue) : serviceFailure(error);
    }
}
