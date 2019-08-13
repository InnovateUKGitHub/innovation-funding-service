package org.innovateuk.ifs.question.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service for uploading template files for {@link FormInput}.
 */
@Service
@Transactional
public class QuestionFileSetupCompetitionServiceImpl implements QuestionFileSetupCompetitionService {

    @Value("${ifs.data.service.file.storage.form.input.template.max.filesize.bytes}")
    private Long maxFileSize;

    @Value("${ifs.data.service.file.storage.form.input.template.valid.media.types}")
    private List<String> validMediaTypes;

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    @Qualifier("mediaTypeStringsFileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidator;

    private FileControllerUtils fileControllerUtils = new FileControllerUtils();

    @Override
    @Transactional
    public ServiceResult<Void> uploadTemplateFile(String contentType, String contentLength, String originalFilename, long questionId, HttpServletRequest request) {
        return findFormInputByQuestionId(questionId).andOnSuccess(formInput ->
        fileControllerUtils.handleFileUpload(contentType, contentLength, originalFilename, fileValidator, validMediaTypes, maxFileSize, request,
                (fileAttributes, inputStreamSupplier) -> fileService.createFile(fileAttributes.toFileEntryResource(), inputStreamSupplier)
                        .andOnSuccessReturnVoid(created -> {
                            formInput.setFile(created.getValue());
                        })).toServiceResult());
    }

    @Override
    @Transactional
    public ServiceResult<Void> deleteTemplateFile(long questionId) {
        return findFormInputByQuestionId(questionId).andOnSuccess(formInput -> {
            long fileId = formInput.getFile().getId();
            return fileService.deleteFileIgnoreNotFound(fileId).andOnSuccessReturnVoid(() -> {
                formInput.setFile(null);
            });
        });
    }

    private ServiceResult<FormInput> findFormInputByQuestionId(long questionId) {
        return find(ofNullable(formInputRepository.findByQuestionIdAndScopeAndType(questionId,
                FormInputScope.APPLICATION,
                FormInputType.TEMPLATE_DOCUMENT)), notFoundError(FormInput.class, questionId));
    }
}