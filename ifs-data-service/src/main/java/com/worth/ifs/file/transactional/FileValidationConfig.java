package com.worth.ifs.file.transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * TODO DW - document this class
 */
@Configuration
public class FileValidationConfig {

    @Value("${ifs.data.service.file.storage.fileinputresponse.max.filesize.bytes}")
    private Long maxFilesizeBytesForFormInputResponses;

    @Value("${ifs.data.service.file.storage.fileinputresponse.valid.media.types}")
    private List<String> validMediaTypesForFormInputResponses;

    @Value("${ifs.data.service.file.storage.applicationfinance.max.filesize.bytes}")
    private Long maxFilesizeBytesForApplicationFinance;

    @Value("${ifs.data.service.file.storage.applicationfinance.valid.media.types}")
    private List<String> validMediaTypesForApplicationFinance;

    @Bean(name = "formInputResponseFileValidator")
    public FileValidator getFormInputResponseFileValidator() {
        List<MediaType> mediaTypes = simpleMap(validMediaTypesForFormInputResponses, MediaType::valueOf);
        return new FileValidatorImpl(maxFilesizeBytesForFormInputResponses, mediaTypes);
    }

    @Bean(name = "applicationFinanceFileValidator")
    public FileValidator getApplicationFinanceFileValidator() {
        List<MediaType> mediaTypes = simpleMap(validMediaTypesForApplicationFinance, MediaType::valueOf);
        return new FileValidatorImpl(maxFilesizeBytesForApplicationFinance, mediaTypes);
    }
}
