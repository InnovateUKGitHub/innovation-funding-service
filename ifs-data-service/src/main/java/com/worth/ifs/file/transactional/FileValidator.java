package com.worth.ifs.file.transactional;

import com.worth.ifs.commons.service.ServiceResult;

/**
 * TODO DW - document this class
 */
public interface FileValidator {

    ServiceResult<FileHeaderAttributes> validateFileHeaders(String contentTypeHeaderValue, String contentLengthValue, String originalFilenameValue);
}
