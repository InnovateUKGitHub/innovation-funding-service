package com.worth.ifs.file.transactional;

import com.worth.ifs.commons.service.ServiceResult;

/**
 * Interface defining a component that, given some HTTP headers, is able to validate those headers and their values against some set of restrictions
 */
public interface FileHttpHeadersValidator {

    ServiceResult<FileHeaderAttributes> validateFileHeaders(String contentTypeHeaderValue, String contentLengthValue, String originalFilenameValue);
}
