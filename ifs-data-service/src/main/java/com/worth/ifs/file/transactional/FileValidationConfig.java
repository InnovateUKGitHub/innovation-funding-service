package com.worth.ifs.file.transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * Spring Configuration that is able to supply a number of FileValidators for the use of various Controllers in order
 * to ensure that files being uploaded for particular contexts (e.g. a file upload for an com.worth.ifs.Application Form question)
 * are valid for those contexts
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

    @Value("${ifs.data.service.file.storage.assessorfeedback.max.filesize.bytes}")
    private Long maxFilesizeBytesForAssessorFeedback;

    @Value("${ifs.data.service.file.storage.assessorfeedback.valid.media.types}")
    private List<String> validMediaTypesForAssessorFeedback;

    @Value("${ifs.data.service.file.storage.projectsetupotherdocuments.max.filesize.bytes}")
    private Long maxFilesizeBytesForProjectSetupOtherDocuments;

    @Value("${ifs.data.service.file.storage.projectsetupotherdocuments.valid.media.types}")
    private List<String> validMediaTypesForProjectSetupOtherDocuments;

    @Value("${ifs.data.service.file.storage.projectsetupgrantofferletter.max.filesize.bytes}")
    private Long maxFilesizeBytesForProjectSetupGrantOfferLetter;

    @Value("${ifs.data.service.file.storage.projectsetupgrantofferletter.valid.media.types}")
    private List<String> validMediaTypesForProjectSetupGrantOfferLetter;


    @Bean(name = "formInputResponseFileValidator")
    public FileHttpHeadersValidator getFormInputResponseFileValidator() {
        return createFileValidator(validMediaTypesForFormInputResponses, maxFilesizeBytesForFormInputResponses);
    }

    @Bean(name = "applicationFinanceFileValidator")
    public FileHttpHeadersValidator getApplicationFinanceFileValidator() {
        return createFileValidator(validMediaTypesForApplicationFinance, maxFilesizeBytesForApplicationFinance);
    }

    @Bean(name = "assessorFeedbackFileValidator")
    public FileHttpHeadersValidator getAssessorFeedbackFileValidator() {
        return createFileValidator(validMediaTypesForAssessorFeedback, maxFilesizeBytesForAssessorFeedback);
    }

    @Bean(name = "projectSetupOtherDocumentsFileValidator")
    public FileHttpHeadersValidator getProjectSetupOtherDocumentsFileValidator() {
        return createFileValidator(validMediaTypesForProjectSetupOtherDocuments, maxFilesizeBytesForProjectSetupOtherDocuments);
    }

    @Bean(name = "projectSetupGrantOfferLetterFileValidator")
    public FileHttpHeadersValidator getProjectSetupGrantOfferLetterFileValidator() {
        return createFileValidator(validMediaTypesForProjectSetupGrantOfferLetter, maxFilesizeBytesForProjectSetupGrantOfferLetter);
    }


    private FileHttpHeadersValidator createFileValidator(List<String> validMediaTypes, Long maxFilesizeBytes) {
        List<MediaType> mediaTypes = simpleMap(validMediaTypes, MediaType::valueOf);
        return new FilesizeAndTypeFileValidator(maxFilesizeBytes, mediaTypes);
    }
}
