package org.innovateuk.ifs.question.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputScope;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class QuestionFileSetupCompetitionServiceImplTest extends BaseServiceUnitTest<QuestionFileSetupCompetitionServiceImpl> {

    @Mock
    private FormInputRepository formInputRepository;

    @Mock
    private FileEntryService fileEntryServiceMock;

    @Override
    protected QuestionFileSetupCompetitionServiceImpl supplyServiceUnderTest() {
        return new QuestionFileSetupCompetitionServiceImpl();
    }

    @Test
    public void deleteTemplateFile_submitted() throws Exception {
        final long questionId = 1L;
        final long fileId = 101L;
        final FileEntry fileEntry = new FileEntry(fileId, "somefile.pdf", MediaType.APPLICATION_PDF, 1111L);
        FormInput formInput = newFormInput()
                .withFile(fileEntry)
                .build();

        when(formInputRepository.findByQuestionIdAndScopeAndType(questionId, FormInputScope.APPLICATION, FormInputType.TEMPLATE_DOCUMENT)).thenReturn(formInput);
        when(fileServiceMock.deleteFileIgnoreNotFound(fileId)).thenReturn(ServiceResult.serviceSuccess(fileEntry));

        ServiceResult<Void> response = service.deleteTemplateFile(questionId);

        assertTrue(response.isSuccess());
        assertNull(formInput.getFile());
    }
}