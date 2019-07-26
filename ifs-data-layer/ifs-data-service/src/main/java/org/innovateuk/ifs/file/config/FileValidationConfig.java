package org.innovateuk.ifs.file.config;

import org.innovateuk.ifs.file.service.ByFormInputMediaTypesGenerator;
import org.innovateuk.ifs.file.service.ByMediaTypeStringsMediaTypesGenerator;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Spring Configuration that is able to supply a number of {@link FilesizeAndTypeFileValidator} for the use of various
 * Controllers in order to ensure that files being uploaded for particular contexts (e.g. a file upload for an
 * Application Form question) are valid for those contexts
 */
@Configuration
public class FileValidationConfig {

    @Bean(name = "formInputFileValidator")
    public FilesizeAndTypeFileValidator<Long> formInputFileValidator(ByFormInputMediaTypesGenerator mediaTypesGenerator) {
        return new FilesizeAndTypeFileValidator<>(mediaTypesGenerator);
    }

    @Bean(name = "mediaTypeStringsFileValidator")
    public FilesizeAndTypeFileValidator<List<String>> mediaTypeStringsFileValidator() {
        return new FilesizeAndTypeFileValidator<>(new ByMediaTypeStringsMediaTypesGenerator());
    }
}
