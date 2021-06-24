package org.innovateuk.ifs.management.admin.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileUploadRestService;
import org.innovateuk.ifs.management.admin.form.UploadFilesForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class FileUploadControllerTest extends BaseControllerMockMVCTest<FileUploadController> {

    @Mock
    private FileUploadRestService fileUploadRestServiceMock;

    @Override
    protected FileUploadController supplyControllerUnderTest() {
        return new FileUploadController();
    }

    @Test
    public void uploadFiles() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/upload-files"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/upload-files"));
    }

    @Test
    public void postUploadFiles() throws Exception {
        String fileName = "original.csv";
        String fileContent = "My content!";
        MockMultipartFile file = new MockMultipartFile("file", fileName, "application/csv", fileContent.getBytes());

        UploadFilesForm expectedUserForm = new UploadFilesForm();
        expectedUserForm.setFileName(fileName);
        expectedUserForm.setFile(file);

        FileEntryResource fileEntryResource = newFileEntryResource().build();

        when(fileUploadRestServiceMock.uploadFile("AssessmentOnly", "application/csv", 11, fileName, fileContent.getBytes()))
                .thenReturn(RestResult.restSuccess(fileEntryResource));

        mockMvc.perform(
                multipart("/admin/upload-files")
                        .file(file)
                        .param("upload_file", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/upload-files"))
                .andReturn();

        verify(fileUploadRestServiceMock).uploadFile("AssessmentOnly", "application/csv", 11, fileName, fileContent.getBytes());
    }

    @Test
    public void postUploadFilesFails() throws Exception {
        String fileName = "original.csv";
        String fileContent = "My content!";
        MockMultipartFile file = new MockMultipartFile("file", fileName, "application/csv", fileContent.getBytes());

        UploadFilesForm expectedUserForm = new UploadFilesForm();
        expectedUserForm.setFileName(fileName);
        expectedUserForm.setFile(file);

        when(fileUploadRestServiceMock.uploadFile("AssessmentOnly", "application/csv", 11, fileName, fileContent.getBytes()))
                .thenReturn(RestResult.restFailure(HttpStatus.INTERNAL_SERVER_ERROR));

        mockMvc.perform(
                multipart("/admin/upload-files")
                        .file(file)
                        .param("upload_file", "true"))
                .andExpect(status().isInternalServerError());

        verify(fileUploadRestServiceMock).uploadFile("AssessmentOnly", "application/csv", 11, fileName, fileContent.getBytes());
    }
}
