package org.innovateuk.ifs.application.forms.saver;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.REMOVE_UPLOADED_FILE;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Tests {@link ApplicationQuestionFileSaver}
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class ApplicationQuestionFileSaverTest {

    @InjectMocks
    private ApplicationQuestionFileSaver fileSaver;

    @Mock
    private FormInputResponseRestService formInputResponseRestService;

    @Mock
    private FormInputRestService formInputRestService;

    private Long applicationId = 509230L;
    private Long processRoleId = 232425L;
    private Long questionId = 92912L;
    private List<QuestionResource> questions = newQuestionResource().withId(questionId).build(1);

    @Test
    public void saveFileUploadQuestionsIfAny_notFileUpload() {
        
        final Map<String, String[]> params = emptyMap();
        final HttpServletRequest normalRequest = mock(HttpServletRequest.class);

        when(formInputRestService.getByQuestionIdAndScope(questionId, APPLICATION)).thenReturn(restSuccess(newFormInputResource().withType(FormInputType.FILEUPLOAD).build(1)));

        ValidationMessages result = fileSaver.saveFileUploadQuestionsIfAny(questions, params, normalRequest, applicationId, processRoleId);

        assertFalse(result.hasErrors());
    }

    @Test
    public void saveFileUploadQuestionsIfAny_UploadFile() {
        
        final Map<String, String[]> params = emptyMap();
        final MultipartHttpServletRequest uploadRequest = mock(MultipartHttpServletRequest.class);
        when(uploadRequest.getFileMap()).thenReturn(asMap("formInput[1000]", new MockMultipartFile("upload.pdf", new byte[]{1})));

        when(formInputRestService.getByQuestionIdAndScope(questionId, APPLICATION)).thenReturn(restSuccess(newFormInputResource()
                .withId(1000L)
                .withType(FormInputType.FILEUPLOAD)
                .build(1)));
        when(formInputResponseRestService.createFileEntry(anyLong(), anyLong(), anyLong(), nullable(String.class), anyLong(), nullable(String.class), any(byte[].class))).thenReturn(restSuccess(newFileEntryResource().build()));

        ValidationMessages result = fileSaver.saveFileUploadQuestionsIfAny(questions, params, uploadRequest, applicationId, processRoleId);

        assertFalse(result.hasErrors());
        verify(formInputResponseRestService, times(1)).createFileEntry(anyLong(), anyLong(), anyLong(), nullable(String.class), anyLong(), anyString(), any(byte[].class));
    }

    @Test
    public void saveFileUploadQuestionsIfAny_UploadFileWithFailure() {
        
        final Map<String, String[]> params = emptyMap();
        final MultipartHttpServletRequest uploadRequest = mock(MultipartHttpServletRequest.class);
        when(uploadRequest.getFileMap()).thenReturn(asMap("formInput[1000]", new MockMultipartFile("upload.pdf", new byte[]{1})));

        when(formInputRestService.getByQuestionIdAndScope(questionId, APPLICATION)).thenReturn(restSuccess(newFormInputResource()
                .withId(1000L)
                .withType(FormInputType.FILEUPLOAD)
                .build(1)));
        when(formInputResponseRestService.createFileEntry(anyLong(), anyLong(), anyLong(), nullable(String.class), anyLong(), anyString(), any(byte[].class)))
                .thenReturn(RestResult.restFailure(new Error("Something went wrong", HttpStatus.BAD_REQUEST)));

        ValidationMessages result = fileSaver.saveFileUploadQuestionsIfAny(questions, params, uploadRequest, applicationId, processRoleId);

        assertTrue(result.hasErrors());
        verify(formInputResponseRestService, times(1)).createFileEntry(anyLong(), anyLong(), anyLong(), nullable(String.class), anyLong(), anyString(), any(byte[].class));
    }

    @Test
    public void saveFileUploadQuestionsIfAny_UploadFileNotProvided() {
        
        final Map<String, String[]> params = emptyMap();
        final MultipartHttpServletRequest uploadRequest = mock(MultipartHttpServletRequest.class);
        when(uploadRequest.getFileMap()).thenReturn(emptyMap());

        when(formInputRestService.getByQuestionIdAndScope(questionId, APPLICATION)).thenReturn(restSuccess(newFormInputResource()
                .withId(1000L)
                .withType(FormInputType.FILEUPLOAD)
                .build(1)));

        ValidationMessages result = fileSaver.saveFileUploadQuestionsIfAny(questions, params, uploadRequest, applicationId, processRoleId);

        assertFalse(result.hasErrors());
        verify(formInputResponseRestService, times(0)).createFileEntry(anyLong(), anyLong(), anyLong(), anyString(), anyLong(), anyString(), any(byte[].class));
    }

    @Test
    public void saveFileUploadQuestionsIfAny_RemoveFile() {
        
        final Map<String, String[]> params = asMap(REMOVE_UPLOADED_FILE, new String[]{"something"});
        final MultipartHttpServletRequest uploadRequest = mock(MultipartHttpServletRequest.class);

        when(formInputRestService.getByQuestionIdAndScope(questionId, APPLICATION)).thenReturn(restSuccess(newFormInputResource()
                .withId(1000L)
                .withType(FormInputType.FILEUPLOAD)
                .build(1)));
        when(formInputResponseRestService.removeFileEntry(1000L, applicationId, processRoleId)).thenReturn(restSuccess());

        ValidationMessages result = fileSaver.saveFileUploadQuestionsIfAny(questions, params, uploadRequest, applicationId, processRoleId);

        assertFalse(result.hasErrors());
        verify(formInputResponseRestService, times(1)).removeFileEntry(1000L, applicationId, processRoleId);
    }
}
